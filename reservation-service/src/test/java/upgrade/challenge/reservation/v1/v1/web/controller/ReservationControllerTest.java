package upgrade.challenge.reservation.v1.v1.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import upgrade.challenge.reservation.domain.ReservationStatus;
import upgrade.challenge.reservation.v1.v1.adapter.ReservationAdapter;
import upgrade.challenge.reservation.v1.v1.dto.ReservationDto;
import upgrade.challenge.reservation.v1.v1.dto.ReservationResponseDto;
import upgrade.challenge.reservation.v1.v1.web.handler.ErrorMessage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    private static final String CONTROLLER_BASE_URL = "/api/reservation/v1.1";
    private static final String EMAIL = "email@test.com";
    private static final String INVALID_FIELD_ERROR_MESSAGE = "Invalid field provided";
    private static final String REQUEST_BODY_MISSING_ERROR_MESSAGE = "Request body is missing";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationAdapter reservationAdapter;

    private ObjectMapper objectMapper;
    private ReservationDto reservationDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        reservationDto = buildReservationDto();
    }

    @Test
    void getAllReservationsByEmail_shouldReturn200Ok() throws Exception {
        final List<ReservationResponseDto> expected = List.of(
                buildReservationResponseDto(Instant.now(), Instant.now().plus(1L, ChronoUnit.DAYS)),
                buildReservationResponseDto(Instant.now(), Instant.now().plus(1L, ChronoUnit.DAYS))
        );

        when(reservationAdapter.getAllReservationsByEmail(EMAIL))
                .thenReturn(expected);

        this.mockMvc.perform(get(CONTROLLER_BASE_URL)
                        .param("email", EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(reservationAdapter).getAllReservationsByEmail(EMAIL);
    }

    @ParameterizedTest
    @NullSource
    void getAllReservationsByEmail_withMissingEmailRequestParameter_shouldReturn400BadRequest(final String email) throws Exception {
        this.mockMvc.perform(get(CONTROLLER_BASE_URL)
                        .queryParam("email", email))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .string(objectMapper.writeValueAsString(
                                buildErrorMessage("Required request parameter 'email' for method parameter type String is not present"))));

        verifyNoInteractions(reservationAdapter);
    }

    @Test
    void makeReservation_shouldReturn201Created() throws Exception {
        final ReservationResponseDto expected = buildReservationResponseDto(
                reservationDto.getArrivalDate(),
                reservationDto.getDepartureDate());

        when(reservationAdapter.makeReservation(any(ReservationDto.class)))
                .thenReturn(expected);

        this.mockMvc.perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(reservationAdapter).makeReservation(any(ReservationDto.class));
    }

    @Test
    void makeReservation_withMissingBody_shouldReturn400BadRequest() throws Exception {
        this.mockMvc.perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(
                        buildErrorMessage(REQUEST_BODY_MISSING_ERROR_MESSAGE))));

        verifyNoInteractions(reservationAdapter);
    }

    @ParameterizedTest
    @MethodSource("getInvalidRequestBodies")
    void makeReservation_withInvalidField_shouldReturn400BadRequest(final ReservationDto reservationDto,
                                                                    final List<String> errorMessages) throws Exception {
        this.mockMvc.perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(
                        buildErrorMessage(errorMessages, INVALID_FIELD_ERROR_MESSAGE))));

        verifyNoInteractions(reservationAdapter);
    }

    @Test
    void cancelReservation_shouldReturn200Ok() throws Exception {
        doNothing().when(reservationAdapter).deleteReservation(123456789L);

        this.mockMvc.perform(delete(CONTROLLER_BASE_URL + "/123456789"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(reservationAdapter).deleteReservation(123456789L);
    }

    @Test
    void cancelReservation_withMissingReservationId_shouldReturn400BadRequest() throws Exception {
        this.mockMvc.perform(delete(CONTROLLER_BASE_URL + "/"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""));

        verifyNoInteractions(reservationAdapter);
    }

    private static Stream<Arguments> getInvalidRequestBodies() {
        return Stream.of(
                Arguments.arguments(buildReservationDto().setGuestEmail(null), Collections.singletonList("guestEmail must not be blank")),
                Arguments.arguments(buildReservationDto().setGuestEmail(""), Collections.singletonList("guestEmail must not be blank")),
                Arguments.arguments(buildReservationDto().setGuestEmail(" "), Collections.singletonList("guestEmail must not be blank")),
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
                .guestEmail("email")
                .firstName("first-name")
                .lastName("last-name")
                .arrivalDate(now)
                .departureDate(now.plus(1L, ChronoUnit.DAYS))
                .build();
    }

    private ReservationResponseDto buildReservationResponseDto(final Instant arrivalDate,
                                                               final Instant departureDate) {
        return ReservationResponseDto.builder()
                .reservationId("reservation-id")
                .status(ReservationStatus.RESERVATION_PENDING.toString())
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .reservationDate(Instant.now())
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
