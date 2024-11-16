package hadis.searchengine.v2.static_data_server.server;

import hadis.searchengine.v2.search_server.entity.Note;
import hadis.searchengine.v2.search_server.service.NoteCrudService;
import hadis.searchengine.v2.static_data_server.repository.StaticRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service @RequiredArgsConstructor
public class HadisProcessor {

    private final StaticRepo staticRepo;
    private final NoteCrudService noteCrudService;
    private static final Logger LOGGER = LoggerFactory.getLogger(HadisProcessor.class);

    public Map<?, ?> process() {
        Map<String, String> data = staticRepo.getHadisMapJsonAsObject();
        long id = 1;
        Map<String, Map> results = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        long totalSuccessTime = 0;
        long totalFailTime = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            long time = System.currentTimeMillis();
            Note note = generateNote(id++, entry.getKey(), entry.getValue());
            try {
                var result = noteCrudService.createNote(note);
                long st = System.currentTimeMillis() - time;
                successCount++;
                totalSuccessTime += st;
                LOGGER.info("Created note: {}, time taken: {}", result, st);
                results.put(note.getTag(), Map.of(
                        "status", "success",
                        "noteId", note.getNoteId(),
                        "timeTaken", st
                ));
            } catch (Exception e) {
                long ft = System.currentTimeMillis() - time;
                failCount++;
                totalFailTime += ft;
                LOGGER.error("Failed to create note: {}, time taken: {}", note, ft);
                results.put(note.getTag(), Map.of(
                        "status", "failed",
                        "noteId", note.getNoteId(),
                        "timeTaken", ft
                ));
            }

        }
        return Map.of(
                "successCount", successCount,
                "totalSuccessTime", totalSuccessTime,
                "totalFailTime", totalFailTime,
                "totalTime", totalSuccessTime + totalFailTime,
                "failCount", failCount,
                "results", results
        );
    }

    private Note generateNote(long id, String key, String value) {
        return Note.builder()
                .noteId(id)
                .tag(key)
                .characterCount(value.length())
                .uniqueCharacterCount(new HashSet<>(List.of(value.toCharArray())).size())
                .wordCount((int) Arrays.stream(value.split(" ")).count())
                .uniqueWordCount(new HashSet<>(Arrays.stream(value.split(" ")).toList()).size())
                .textOriginal(value)
                .sourceBreadCrumb(getBreadCrumb(key))
                .build();
    }

    private @NonNull List<String> getBreadCrumb(String key) {
        String book = switch (key.substring(0, key.indexOf("-"))) {
            case "BUK" -> "Sahih al-Bukhari";
            case "MUS" -> "Sahih Muslim";
            case "TIR" -> "Tirmidhi";
            case "MAJ" -> "Sunan Ibn Majah";
            case "DAU" -> "Sunan Abi Dawud";
            case "NAS" -> "Sunan an-Nasa'i";
            default -> "Other";
        };
        String hadisNo = key.substring(key.indexOf("-") + 1);
        return List.of(book, hadisNo);
    }
}
