package upgrade.challenge.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upgrade.challenge.reservation.domain.Reservation;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByGuestEmailOrderByCreatedDateDesc(final String email);
}
