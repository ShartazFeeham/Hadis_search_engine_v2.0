package hadis.searchengine.v2.search_server.repository;

import hadis.searchengine.v2.search_server.entity.Note;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NoteRepository extends ElasticsearchRepository<Note, Long> {
    public Note findByTag(String tag);
}
