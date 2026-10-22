package dev.jkexamples.hotel.rest;

import dev.jkexamples.hotel.contract.Availability;
import dev.jkexamples.hotel.contract.HotelDetail;
import dev.jkexamples.hotel.contract.HotelSummary;
import dev.jkexamples.hotel.contract.SearchResult;
import dev.jkexamples.hotel.service.HotelCatalog;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Public read API: hotels, search and availability. */
@RestController
@RequestMapping("/api")
class CatalogController {

    private final HotelCatalog catalog;

    CatalogController(HotelCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/hotels")
    List<HotelSummary> hotels() {
        return catalog.hotels();
    }

    @GetMapping("/hotels/{slug}")
    HotelDetail hotel(@PathVariable String slug) {
        return catalog.hotel(slug);
    }

    @GetMapping("/search")
    List<SearchResult> search(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "2") @Min(1) @Max(8) int guests) {
        return catalog.search(checkIn, checkOut, guests);
    }

    @GetMapping("/hotels/{slug}/availability")
    Availability availability(
            @PathVariable String slug,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "2") @Min(1) @Max(8) int guests) {
        return catalog.availability(slug, checkIn, checkOut, guests);
    }
}
