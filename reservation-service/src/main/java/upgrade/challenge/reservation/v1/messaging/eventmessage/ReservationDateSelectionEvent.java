package upgrade.challenge.reservation.v1.messaging.eventmessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ReservationDateSelectionEvent {

    private Long reservationId;
    private Instant arrivalDate;
    private Instant departureDate;
}
