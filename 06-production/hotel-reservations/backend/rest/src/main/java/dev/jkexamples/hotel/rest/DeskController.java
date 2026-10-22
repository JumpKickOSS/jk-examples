package dev.jkexamples.hotel.rest;

import dev.jkexamples.hotel.contract.DeskDay;
import dev.jkexamples.hotel.contract.DeskReservation;
import dev.jkexamples.hotel.contract.OccupancyPoint;
import dev.jkexamples.hotel.contract.StaffUser;
import dev.jkexamples.hotel.service.FrontDesk;
import dev.jkexamples.hotel.service.StaffAccounts;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** The front desk. Every route requires a signed-in staff member ({@link SecurityConfiguration}). */
@RestController
@RequestMapping("/api/desk")
class DeskController {

    private final FrontDesk desk;
    private final StaffAccounts staff;

    DeskController(FrontDesk desk, StaffAccounts staff) {
        this.desk = desk;
        this.staff = staff;
    }

    @GetMapping("/me")
    StaffUser me(Principal principal) {
        return staff.user(principal.getName());
    }

    @GetMapping("/{slug}/day")
    DeskDay day(
            Principal principal,
            @PathVariable String slug,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return desk.day(principal.getName(), slug, date);
    }

    @GetMapping("/{slug}/occupancy")
    List<OccupancyPoint> occupancy(
            Principal principal,
            @PathVariable String slug,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return desk.occupancy(principal.getName(), slug, from, to);
    }

    @PostMapping("/reservations/{code}/check-in")
    DeskReservation checkIn(Principal principal, @PathVariable String code) {
        return desk.checkIn(principal.getName(), code);
    }

    @PostMapping("/reservations/{code}/check-out")
    DeskReservation checkOut(Principal principal, @PathVariable String code) {
        return desk.checkOut(principal.getName(), code);
    }
}
