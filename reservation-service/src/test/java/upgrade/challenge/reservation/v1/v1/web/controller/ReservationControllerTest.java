package upgrade.challenge.reservation.v1.v1.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.web.handler.ErrorMessage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    private static final String CONTROLLER_BASE_URL = "/reservation/v1.1";
    private static final String INVALID_FIELD_ERROR_MESSAGE = "Invalid field provided";
    private static final String REQUEST_BODY_MISSING_ERROR_MESSAGE = "Request body is missing";

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private ReservationDto reservationDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        reservationDto = buildReservationDto();
    }

    @Test
    void reserveCampsite_shouldReturn201Created() throws Exception {
        this.mockMvc.perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    void reserveCampsite_withMissingBody_shouldReturn400BadRequest() throws Exception {
        this.mockMvc.perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(
                        buildErrorMessage(REQUEST_BODY_MISSING_ERROR_MESSAGE))));
    }

    @ParameterizedTest
    @MethodSource("getInvalidRequestBodies")
    void reserveCampsite_withInvalidField_shouldReturn400BadRequest(final ReservationDto reservationDto,
                                                                    final List<String> errorMessages) throws Exception {
        this.mockMvc.perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(
                        buildErrorMessage(errorMessages, INVALID_FIELD_ERROR_MESSAGE))));
    }

    private static Stream<Arguments> getInvalidRequestBodies() {
        return Stream.of(
                Arguments.arguments(buildReservationDto().setEmail(null), Collections.singletonList("email must not be blank")),
                Arguments.arguments(buildReservationDto().setEmail(""), Collections.singletonList("email must not be blank")),
                Arguments.arguments(buildReservationDto().setEmail(" "), Collections.singletonList("email must not be blank")),
                Arguments.arguments(buildReservationDto().setFirstName(null), Collections.singletonList("firstName must not be blank")),
                Arguments.arguments(buildReservationDto().setFirstName(""), Collections.singletonList("firstName must not be blank")),
                Arguments.arguments(buildReservationDto().setFirstName(" "), Collections.singletonList("firstName must not be blank")),
                Arguments.arguments(buildReservationDto().setLastName(null), Collections.singletonList("lastName must not be blank")),
                Arguments.arguments(buildReservationDto().setLastName(""), Collections.singletonList("lastName must not be blank")),
                Arguments.arguments(buildReservationDto().setLastName(" "), Collections.singletonList("lastName must not be blank")),
                Arguments.arguments(buildReservationDto().setArrivalDate(null), Collections.singletonList("arrivalDate must not be null")),
                Arguments.arguments(buildReservationDto().setDepartureDate(null), Collections.singletonList("departureDate must not be null"))
        );
    }

    private static ReservationDto buildReservationDto() {
        final Instant now = Instant.now();

        return ReservationDto.builder()
                .email("email")
                .firstName("first-name")
                .lastName("last-name")
                .arrivalDate(now)
                .departureDate(now.plus(1L, ChronoUnit.DAYS))
                .build();
    }

    private ErrorMessage buildErrorMessage(final String message) {
        return buildErrorMessage(null, message);
    }

    private ErrorMessage buildErrorMessage(final List<?> causes, final String message) {
        return ErrorMessage.builder()
                .causes(causes)
                .message(message)
                .build();
    }
}
