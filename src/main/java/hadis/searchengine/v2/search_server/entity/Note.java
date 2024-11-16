package hadis.searchengine.v2.search_server.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static hadis.searchengine.v2.utils.Constants.INDEX_NAME;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = INDEX_NAME)
@Builder
public class Note {
    @Id
    @NonNull private Long noteId;
    @NonNull private String tag;
    @NonNull private Integer characterCount;
    @NonNull private Integer uniqueCharacterCount;
    @NonNull private Integer wordCount;
    @NonNull private Integer uniqueWordCount;
    @NonNull private String textOriginal;
    @NonNull private List<String> sourceBreadCrumb;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Set<String> thisWords = Arrays.stream(
                textOriginal.split(" ")
        ).collect(Collectors.toSet());

        Set<String> oWords = Arrays.stream(
                ((Note) o)
                        .getTextOriginal()
                        .split(" ")
        ).collect(Collectors.toSet());

        return thisWords.size() == oWords.size() && thisWords.containsAll(oWords) && oWords.containsAll(thisWords);
    }
}

