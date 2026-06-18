package ar.com.leguitech.fintechcoreservice.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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

    @Test
    void createNew_shouldThrowException_whenTickerIsNull() {
        String ticker = null;
        String name = "Apple Inc.";
        AssetType type = AssetType.STOCK;
        Money lastPrice = Money.of(new BigDecimal("150.50"), "USD");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Asset.createNew(ticker, name, type, lastPrice));

        assertEquals("Ticker cannot be null or empty", exception.getMessage());
    }

    @Test
    void createNew_shouldThrowException_whenTickerIsBlank() {
        String ticker = "   ";
        String name = "Apple Inc.";
        AssetType type = AssetType.STOCK;
        Money lastPrice = Money.of(new BigDecimal("150.50"), "USD");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Asset.createNew(ticker, name, type, lastPrice));

        assertEquals("Ticker cannot be null or empty", exception.getMessage());
    }

    @Test
    void createNew_shouldThrowException_whenNameIsNull() {
        String ticker = "AAPL";
        String name = null;
        AssetType type = AssetType.STOCK;
        Money lastPrice = Money.of(new BigDecimal("150.50"), "USD");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Asset.createNew(ticker, name, type, lastPrice));

        assertEquals("Asset name cannot be null or empty", exception.getMessage());
    }

    @Test
    void createNew_shouldThrowException_whenNameIsBlank() {
        String ticker = "AAPL";
        String name = "   ";
        AssetType type = AssetType.STOCK;
        Money lastPrice = Money.of(new BigDecimal("150.50"), "USD");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Asset.createNew(ticker, name, type, lastPrice));

        assertEquals("Asset name cannot be null or empty", exception.getMessage());
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