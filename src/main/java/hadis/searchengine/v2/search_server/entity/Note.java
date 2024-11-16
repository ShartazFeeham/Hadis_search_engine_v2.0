package hadis.searchengine.v2.search_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static hadis.searchengine.v2.Constants.INDEX_NAME;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = INDEX_NAME)
public class Note {
    @Id
    @NonNull private Long noteId;
    @NonNull private List<String> sourceBreadCrumb;
    @NonNull private Integer length;
    @NonNull private Integer characterCount;
    @NonNull private Integer uniqueCharacterCount;
    @NonNull private Integer wordCount;
    @NonNull private Integer uniqueWordCount;
    @NonNull private Integer blocks;
    @NonNull private String textOriginal;

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

