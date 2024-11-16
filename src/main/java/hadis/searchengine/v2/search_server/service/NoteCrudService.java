package hadis.searchengine.v2.search_server.service;

import hadis.searchengine.v2.search_server.entity.Note;
import hadis.searchengine.v2.search_server.repository.NoteRepository;
import org.springframework.stereotype.Service;

@Service
public class NoteCrudService {

    private final NoteRepository noteRepository;

    public NoteCrudService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    public Note readtNote(long noteId) {
        return noteRepository.findById(noteId).orElse(null);
    }

    public Note updateNote(long noteId, Note note) {
        var Note = readtNote(noteId);
        if (Note == null) {
            return null;
        }
        note.setNoteId(noteId);
        return noteRepository.save(note);
    }

    public void deleteNote(long noteId) {
        noteRepository.deleteById(noteId);
    }

    public Iterable<Note> getALl() {
        return noteRepository.findAll();
    }
}
