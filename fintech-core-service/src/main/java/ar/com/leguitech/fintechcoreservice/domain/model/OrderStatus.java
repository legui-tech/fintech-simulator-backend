package ar.com.leguitech.fintechcoreservice.domain.model;

/**
 * Domain enumeration representing the finite state lifecycle of a financial trading order.
 * <p>
 * Orders transition sequentially through these states based on business rule validations,
 * market availability, and transactional balance confirmations.
 * </p>
 *
 * @author Walter Ariel Leguiza
 * @version 1.0
 * @see Order
 * @since 2026-06
 */
public enum OrderStatus {

    /**
     * The order has been newly created and acknowledged by the core domain,
     * but has not yet undergone validation or ledger reservation.
     */
    PENDING,

    /**
     * The order has successfully cleared all business invariants and risk limits.
     * If applicable, corresponding transaction funds or assets are locked in reservation.
     */
    APPROVED,

    /**
     * The order has been matched and executed in the market.
     * Account balances and asset portfolios have been definitively updated. This is a terminal state.
     */
    COMPLETED,

    /**
     * The order was explicitly retracted by the user or an automated system
     * before market execution. This is a terminal state.
     */
    CANCELLED,

    /**
     * The order was turned down by the domain engine due to invariant breaches,
     * such as insufficient ledger funds, illegal thresholds, or inactive asset tickers. This is a terminal state.
     */
    REJECTED
}