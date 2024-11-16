package serhii.malov.currencies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import serhii.malov.currencies.model.dto.CurrencyDtoRq;
import serhii.malov.currencies.model.dto.CurrencyDtoRs;
import serhii.malov.currencies.model.dto.ExchangeRateDtoRs;
import serhii.malov.currencies.service.CurrencyService;
import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    @Operation(summary = "Get List Currency")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "`OK`",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CurrencyDtoRs.class))
                    )
            )
    })
    public ResponseEntity<List<CurrencyDtoRs>> getCurrencies() {
        return ResponseEntity.ok(currencyService.getCurrencies());
    }

    @GetMapping("/{code}/exchange-rate")
    @Operation(summary = "Get List ExchangeRate by Currency code")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "`OK`",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ExchangeRateDtoRs.class))
                    )
            )
    })
    public ResponseEntity<List<ExchangeRateDtoRs>> getExchangeRate(
            @Parameter(description = "Currency code for fetching exchange rates")
            @PathVariable String code) {
        return ResponseEntity.ok(currencyService.getExchangeRate(code));
    }

    @PostMapping
    @Operation(summary = "Create Currency")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "`Created`",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CurrencyDtoRs.class)
                    )
            )
    })
    public ResponseEntity<CurrencyDtoRs> addCurrency(@RequestBody CurrencyDtoRq currencyDtoRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyService.addCurrency(currencyDtoRq));
    }
}
