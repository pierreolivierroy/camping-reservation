package upgrade.challenge.availability.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upgrade.challenge.availability.domain.CampsiteOccupancy;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampsiteOccupancyRepository extends JpaRepository<CampsiteOccupancy, Long> {

    void deleteByReservationId(final Long reservationId);

    Optional<CampsiteOccupancy> findByReservationId(final Long reservationId);

    List<CampsiteOccupancy> findAllByArrivalDateLessThanEqualAndDepartureDateGreaterThanEqual(final Instant arrivalDate, final Instant departureDate);

    List<CampsiteOccupancy> findAllByDepartureDateGreaterThanEqualAndDepartureDateLessThanEqual(final Instant arrivalDate, final Instant departureDate);

    List<CampsiteOccupancy> findAllByArrivalDateGreaterThanEqualAndDepartureDateLessThanEqualOrderByArrivalDate(final Instant arrivalDate, final Instant departureDate);
}
