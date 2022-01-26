package upgrade.challenge.reservation.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import upgrade.challenge.reservation.domain.Reservation;
import upgrade.challenge.reservation.domain.ReservationStatus;
import upgrade.challenge.reservation.exception.NotFoundException;
import upgrade.challenge.reservation.exception.ValidationException;
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
        validateReservation(reservation);

        // trigger availability check event
        return reservationRepository.save(reservation);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public void cancelReservation(final Long id) {
        final Reservation reservationToCancel = reservationRepository.getById(id);

        reservationToCancel.setStatus(ReservationStatus.RESERVATION_CANCELLED);
        // trigger availability check event
        reservationRepository.save(reservationToCancel);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getAllReservationsByEmail(final String email) {
        return reservationRepository.findAllByGuestEmailOrderByCreatedDateDesc(email);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Reservation patchReservation(final Long id, final Reservation reservation) {
        return reservationRepository.findById(id)
                .map(existingReservation -> patchReservation(existingReservation, reservation))
                .orElseThrow(NotFoundException::new);
    }

    private void validateReservation(final Reservation reservation) {
        final BeanPropertyBindingResult beanPropertyBindingResult = new BeanPropertyBindingResult(reservation, "reservation");
        reservationValidator.validate(reservation, beanPropertyBindingResult);

        throwExceptionIfAnyValidationErrors(beanPropertyBindingResult);
    }

    private void throwExceptionIfAnyValidationErrors(final BeanPropertyBindingResult beanPropertyBindingResult) {
        if (!CollectionUtils.isEmpty(beanPropertyBindingResult.getFieldErrors())) {
            throw new ValidationException(beanPropertyBindingResult.getFieldErrors());
        }
    }

    private Reservation patchReservation(final Reservation existingReservation, final Reservation reservation) {
        if (existingReservation.getArrivalDate() != reservation.getArrivalDate()
                || existingReservation.getDepartureDate() != reservation.getDepartureDate()) {
            existingReservation.setArrivalDate(reservation.getArrivalDate());
            existingReservation.setDepartureDate(reservation.getDepartureDate());
            existingReservation.setStatus(ReservationStatus.RESERVATION_CHANGE_PENDING);

            validateReservation(existingReservation);
            // trigger availability check event
            return reservationRepository.save(existingReservation);
        }

        return existingReservation;
    }
}
