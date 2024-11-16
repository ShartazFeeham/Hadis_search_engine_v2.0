package hadis.searchengine.v2.search_server.repository;

import org.springframework.data.elasticsearch.core.query.StringQuery;

import static hadis.searchengine.v2.Constants.CONTENT_FIELD;

public class ElasticQuery {

    public static class SearchQuery {
        private static final String INPUT_PLACEHOLDER = "$$$";

        public static final String QUERY_ON_SINGLE_FIELD_WITH_MUST = """
                {
                  "bool": {
                      "must": {"match": {"textOriginal": "$$$"}}
                    }
                }
            """;
        public static final String QUERY_ON_SINGLE_FIELD_WITH_SHOULD = """
                {
                  "bool": {
                      "should": {"match": {"textOriginal": "$$$"}}
                    }
                }
            """;

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

        public static final String QUERY_ON_SINGLE_FIELD_WITH_FUZZY_2 = """
                {
                 "match": {
                   "textOriginal": {
                     "query": "$$$",
                     "fuzziness": 2
                   }
                 }
                }
            """;

        public static StringQuery getElasticQuery(String queryTemplate, String inputText) {
            return new StringQuery(queryTemplate.replace(INPUT_PLACEHOLDER, inputText));
        }
    }

    public static class NativeQuery {
        public static org.springframework.data.elasticsearch.client.elc.NativeQuery queryOnSingleFieldWithMust(String inputText) {
            return org.springframework.data.elasticsearch.client.elc.NativeQuery.builder()
                    .withQuery(query -> query
                            .bool(bool -> bool
                                    .must(must -> must
                                            .match(match -> match
                                                    .field(CONTENT_FIELD)
                                            )
                                    )
                            )
                    ).build();
        }

        public static org.springframework.data.elasticsearch.client.elc.NativeQuery queryOnSingleFieldWithShould(String inputText) {
            return org.springframework.data.elasticsearch.client.elc.NativeQuery.builder()
                    .withQuery(query -> query
                            .bool(bool -> bool
                                    .should(should -> should
                                            .match(match -> match
                                                    .field(CONTENT_FIELD)
                                            )
                                    )
                            )
                    ).build();
        }

        public static org.springframework.data.elasticsearch.client.elc.NativeQuery queryOnSingleFieldWithFuzzy1(String inputText) {
            return org.springframework.data.elasticsearch.client.elc.NativeQuery.builder()
                    .withQuery(query -> query
                            .fuzzy(fuzzy -> fuzzy
                                    .field(CONTENT_FIELD)
                                    .value(inputText)
                                    .fuzziness("1")
                            )
                    ).build();
        }

        public static org.springframework.data.elasticsearch.client.elc.NativeQuery queryOnSingleFieldWithFuzzy2(String inputText) {
            return org.springframework.data.elasticsearch.client.elc.NativeQuery.builder()
                    .withQuery(query -> query
                            .fuzzy(fuzzy -> fuzzy
                                    .field(CONTENT_FIELD)
                                    .value(inputText)
                                    .fuzziness("2")
                            )
                    ).build();
        }
    }
}
