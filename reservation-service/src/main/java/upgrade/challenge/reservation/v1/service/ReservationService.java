package upgrade.challenge.reservation.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.domain.ReservationStatus;
import upgrade.challenge.reservation.repository.ReservationRepository;

import java.sql.SQLException;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Reservation createReservation(final Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public void cancelReservation(final Long id) {
        final Reservation reservationToCancel = reservationRepository.getById(id);

        reservationToCancel.setStatus(ReservationStatus.RESERVATION_CANCELLED);
        reservationRepository.save(reservationToCancel);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getAllReservationsByEmail(final String email) {
        return reservationRepository.findAllByGuestEmailOrderByCreatedDateDesc(email);
    }
}