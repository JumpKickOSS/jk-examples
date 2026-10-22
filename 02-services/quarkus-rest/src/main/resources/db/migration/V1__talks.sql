CREATE TABLE talk (
    id        BIGSERIAL PRIMARY KEY,
    title     VARCHAR(200) NOT NULL,
    speaker   VARCHAR(120) NOT NULL,
    track     VARCHAR(40)  NOT NULL,
    room      VARCHAR(40)  NOT NULL,
    starts_at TIMESTAMP    NOT NULL,
    minutes   INTEGER      NOT NULL CHECK (minutes BETWEEN 5 AND 240)
);

CREATE INDEX talk_track ON talk (track);

INSERT INTO talk (title, speaker, track, room, starts_at, minutes) VALUES
('Lockfiles are law', 'Ada Abbott', 'build', 'Harbour', '2026-11-12 09:30:00', 45),
('Virtual threads in production', 'Bruno Brandt', 'jvm', 'Vine', '2026-11-12 10:30:00', 45),
('Native images without tears', 'Celia Castillo', 'jvm', 'Harbour', '2026-11-12 11:30:00', 30),
('Guards: architecture as a build step', 'Dmitri Dubois', 'build', 'Summit', '2026-11-12 14:00:00', 45);
