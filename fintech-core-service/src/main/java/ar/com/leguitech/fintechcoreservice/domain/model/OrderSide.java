package ar.com.leguitech.fintechcoreservice.domain.model;

/**
 * Domain enumeration representing the operational direction of a financial transaction.
 * <p>
 * This enum determines whether an {@link Order} is intended to acquire or liquidate
 * a specific asset within the trading system.
 * </p>
 *
 * @author Walter Ariel Leguiza
 * @version 1.0
 * @see Order
 * @since 2026-06
 */
public enum OrderSide {

    /**
     * Represents a buy operation (acquisition of nominal units).
     * Execution will attempt to debit funds from the account balance and credit assets to the portfolio.
     */
    BUY,

    /**
     * Represents a sell operation (liquidation of nominal units).
     * Execution will attempt to debit assets from the portfolio and credit funds to the account balance.
     */
    SELL
}
