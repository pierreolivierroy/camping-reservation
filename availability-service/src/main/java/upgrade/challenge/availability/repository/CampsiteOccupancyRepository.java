package upgrade.challenge.availability.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upgrade.challenge.availability.domain.CampsiteOccupancy;

@Repository
public interface CampsiteOccupancyRepository extends JpaRepository<CampsiteOccupancy, Long> {

    void deleteByReservationId(final Long reservationId);
}
