package upgrade.challenge.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upgrade.challenge.reservation.domain.ReservationEvent;

@Repository
public interface ReservationEventRepository extends JpaRepository<ReservationEvent, Long> {
}
