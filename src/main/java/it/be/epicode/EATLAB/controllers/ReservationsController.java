package it.be.epicode.EATLAB.controllers;

import it.be.epicode.EATLAB.entities.Reservation;
import it.be.epicode.EATLAB.entities.Type;
import it.be.epicode.EATLAB.entities.User;
import it.be.epicode.EATLAB.exceptions.UnauthorizedException;
import it.be.epicode.EATLAB.payloads.reservations.ReservationCreationDTO;
import it.be.epicode.EATLAB.payloads.reservations.ReservationUpdatingDTO;
import it.be.epicode.EATLAB.services.ReservationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/reservations")
public class ReservationsController {

    @Autowired
    private ReservationsService reservationsService;


    @PostMapping("/creation/{restaurantId}")
    public Reservation createReservation(@RequestBody ReservationCreationDTO reservationDTO,@PathVariable UUID restaurantId) {

        return reservationsService.saveReservation(reservationDTO,restaurantId);
    }


    @GetMapping("/myreservations")
    public ResponseEntity<List<Reservation>> getMyReservations() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
User currentUser = (User) authentication.getPrincipal();
if (currentUser.getType() == Type.CUSTOMER) {
    List<Reservation> reservations = reservationsService.getReservationsByUserEmail(userEmail);

    return ResponseEntity.ok(reservations);
} else {
    throw new UnauthorizedException("Unable to return your list of reservations, you are not a Customer");
}

    }


    @PutMapping("/{reservationId}")
    public Reservation updateReservation(@PathVariable UUID reservationId, @RequestBody ReservationUpdatingDTO modifiedReservation) {

            return reservationsService.findByIdAndUpdate(reservationId,modifiedReservation);

    }


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Reservation> getAllReservations(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "id") String orderBy
    ) {
        return this.reservationsService.getReservations(page, size, orderBy);
    }


    @DeleteMapping("/{reservationId}")
    public void deleteReservation(@PathVariable UUID reservationId) {

            reservationsService.findByIdAndDelete(reservationId);
    }

}
