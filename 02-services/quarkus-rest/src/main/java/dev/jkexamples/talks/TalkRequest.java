package dev.jkexamples.talks;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** What a client sends to schedule a talk. */
public record TalkRequest(
        @NotBlank String title,
        @NotBlank String speaker,
        @NotBlank String track,
        @NotBlank String room,
        @NotNull LocalDateTime startsAt,
        @Min(5) @Max(240) int minutes) {}
