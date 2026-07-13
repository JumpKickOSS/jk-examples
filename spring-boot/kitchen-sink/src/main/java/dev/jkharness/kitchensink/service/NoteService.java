package dev.jkharness.kitchensink.service;

import dev.jkharness.kitchensink.domain.Note;
import dev.jkharness.kitchensink.domain.NoteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {

    private final NoteRepository notes;

    public NoteService(NoteRepository notes) {
        this.notes = notes;
    }

    @Transactional
    public Note create(String title, String body) {
        return notes.save(new Note(title, body));
    }

    @Transactional(readOnly = true)
    public List<Note> search(String fragment) {
        return fragment == null || fragment.isBlank()
                ? notes.findAll()
                : notes.findByTitleContainingIgnoreCase(fragment);
    }
}
