package dev.jkexamples.talks;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

/** A conference talk, an active-record Panache entity; the database assigns its id. */
@Entity
public class Talk extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String title;
    public String speaker;
    public String track;
    public String room;

    @Column(name = "starts_at")
    public LocalDateTime startsAt;

    public int minutes;

    public Slot slot() {
        return new Slot(room, startsAt, minutes);
    }

    public static List<Talk> inTrack(String track) {
        return list("track = ?1 order by startsAt", track);
    }

    public static List<Talk> schedule() {
        return listAll(Sort.by("startsAt"));
    }
}
