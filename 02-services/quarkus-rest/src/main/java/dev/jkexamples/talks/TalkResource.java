package dev.jkexamples.talks;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/** {@code /talks}: the schedule, one track of it, scheduling (no clash in the room) and cancelling a talk. */
@Path("/talks")
public class TalkResource {

    @GET
    public List<Talk> schedule(@QueryParam("track") String track) {
        return track == null || track.isBlank() ? Talk.schedule() : Talk.inTrack(track);
    }

    @GET
    @Path("/{id}")
    public Talk one(@PathParam("id") long id) {
        Talk talk = Talk.findById(id);
        if (talk == null) throw new NotFoundException("no talk " + id);
        return talk;
    }

    @POST
    @Transactional
    public Response schedule(@Valid TalkRequest request) {
        Slot wanted = new Slot(request.room(), request.startsAt(), request.minutes());
        for (Talk other : Talk.<Talk>list("room", request.room())) {
            if (other.slot().clashesWith(wanted)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("\"" + request.room() + " is booked for '" + other.title + "'\"")
                        .build();
            }
        }
        Talk talk = new Talk();
        talk.title = request.title();
        talk.speaker = request.speaker();
        talk.track = request.track();
        talk.room = request.room();
        talk.startsAt = request.startsAt();
        talk.minutes = request.minutes();
        talk.persist();
        return Response.created(URI.create("/talks/" + talk.id)).entity(talk).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void cancel(@PathParam("id") long id) {
        if (!Talk.deleteById(id)) throw new NotFoundException("no talk " + id);
    }
}
