package ar.com.leguitech.fintechcoreservice.domain.model;

/**
 * Domain enumeration classifying the legal and operational nature of financial instruments
 * tradeable within the simulator.
 * <p>
 * This classification drives specific settlement rules, market hours, and valuation strategies
 * across the core execution engine.
 * </p>
 *
 * @author Walter Ariel Leguiza
 * @version 1.0
 * @see Asset
 * @since 2026-06
 */
public enum AssetType {

    /**
     * Equity security representing fractional ownership in a corporation (e.g., Apple, Microsoft).
     * Subject to real-time market price volatility.
     */
    STOCK,

    /**
     * Fixed-income debt security issued by a government or corporate entity,
     * featuring specific maturity dates and coupon structures.
     */
    BOND,

    /**
     * Certificado de Depósito Argentino (Argentine Depositary Receipt).
     * Represents foreign underlying shares trading locally in the Argentine market,
     * subject to local currency fluctuations and CCL (Contado con Liquidación) ratio conversions.
     */
    CEDEAR
}