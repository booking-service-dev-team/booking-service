package mate.academy.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingservice.dto.accommodation.external.AvailabilityRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.CreateAccommodationRequestDto;
import mate.academy.bookingservice.dto.accommodation.external.PriceRequestDto;
import mate.academy.bookingservice.dto.accommodation.internal.AccommodationDto;
import mate.academy.bookingservice.dto.address.external.AddressRequestDto;
import mate.academy.bookingservice.service.accommodation.AccommodationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management",
        description = "Endpoints for managing accommodations.")
@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new accommodation",
            description = "Create new accommodation")
    public AccommodationDto createAccommodation(
            @RequestBody @Valid CreateAccommodationRequestDto requestDto
    ) {
        return accommodationService.save(requestDto);
    }

    @PostMapping("/save-all")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create all accommodations",
            description = "Saves several accommodations to the database")
    public String saveAll(@RequestBody @Valid CreateAccommodationRequestDto[] requestDtos) {
        return accommodationService.saveAll(requestDtos);
    }

    @Operation(summary = "Get all accommodations",
            description = "Get a list of all available accommodations")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AccommodationDto> getAll(Pageable pageable) {
        return accommodationService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get an accommodation by id",
            description = "Get an accommodation by id")
    public AccommodationDto getAccommodationById(@PathVariable Long id) {
        return accommodationService.getById(id);
    }

    @Operation(summary = "Accommodation price update by id",
            description = "Accommodation price update by id")
    @PatchMapping("/{id}/price")
    @ResponseStatus(HttpStatus.OK)
    public void updatePrice(@RequestBody @Valid PriceRequestDto requestDto,
                                        @PathVariable Long id) {
        accommodationService.updatePrice(requestDto, id);
    }

    @Operation(summary = "Accommodation availability update by id",
            description = "Accommodation availability update by id")
    @PatchMapping("/{id}/availability")
    @ResponseStatus(HttpStatus.OK)
    public void updateAvailability(@RequestBody @Valid AvailabilityRequestDto requestDto,
                            @PathVariable Long id) {
        accommodationService.updateAvailability(requestDto, id);
    }

    @Operation(summary = "Accommodation address update by id",
            description = "Accommodation address update by id. "
                    + "The country, city, street and house number should be indicated")
    @PatchMapping("/{id}/address")
    @ResponseStatus(HttpStatus.OK)
    public void updateAddress(@RequestBody @Valid AddressRequestDto requestDto,
                                   @PathVariable Long id) {
        accommodationService.updateAddress(requestDto, id);
    }

    @Operation(summary = "Delete an accommodation by id",
            description = "Soft delete an accommodation by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
