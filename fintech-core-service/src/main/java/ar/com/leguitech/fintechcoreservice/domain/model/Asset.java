package ar.com.leguitech.fintechcoreservice.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.OffsetDateTime;

/**
 * Pure Domain Entity representing a financial instrument (Asset) tradeable within the ecosystem.
 * <p>
 * This class is completely <strong>immutable</strong> via Lombok's {@code @Value} annotation,
 * ensuring thread-safety and consistent state representation across the core services.
 * </p>
 *
 * <p><strong>Core Business Invariants:</strong></p>
 * <ul>
 * <li><strong>Natural Identity:</strong> Object equality and hash code generation are driven
 * solely and exclusively by the {@code ticker} field (e.g., "AAPL"), which serves as the
 * unique business identifier.</li>
 * <li><strong>Currency-Bound Valuation:</strong> The {@code lastPrice} field strictly utilizes
 * the {@link Money} Value Object, ensuring the asset's market value is always explicitly coupled
 * with its native operational currency (e.g., USD, ARS) to prevent cross-currency validation errors.</li>
 * <li><strong>Temporal Normalization:</strong> The {@code updatedAt} timestamp is strictly
 * normalized to UTC to guarantee global synchronization across distributed market feeds.</li>
 * </ul>
 *
 * @author Walter Ariel Leguiza
 * @version 1.1
 * @see AssetType
 * @see Money
 * @see Order
 * @since 2026-06
 */
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Asset {

    /**
     * Unique trading symbol of the financial instrument (e.g., "AAPL", "AL30", "KO").
     * Acts as the strict natural identity of this domain entity.
     */
    @lombok.EqualsAndHashCode.Include
    String ticker;

    /**
     * Full legal or corporate name of the asset issuer (e.g., "Apple Inc.", "Government of Argentina").
     */
    String name;

    /**
     * Categorization classifying the operational and asset class nature of the instrument.
     */
    AssetType type;

    /**
     * The last recorded market unit price of the asset, encapsulating both the amount and its currency.
     */
    Money lastPrice;

    /**
     * Exact timestamp when the asset price was last updated by the market feed.
     * Enforced in UTC zone offset.
     */
    OffsetDateTime updatedAt;

    /**
     * Static factory method to instantiate a brand new tradeable asset within the system.
     * <p>
     * This method initializes the instrument with its baseline metadata, ensures the
     * ticker is normalized to uppercase, and sets the initial tracking timestamp to UTC
     * using {@link OffsetDateTime#now(java.time.ZoneId)}.
     * </p>
     *
     * @param ticker    unique trading symbol. Will be normalized to uppercase. Must not be null or blank.
     * @param name      full corporate name. Must not be null or blank.
     * @param type      the asset class category. Must not be null.
     * @param lastPrice initial market unit price including currency configuration. Must not be null.
     * @return a new, valid {@link Asset} instance ready to be cataloged in the core engine.
     * @throws IllegalArgumentException if any string parameters are blank or object parameters are null.
     */
    public static Asset createNew(String ticker, String name, AssetType type, Money lastPrice) {
        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("Ticker cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Asset name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Asset type must be specified");
        }
        if (lastPrice == null) {
            throw new IllegalArgumentException("Initial market price must not be null");
        }

        return new Asset(
                ticker.trim().toUpperCase(),
                name,
                type,
                lastPrice,
                OffsetDateTime.now(java.time.ZoneOffset.UTC)
        );
    }

    /**
     * Static factory method used by infrastructure adapters to rehydrate an existing
     * asset from persistent storage.
     * <p>
     * Honors the historical pricing data and timeline logs exactly as retrieved from the database.
     * </p>
     *
     * @param ticker    historical trading symbol.
     * @param name      persisted corporate name.
     * @param type      persisted asset class category.
     * @param lastPrice last historically recorded market price with its currency.
     * @param updatedAt historical timestamp of the last price update.
     * @return a reconstructive {@link Asset} instance true to the infrastructure records.
     */
    public static Asset restore(String ticker, String name, AssetType type, Money lastPrice, OffsetDateTime updatedAt) {
        return new Asset(ticker, name, type, lastPrice, updatedAt);
    }
}