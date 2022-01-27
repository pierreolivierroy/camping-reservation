package upgrade.challenge.reservation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = @Index(name = "guest_email_idx", columnList = "guestEmail"))
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.RESERVATION_PENDING;

    @NotBlank
    private String guestEmail;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private Instant arrivalDate;

    @NotNull
    private Instant departureDate;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;
}
