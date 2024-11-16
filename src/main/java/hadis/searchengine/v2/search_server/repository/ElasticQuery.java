package hadis.searchengine.v2.search_server.repository;

import co.elastic.clients.elasticsearch.core.search.Suggester;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.time.Duration;
import java.util.List;

import static hadis.searchengine.v2.utils.Constants.*;

public class ElasticQuery {

    public static class SearchQuery {
        private static final String INPUT_PLACEHOLDER = "$$$";

        public static final String QUERY_ON_SINGLE_FIELD_WITH_FUZZY_1 = """
                {
                 "match": {
                   "textOriginal": {
                     "query": "$$$",
                     "fuzziness": 1
                   }
                 }
               }
            """;

        public static StringQuery getElasticQuery(String queryTemplate, String inputText) {
            return new StringQuery(queryTemplate.replace(INPUT_PLACEHOLDER, inputText));
        }
    }

    public static class Native {

        /**
         * Constructs a NativeQuery using the specified search parameters, including pagination.
         *
         * @param inputText The search text or query.
         * @param pageNumber The expected page number of the search results.
         * @param pageSize The expected page size of the search results.
         * @param fuzziness The level of fuzziness for the search (0 = exact, higher values for more fuzziness).
         * @param fields The list of fields to search in.
         * @return A NativeQuery constructed with the specified parameters.
         * @throws IllegalArgumentException If the fields list is null or empty.
         */
        public static NativeQuery searchQuery(String inputText, int pageNumber, int pageSize,
                                              int fuzziness, List<String> fields) {

            if (fields == null || fields.isEmpty()) {
                throw new IllegalArgumentException("Fields list cannot be null or empty.");
            }

            return NativeQuery.builder()
                    .withQuery(query -> query
                            .multiMatch(multiMatch -> multiMatch
                                    .query(inputText)
                                    .fields(fields)
                                    .fuzziness(String.valueOf(fuzziness))
                            )
                    )
                    .withExplain(false)
                    .withPageable(PageRequest.of(pageNumber, pageSize))
                    .withTimeout(Duration.ofMillis(10_000))
                    .build();
        }

    }
}
