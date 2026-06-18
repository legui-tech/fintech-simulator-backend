package ar.com.leguitech.fintechcoreservice.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Pure Domain Entity representing a financial purchase or sale Order.
 * <p>
 * This class is completely <strong>immutable</strong> due to Lombok's {@code @Value} annotation,
 * ensuring absolute thread-safety and side-effect-free processing across the distributed
 * ecosystem.
 * </p>
 *
 * <p><strong>Core Business Invariants:</strong></p>
 * <ul>
 * <li><strong>Financial Precision:</strong> Quantity, price, and total amount fields strictly
 * utilize {@link BigDecimal} and {@link Money} to completely mitigate binary floating-point rounding errors and
 * encapsulate currency-specific logic.</li>
 * <li><strong>Strict Identity:</strong> Object equality and hash code generation are driven
 * solely and exclusively by the {@code id} field, abstracting away any numerical scale
 * variations from the remaining attributes.</li>
 * </ul>
 *
 * @author Walter Ariel Leguiza
 * @version 1.0
 * @see Asset
 * @see OrderStatus
 * @see OrderSide
 * @since 2026-06
 */
@Value
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    /**
     * Unique identifier of the order (UUID represented as a String).
     * Defines the strict identity of the entity within the distributed ecosystem.
     */
    @EqualsAndHashCode.Include
    String id;

    /**
     * Unique identifier of the origin/destination ledger account executing the operation.
     * Logically couples this order with the balance microservice domain.
     */
    String accountId;

    /**
     * Financial instrument trading symbol or ticker (e.g., "AAPL", "AL30").
     */
    String ticker;

    /**
     * Direction of the financial transaction (Purchase or Sale).
     */
    OrderSide side;

    /**
     * Number of nominal units to trade.
     * Defined as a pure {@code BigDecimal} to natively support fractional asset acquisition
     * without currency binding.
     */
    BigDecimal quantity;

    /**
     * Agreed unit market price for the transaction execution.
     */
    Money price;

    /**
     * Consolidated total financial amount of the operation.
     * Derived internally through currency-safe scalar multiplication: {@code price.multiply(quantity)}.
     */
    Money totalAmount;

    /**
     * Current lifecycle state of the order within the ecosystem.
     */
    OrderStatus status;

    /**
     * Exact timestamp when the order was initially recorded by the core engine.
     */
    OffsetDateTime createdAt;

    /**
     * Static factory method to instantiate brand-new orders within the domain boundary.
     * <p>
     * This method automatically initializes the order with a random {@link java.util.UUID},
     * enforces the initial state as {@link OrderStatus#PENDING}, and captures the current
     * timestamp explicitly normalized to UTC using {@link OffsetDateTime#now(java.time.ZoneId)}.
     * </p>
     * <p>
     * Additionally, it derives the {@code totalAmount} by multiplying quantity and price,
     * strictly applying financial precision rounding constraints with a scale of 4 decimals
     * and {@link java.math.RoundingMode#HALF_EVEN}.
     * </p>
     *
     * @param accountId unique identifier of the requesting account. Must not be null or empty.
     * @param ticker    trading symbol of the asset. Must not be null or empty.
     * @param side      operational direction of the trade (BUY/SELL).
     * @param quantity  amount of nominal units. Must be greater than zero.
     * @param price     agreed execution unit price. Must be greater than zero.
     * @return a new, valid {@link Order} instance ready for use-case orchestration.
     * @throws IllegalArgumentException if numerical thresholds violate business invariants.
     */
    public static Order createNew(String accountId, String ticker, OrderSide side, BigDecimal quantity, Money price) {
        validateInputOrThrow(accountId, ticker, side, quantity, price);

        return new Order(
                UUID.randomUUID().toString(),
                accountId,
                ticker.trim().toUpperCase(),
                side,
                quantity,
                price,
                price.multiply(quantity),
                OrderStatus.PENDING,
                OffsetDateTime.now(ZoneOffset.UTC)
        );
    }

    private static void validateInputOrThrow(String accountId, String ticker, OrderSide side, BigDecimal quantity, Money price) {
        if (accountId == null || accountId.isBlank())
            throw new IllegalArgumentException("Account ID cannot be null or empty");

        if (ticker == null || ticker.isBlank())
            throw new IllegalArgumentException("Ticker cannot be null or empty");

        if (side == null)
            throw new IllegalArgumentException("Order side must be specified (BUY/SELL)");

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Quantity must be strictly greater than zero");

        if (price == null || !price.isPositive())
            throw new IllegalArgumentException("Price must be strictly greater than zero");
    }

}