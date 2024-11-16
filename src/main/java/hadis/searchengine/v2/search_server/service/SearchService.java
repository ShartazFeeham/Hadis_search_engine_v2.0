package hadis.searchengine.v2.search_server.service;

import hadis.searchengine.v2.search_server.entity.Note;
import hadis.searchengine.v2.search_server.repository.ElasticQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.Iterator;

import static hadis.searchengine.v2.search_server.repository.ElasticQuery.SearchQuery.*;

@Service
public class SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    public SearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Iterator<SearchHit<Note>> search(String inputText) {
        // With fuzzy ness 1
        StringQuery stringQuery = ElasticQuery.SearchQuery.getElasticQuery(QUERY_ON_SINGLE_FIELD_WITH_FUZZY_1, inputText);
        var stringResult = elasticsearchOperations.search(stringQuery, Note.class);
        LOGGER.info("Input text: {}, Search query: {}, Search result: {}", inputText, stringQuery, stringResult);

        // WIth fuzzy ness 2
        NativeQuery nativeQuery = ElasticQuery.NativeQuery.queryOnSingleFieldWithFuzzy2(inputText);
        var nativeResult = elasticsearchOperations.search(nativeQuery, Note.class);
        LOGGER.info("Input text: {}, Search query: {}, Search result: {}", inputText, nativeQuery, nativeResult);

        return nativeResult.stream().iterator();
    }
}
