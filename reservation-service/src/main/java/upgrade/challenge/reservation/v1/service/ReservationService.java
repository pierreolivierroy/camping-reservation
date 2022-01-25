package upgrade.challenge.reservation.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.domain.ReservationStatus;
import upgrade.challenge.reservation.repository.ReservationRepository;
import upgrade.challenge.reservation.validator.ReservationValidator;

import java.sql.SQLException;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, ReservationValidator reservationValidator) {
        this.reservationRepository = reservationRepository;
        this.reservationValidator = reservationValidator;
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Reservation createReservation(final Reservation reservation) {
        final BeanPropertyBindingResult beanPropertyBindingResult = new BeanPropertyBindingResult(reservation, "reservation");

        reservationValidator.validate(reservation, beanPropertyBindingResult);

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

    private void validateReservation(final Reservation reservation) {
        final BeanPropertyBindingResult beanPropertyBindingResult = new BeanPropertyBindingResult(reservation, "reservation");
        reservationValidator.validate(reservation, beanPropertyBindingResult);


    }

    private void throwExceptionIfAnyValidationErrors(final BeanPropertyBindingResult beanPropertyBindingResult) {
        if (CollectionUtils)
    }
}
