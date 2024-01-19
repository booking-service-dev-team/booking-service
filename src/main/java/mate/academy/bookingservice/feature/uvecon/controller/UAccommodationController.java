package mate.academy.bookingservice.feature.uvecon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.feature.uvecon.dto.UCreateApartmentAccommodationRequestDto;
import mate.academy.bookingservice.feature.uvecon.service.UveconApartmentAccommodationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Uvecon accommodation management",
        description = "Endpoints for uvecon accommodation management."
                + "By default, it is intended for work with apartments in Ukraine.")
@RestController
@RequestMapping("/uvecon/accommodations")
@RequiredArgsConstructor
public class UAccommodationController {
    private final UveconApartmentAccommodationService accommodationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new apartment accommodation for uvecon",
            description = "Create new apartment accommodation for uvecon"
                    + ", with availability = 1, and country = Ukraine by default")
    public AccommodationDto createAccommodation(
            @RequestBody UCreateApartmentAccommodationRequestDto requestDto
    ) {
        return accommodationService.saveApartmentUkraine(requestDto);
    }

    @PostMapping("/save-all")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create all apartment accommodations for uvecon",
            description = "Saves several apartment accommodations for uvecon to the database")
    public String saveAll(@RequestBody UCreateApartmentAccommodationRequestDto[] requestDtos) {
        return accommodationService.saveAllApartmentUkraine(requestDtos);
    }
}
