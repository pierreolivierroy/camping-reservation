package upgrade.challenge.availability.v1.messaging.eventmessage;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
public class ReservationCreatedEvent {

    private Long reservationId;

    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant arrivalDate;

    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant departureDate;
}
