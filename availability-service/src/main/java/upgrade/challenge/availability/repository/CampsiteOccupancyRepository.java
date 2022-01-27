package upgrade.challenge.availability.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upgrade.challenge.availability.domain.CampsiteOccupancy;

import java.util.Optional;

@Repository
public interface CampsiteOccupancyRepository extends JpaRepository<CampsiteOccupancy, Long> {

    void deleteByReservationId(final Long reservationId);

    Optional<CampsiteOccupancy> findByReservationId(final Long reservationId);
}
