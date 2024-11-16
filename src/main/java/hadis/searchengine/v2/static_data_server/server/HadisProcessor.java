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
        return singleProcess(data);
    }

    private Map<?, ?> batchProcess(Map<String, String> data) {
        long totalSuccessTime = System.currentTimeMillis();
        long totalFailTime = System.currentTimeMillis();
        long id = 1;
        List<Note> notes = new ArrayList<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            long time = System.currentTimeMillis();
            Note note = generateNote(id++, entry.getKey(), entry.getValue());
            notes.add(note);
        }
        var results = noteCrudService.batchCreateNotes(notes);
        totalSuccessTime = System.currentTimeMillis() - totalSuccessTime;
        totalFailTime = System.currentTimeMillis() - totalFailTime;
        return Map.of(
                "totalSuccessTime", totalSuccessTime,
                "totalFailTime", totalFailTime,
                "totalTime", totalSuccessTime + totalFailTime,
                "results", results
        );
    }

    private Map<?, ?> singleProcess(Map<String, String> data) {
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
                LOGGER.info("Time taken: {}, Successfully created note: {}, ID: {}", st, note.getTag(), result.getNoteId());
                results.put(note.getTag(), Map.of(
                        "status", "success",
                        "noteId", note.getNoteId(),
                        "timeTaken", st
                ));
            } catch (Exception e) {
                long ft = System.currentTimeMillis() - time;
                failCount++;
                totalFailTime += ft;
                LOGGER.error("Time taken: {}, Failed to create note: {}", ft, note.getTag());
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
                .keywords(generateKeywords(key, value))
                .build();
    }

    private @NonNull Map<String, Object> generateKeywords(String key, String value) {
        Map<String, Object> keywords = new HashMap<>();
        keywords.put("breadCrumb-en", getBreadCrumbEn(key));
        keywords.put("breadCrumb-bn", getBreadCrumbBn(key));
        return keywords;
    }

    private @NonNull List<String> getBreadCrumbBn(String key) {
        String book = switch (key.substring(0, key.indexOf("-"))) {
            case "BUK" -> "সহীহ বুখারী";
            case "MUS" -> "সহীহ মুসলিম";
            case "TIR" -> "সুনানে আল-তিরমিজী";
            case "MAJ" -> "সুনানে ইবনে মাজাহ";
            case "DAU" -> "সুনানে আবু দাউদ";
            case "NAS" -> "সুনানে নাসাই";
            default -> "অন্যান্য";
        };
        String hadisNo = getBnNumber(key.substring(key.indexOf("-") + 1));
        return List.of(book, hadisNo);
    }

    Map<String, String> bengaliNumbers = Map.of("0", "০", "1", "১",
            "2", "২", "3", "৩", "4", "৪", "5", "৫",
            "6", "৬", "7", "৭", "8", "৮", "9", "৯"
    );
    private String getBnNumber(String engNum) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < engNum.length(); i++) {
            sb.append(bengaliNumbers.get(engNum.charAt(i) + ""));
        }
        return sb.toString();
    }

    private @NonNull List<String> getBreadCrumbEn(String key) {
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
