package ar.com.leguitech.fintechcoreservice.domain.model;

import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Immutable Value Object representing a monetary financial amount bound to a standardized currency.
 * <p>
 * This class encapsulates {@link BigDecimal} operations to enforce strict business rules
 * regarding decimal scale, rounding behaviors, and currency alignment natively, eliminating
 * the risk of cross-currency contamination or precision drift across the domain layer.
 * </p>
 *
 * <p><strong>Architectural Specifications:</strong></p>
 * <ul>
 * <li><strong>Scale:</strong> Enforces a strict scale of 4 decimal places for high-precision
 * financial simulation fractions.</li>
 * <li><strong>Rounding Mode:</strong> Strictly applies {@link RoundingMode#HALF_EVEN}
 * (Banker's Rounding) on all mathematical operations to minimize statistical bias.</li>
 * <li><strong>Currency Safety:</strong> Utilizes Java's native {@link Currency} mechanism to
 * validate inputs against the ISO 4217 standard, while storing it as a {@link String} to maximize
 * distributed serialization compatibility (REST/Kafka).</li>
 * </ul>
 *
 * @author Walter Ariel Leguiza
 * @version 1.2
 * @see Order
 * @see Asset
 * @since 2026-06
 */
@Value
public class Money implements Comparable<Money> {

    /**
     * The internal raw numeric value, scaled and normalized.
     */
    BigDecimal amount;

    /**
     * The ISO 4217 three-letter currency code representing the monetary unit (e.g., "USD", "ARS").
     */
    String currency;

    /**
     * Private constructor that normalizes numeric scale and strictly validates the currency code.
     * <p>
     * This constructor leverages {@link Currency#getInstance(String)} defensively. If the provided
     * code does not match a legally recognized ISO 4217 currency, a runtime exception is thrown.
     * </p>
     *
     * @param amount       the raw numeric value to encapsulate. Can be null (defaults to zero).
     * @param currencyCode the ISO 4217 three-letter currency code (e.g., "USD", "ars"). Must not be null or blank.
     * @throws IllegalArgumentException if the currencyCode is null, blank, or an invalid ISO 4217 token.
     */
    private Money(BigDecimal amount, String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("Currency code cannot be null or blank");
        }

        String normalizedCode = currencyCode.trim().toUpperCase();

        // Defensive Check: Verifies code legitimacy against the JDK ISO registry.
        // Throws IllegalArgumentException natively if the token is unrecognized (e.g., "XYZ").
        Currency.getInstance(normalizedCode);

        this.currency = normalizedCode;
        this.amount = amount != null
                ? amount.setScale(4, RoundingMode.HALF_EVEN)
                : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_EVEN);
    }

    /**
     * Static factory method to create a Money instance with a specified amount and currency.
     *
     * @param amount       the raw numeric value.
     * @param currencyCode the ISO 4217 three-letter currency code (e.g., "USD", "ARS").
     * @return a new, immutable {@link Money} instance.
     * @throws IllegalArgumentException if the currencyCode is null, blank, or an invalid ISO 4217 token.
     */
    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, currencyCode);
    }

    /**
     * Static factory method to create a Money instance with a specified amount and a default currency (USD).
     *
     * @param amount the raw numeric value.
     * @return a new, immutable {@link Money} instance with "USD" as currency.
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount, "USD");
    }

    /**
     * Performs financial multiplication between this monetary value and a given factor.
     * <p>
     * The resulting Money instance preserves the original currency code configuration.
     * </p>
     *
     * @param factor the multiplier factor (e.g., quantity of shares). Must not be null.
     * @return a new, immutable {@link Money} instance holding the rounded product result.
     * @throws NullPointerException if the provided factor is null.
     */
    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "Multiplication factor must not be null");
        return Money.of(this.amount.multiply(factor), this.currency);
    }

    /**
     * Performs financial addition between this monetary value and another Money instance.
     *
     * @param other the other monetary amount to add. Must not be null.
     * @return a new, immutable {@link Money} instance holding the combined result.
     * @throws IllegalArgumentException if the currencies do not match.
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Addition value must not be null");
        validateCurrencyMatch(other);
        return Money.of(this.amount.add(other.amount), this.currency);
    }

    /**
     * Performs financial subtraction between this monetary value and another Money instance.
     *
     * @param other the monetary amount to subtract. Must not be null.
     * @return a new, immutable {@link Money} instance holding the net result.
     * @throws IllegalArgumentException if the currencies do not match.
     */
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Subtraction value must not be null");
        validateCurrencyMatch(other);
        return Money.of(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Utility validation check to determine if the monetary value is strictly positive.
     *
     * @return true if the underlying amount is strictly greater than zero, false otherwise.
     */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Compares this Money instance with another to establish sorting or value equivalence.
     *
     * @param other the other Money instance to compare against.
     * @return a negative integer, zero, or a positive integer as this value is less than,
     * equal to, or greater than the specified object.
     * @throws IllegalArgumentException if the currencies do not match.
     */
    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other, "Comparison target must not be null");
        validateCurrencyMatch(other);
        return this.amount.compareTo(other.amount);
    }

    /**
     * Internal defensive check to guarantee currency uniformity across mathematical operations.
     */
    private void validateCurrencyMatch(Money other) {
        if (!this.currency.equals(other.getCurrency())) {
            throw new IllegalArgumentException(String.format(
                    "Currency mismatch: Cannot operate between %s and %s", this.currency, other.getCurrency()
            ));
        }
    }
}