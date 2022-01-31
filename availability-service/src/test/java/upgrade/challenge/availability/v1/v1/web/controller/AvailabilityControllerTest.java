package upgrade.challenge.availability.v1.v1.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import upgrade.challenge.availability.v1.v1.adapter.AvailabilityAdapter;
import upgrade.challenge.availability.v1.v1.dto.AvailabilityDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailabilityController.class)
class AvailabilityControllerTest {

    private static final String CONTROLLER_BASE_URL = "/api/availability/v1.1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvailabilityAdapter availabilityAdapter;

    private ObjectMapper objectMapper;
    private Instant searchStartDate;
    private Instant searchEndDate;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        searchStartDate = Instant.now();
        searchEndDate = searchStartDate.plus(1L, ChronoUnit.DAYS);
    }

    @Test
    void searchAvailabilities_shouldReturn200Ok() throws Exception {
        final List<AvailabilityDto> expected = List.of(buildAvailabilityDto(), buildAvailabilityDto());

        when(availabilityAdapter.searchAvailabilities(searchStartDate, searchEndDate)).thenReturn(expected);

        this.mockMvc.perform(get(CONTROLLER_BASE_URL)
                        .param("searchStartDate", searchStartDate.toString())
                        .param("searchEndDate", searchEndDate.toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(availabilityAdapter).searchAvailabilities(searchStartDate, searchEndDate);
    }

    @Test
    void searchAvailabilities_withMissingQueryParameters_shouldReturn200Ok() throws Exception {
        final List<AvailabilityDto> expected = List.of(buildAvailabilityDto(), buildAvailabilityDto());

        when(availabilityAdapter.searchAvailabilities(null, null)).thenReturn(expected);

        this.mockMvc.perform(get(CONTROLLER_BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(availabilityAdapter).searchAvailabilities(null, null);
    }

    private AvailabilityDto buildAvailabilityDto() {
        return AvailabilityDto.builder()
                .available(true)
                .checkinTime(Instant.now())
                .checkoutTime(Instant.now())
                .build();
    }
}
