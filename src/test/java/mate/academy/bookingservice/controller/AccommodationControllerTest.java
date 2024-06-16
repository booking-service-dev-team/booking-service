package mate.academy.bookingservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookingservice.AbstractIntegrationTest;
import mate.academy.bookingservice.dto.accommodation.external.AvailabilityRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.CreateAccommodationRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.PriceRequestDto;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.dto.address.external.AddressRequestDto;
import mate.academy.bookingservice.dto.address.internal.AddressDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccommodationControllerTest extends AbstractIntegrationTest {
    // todo change groupId

    protected static MockMvc mockMvc;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeAll
    static void beforeAll(WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @SneakyThrows
    @BeforeEach
    void initializeAccommodationsAndAddressesTables(TestInfo testInfo) {
        if (testInfo.getTags().contains("IWantToInitialize")) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(true);
                ScriptUtils.executeSqlScript(
                        connection,
                        new ClassPathResource(
                                "databases/addresses/add-addresses-to-addresses-table.sql"
                        )
                );
                ScriptUtils.executeSqlScript(
                        connection,
                        new ClassPathResource(
                                "databases/accommodations/"
                                + "add-accommodations-to-accommodations-table.sql"
                        )
                );
            }
        }
    }

    @AfterEach
    void cleanUp() {
        tearDown(dataSource);
    }

    @SneakyThrows
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a new accommodation")
    @Test
    void createAccommodation_ValidRequestDto_Success() {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto()
                .setCountryName("United Kingdom")
                .setCityName("London")
                .setStreetName("Baker st.")
                .setNumberOfHouse("221B")
                .setSizeOfAccommodation("big house")
                .setAmenities("fireplace, hall in the wall")
                .setPricePerDayUsd(BigDecimal.valueOf(100))
                .setTypeName("APARTMENT")
                .setNumberOfAvailableAccommodation(1);

        AccommodationDto expected = new AccommodationDto()
                .setId(11L)
                .setType(requestDto.getTypeName())
                .setSizeOfAccommodation(requestDto.getSizeOfAccommodation())
                .setAmenities(requestDto.getAmenities())
                .setPricePerDayUsd(requestDto.getPricePerDayUsd())
                .setNumberOfAvailableAccommodation(requestDto.getNumberOfAvailableAccommodation())
                .setAddress(new AddressDto()
                        .setId(11L)
                        .setCountryName(requestDto.getCountryName())
                        .setCityName(requestDto.getCityName())
                        .setStreetName(requestDto.getStreetName())
                        .setNumberOfHouse(requestDto.getNumberOfHouse()));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/accommodations")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        AccommodationDto actual = objectMapper.readValue(contentAsString, AccommodationDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder
                .reflectionEquals(expected.getAddress(), actual.getAddress(), "id"));
        assertTrue(EqualsBuilder
                .reflectionEquals(expected, actual, "id", "address"));
    }

    @SneakyThrows
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a list of accommodations")
    @Test
    void saveAll_ValidRequestDto_Success() {
        CreateAccommodationRequestDto[] requestDtos = new CreateAccommodationRequestDto[]{
                new CreateAccommodationRequestDto()
                        .setCountryName("United Kingdom")
                        .setCityName("London")
                        .setStreetName("Baker st.")
                        .setNumberOfHouse("221B")
                        .setSizeOfAccommodation("big house")
                        .setAmenities("fireplace, hall in the wall")
                        .setPricePerDayUsd(BigDecimal.valueOf(100))
                        .setTypeName("APARTMENT")
                        .setNumberOfAvailableAccommodation(1),
                new CreateAccommodationRequestDto()
                        .setCountryName("France")
                        .setCityName("Paris")
                        .setStreetName("Champs-Elysees")
                        .setNumberOfHouse("101")
                        .setSizeOfAccommodation("luxury apartment")
                        .setAmenities("balcony, Eiffel Tower view")
                        .setPricePerDayUsd(BigDecimal.valueOf(400))
                        .setTypeName("APARTMENT")
                        .setNumberOfAvailableAccommodation(2),
                new CreateAccommodationRequestDto()
                        .setCountryName("Japan")
                        .setCityName("Tokyo")
                        .setStreetName("Shibuya Crossing")
                        .setNumberOfHouse("3")
                        .setSizeOfAccommodation("compact studio")
                        .setAmenities("high-speed internet, kitchenette")
                        .setPricePerDayUsd(BigDecimal.valueOf(75))
                        .setTypeName("CONDO")
                        .setNumberOfAvailableAccommodation(3)
        };

        String jsonRequest = objectMapper.writeValueAsString(requestDtos);

        MvcResult result = mockMvc.perform(
                        post("/accommodations/save-all")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String expected = "Accommodations saved successfully";
        String actual = result.getResponse().getContentAsString();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @DisplayName("Get a list of all accommodations")
    @Test
    @Tag("IWantToInitialize")
    void getAll_ShouldReturnAllAccommodations() {
        List<AccommodationDto> expected = new ArrayList<>();
        expected.add(new AccommodationDto()
                .setId(1L)
                .setType("APARTMENT")
                .setSizeOfAccommodation("big house")
                .setAmenities("fireplace, hall in the wall")
                .setPricePerDayUsd(BigDecimal.valueOf(99.99))
                .setNumberOfAvailableAccommodation(1)
                .setAddress(new AddressDto()
                        .setId(11L)
                        .setCountryName("United Kingdom")
                        .setCityName("London")
                        .setStreetName("Baker st.")
                        .setNumberOfHouse("221B")));
        expected.add(new AccommodationDto()
                .setId(2L)
                .setType("APARTMENT")
                .setSizeOfAccommodation("luxury apartment")
                .setAmenities("balcony, Eiffel Tower view")
                .setPricePerDayUsd(BigDecimal.valueOf(400.15))
                .setNumberOfAvailableAccommodation(2)
                .setAddress(new AddressDto()
                        .setId(12L)
                        .setCountryName("France")
                        .setCityName("Paris")
                        .setStreetName("Champs-Elysees")
                        .setNumberOfHouse("101")));
        expected.add(new AccommodationDto()
                .setId(3L)
                .setType("CONDO")
                .setSizeOfAccommodation("compact studio")
                .setAmenities("high-speed internet, kitchenette")
                .setPricePerDayUsd(BigDecimal.valueOf(75.95))
                .setNumberOfAvailableAccommodation(3)
                .setAddress(new AddressDto()
                        .setId(13L)
                        .setCountryName("Japan")
                        .setCityName("Tokyo")
                        .setStreetName("Shibuya Crossing")
                        .setNumberOfHouse("3")));

        MvcResult result = mockMvc.perform(
                        get("/accommodations")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), AccommodationDto[].class
        );

        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @SneakyThrows
    @DisplayName("Get an accommodation by id")
    @WithMockUser(username = "user")
    @Test
    @Tag("IWantToInitialize")
    void getAccommodationById_ValidId_Success() {
        AccommodationDto expected = new AccommodationDto()
                .setId(1L)
                .setType("APARTMENT")
                .setSizeOfAccommodation("big house")
                .setAmenities("fireplace, hall in the wall")
                .setPricePerDayUsd(BigDecimal.valueOf(99.99))
                .setNumberOfAvailableAccommodation(1)
                .setAddress(new AddressDto()
                        .setId(11L)
                        .setCountryName("United Kingdom")
                        .setCityName("London")
                        .setStreetName("Baker st.")
                        .setNumberOfHouse("221B"));

        MvcResult result = mockMvc.perform(
                        get("/accommodations/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @DisplayName("Accommodation price update by id")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Tag("IWantToInitialize")
    void updatePrice_ValidRequest_Success() {
        PriceRequestDto requestDto = new PriceRequestDto()
                .setPricePerDayUsd(BigDecimal.valueOf(12345.99));

        AccommodationDto expected = new AccommodationDto()
                .setId(1L)
                .setType("APARTMENT")
                .setSizeOfAccommodation("big house")
                .setAmenities("fireplace, hall in the wall")
                .setPricePerDayUsd(BigDecimal.valueOf(12345.99))
                .setNumberOfAvailableAccommodation(1)
                .setAddress(new AddressDto()
                        .setId(11L)
                        .setCountryName("United Kingdom")
                        .setCityName("London")
                        .setStreetName("Baker st.")
                        .setNumberOfHouse("221B"));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        patch("/accommodations/1/price")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @DisplayName("Accommodation availability update by id")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Tag("IWantToInitialize")
    void updateAvailability_ValidRequest_Success() {
        AvailabilityRequestDto requestDto = new AvailabilityRequestDto()
                .setNumberOfAvailableAccommodation(5);

        AccommodationDto expected = new AccommodationDto()
                .setId(1L)
                .setType("APARTMENT")
                .setSizeOfAccommodation("big house")
                .setAmenities("fireplace, hall in the wall")
                .setPricePerDayUsd(BigDecimal.valueOf(99.99))
                .setNumberOfAvailableAccommodation(5)
                .setAddress(new AddressDto()
                        .setId(11L)
                        .setCountryName("United Kingdom")
                        .setCityName("London")
                        .setStreetName("Baker st.")
                        .setNumberOfHouse("221B"));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        patch("/accommodations/1/availability")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @DisplayName("Accommodation address update by accommodation id")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Tag("IWantToInitialize")
    void updateAddress_ValidRequest_Success() {
        AddressRequestDto requestDto = new AddressRequestDto()
                .setCountryName("England")
                .setCityName("London city")
                .setStreetName("Piccadilly st.")
                .setNumberOfHouse("1");

        AccommodationDto expected = new AccommodationDto()
                .setId(1L)
                .setType("APARTMENT")
                .setSizeOfAccommodation("big house")
                .setAmenities("fireplace, hall in the wall")
                .setPricePerDayUsd(BigDecimal.valueOf(99.99))
                .setNumberOfAvailableAccommodation(5)
                .setAddress(new AddressDto()
                        .setId(11L) //unknown and unimportant
                        .setCountryName("England")
                        .setCityName("London city")
                        .setStreetName("Piccadilly st.")
                        .setNumberOfHouse("1"));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        patch("/accommodations/1/address")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class
        );

        assertNotNull(actual);
        assertTrue(EqualsBuilder
                .reflectionEquals(expected.getAddress(), actual.getAddress(), "id"));
    }

    @SneakyThrows
    @DisplayName("Delete an accommodation by id")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Tag("IWantToInitialize")
    void deleteById_ValidId_Success() {
        mockMvc.perform(
                delete("/accommodations/1")
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/accommodations/1")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    private void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "databases/addresses/remove-all-from-addresses-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "databases/accommodations/remove-all-from-accommodations-table.sql"
                    )
            );
        }
    }
}
