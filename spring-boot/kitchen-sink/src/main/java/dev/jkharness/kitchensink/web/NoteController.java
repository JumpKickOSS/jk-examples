package dev.jkharness.kitchensink.web;

import dev.jkharness.kitchensink.domain.Note;
import dev.jkharness.kitchensink.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    public record NewNote(
            @NotBlank @Size(max = 120) String title,
            @Size(max = 4000) String body) {}

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Note> create(@Valid @RequestBody NewNote request) {
        Note saved = service.create(request.title(), request.body());
        return ResponseEntity.created(URI.create("/api/notes/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Note> search(@RequestParam(required = false) String q) {
        return service.search(q);
    }
}
