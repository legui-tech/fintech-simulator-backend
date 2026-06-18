package ar.com.leguitech.fintechcoreservice.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    @Test
    void createNew_shouldReturnValidAsset() {
        String ticker = "AAPL";
        String name = "Apple Inc.";
        AssetType type = AssetType.STOCK;
        Money lastPrice = Money.of(new BigDecimal("150.50"), "USD");

        Asset asset = Asset.createNew(ticker, name, type, lastPrice);

        assertNotNull(asset);
        assertEquals(ticker.toUpperCase(), asset.getTicker());
        assertEquals(name, asset.getName());
        assertEquals(type, asset.getType());
        assertEquals(lastPrice, asset.getLastPrice());
        assertNotNull(asset.getUpdatedAt());
        assertEquals(ZoneOffset.UTC, asset.getUpdatedAt().getOffset());
    }

    static Stream<Arguments> invalidTickerAndName() {
        return Stream.of(
                Arguments.of(null, "Apple Inc.", "Ticker cannot be null or empty"),
                Arguments.of("   ", "Apple Inc.", "Ticker cannot be null or empty"),
                Arguments.of("AAPL", null, "Asset name cannot be null or empty"),
                Arguments.of("AAPL", "   ", "Asset name cannot be null or empty")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTickerAndName")
    void createNew_shouldThrowException_whenTickerOrNameIsBlank(String ticker, String name, String expectedMessage) {
        AssetType type = AssetType.STOCK;
        Money lastPrice = Money.of(new BigDecimal("150.50"), "USD");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Asset.createNew(ticker, name, type, lastPrice));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void createNew_shouldThrowException_whenTypeIsNull() {
        String ticker = "AAPL";
        String name = "Apple Inc.";
        AssetType type = null;
        Money lastPrice = Money.of(new BigDecimal("150.50"), "USD");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Asset.createNew(ticker, name, type, lastPrice));

        assertEquals("Asset type must be specified", exception.getMessage());
    }

    @Test
    void createNew_shouldThrowException_whenLastPriceIsNull() {
        String ticker = "AAPL";
        String name = "Apple Inc.";
        AssetType type = AssetType.STOCK;
        Money lastPrice = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Asset.createNew(ticker, name, type, lastPrice));

        assertEquals("Initial market price must not be null", exception.getMessage());
    }

    @Test
    void restore_shouldReturnValidAsset() {
        String ticker = "GOOG";
        String name = "Alphabet Inc.";
        AssetType type = AssetType.STOCK;
        Money lastPrice = Money.of(new BigDecimal("2500.75"), "USD");
        OffsetDateTime updatedAt = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);

        Asset asset = Asset.restore(ticker, name, type, lastPrice, updatedAt);

        assertNotNull(asset);
        assertEquals(ticker, asset.getTicker());
        assertEquals(name, asset.getName());
        assertEquals(type, asset.getType());
        assertEquals(lastPrice, asset.getLastPrice());
        assertEquals(updatedAt, asset.getUpdatedAt());
    }
}