package dev.jkexamples.hotel.service.internal;

import static dev.jkexamples.hotel.service.db.Tables.HOTEL;
import static dev.jkexamples.hotel.service.db.Tables.STAFF_HOTEL;
import static dev.jkexamples.hotel.service.db.Tables.STAFF_USER;

import dev.jkexamples.hotel.contract.StaffUser;
import dev.jkexamples.hotel.service.NotFoundException;
import dev.jkexamples.hotel.service.StaffAccounts;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Staff accounts and the hotels each may work. */
@Service
@Transactional(readOnly = true)
class StaffQueries implements StaffAccounts {

    private final DSLContext db;

    StaffQueries(DSLContext db) {
        this.db = db;
    }

    @Override
    public Optional<Credentials> credentials(String username) {
        return db.select(STAFF_USER.USERNAME, STAFF_USER.PASSWORD_HASH)
                .from(STAFF_USER)
                .where(STAFF_USER.USERNAME.eq(username))
                .fetchOptional(r -> new Credentials(r.value1(), r.value2()));
    }

    @Override
    public StaffUser user(String username) {
        String name = db.select(STAFF_USER.DISPLAY_NAME)
                .from(STAFF_USER)
                .where(STAFF_USER.USERNAME.eq(username))
                .fetchOptional(STAFF_USER.DISPLAY_NAME)
                .orElseThrow(() -> new NotFoundException("no staff user " + username));
        return new StaffUser(username, name, hotels(username));
    }

    /** The slugs of the hotels {@code username} may work, in name order. */
    List<String> hotels(String username) {
        return db.select(HOTEL.SLUG)
                .from(HOTEL)
                .join(STAFF_HOTEL)
                .on(STAFF_HOTEL.HOTEL_ID.eq(HOTEL.ID))
                .join(STAFF_USER)
                .on(STAFF_USER.ID.eq(STAFF_HOTEL.STAFF_ID))
                .where(STAFF_USER.USERNAME.eq(username))
                .orderBy(HOTEL.NAME)
                .fetch(HOTEL.SLUG);
    }
}
