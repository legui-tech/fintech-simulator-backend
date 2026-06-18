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

class OrderTest {

    @Test
    void createNewOrder_shouldReturnValidOrder() {
        String accountId = "testAccount123";
        String ticker = "AAPL";
        OrderSide side = OrderSide.BUY;
        BigDecimal quantity = BigDecimal.TEN;
        Money price = Money.of(BigDecimal.valueOf(150.50));

        Order order = Order.createNew(accountId, ticker, side, quantity, price);

        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(accountId, order.getAccountId());
        assertEquals(ticker.toUpperCase(), order.getTicker()); // Ticker should be uppercase
        assertEquals(side, order.getSide());
        assertEquals(quantity, order.getQuantity());
        assertEquals(price, order.getPrice());
        assertEquals(price.multiply(quantity), order.getTotalAmount()); // Use Money's multiply
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertEquals(ZoneOffset.UTC, order.getCreatedAt().getOffset()); // Check UTC offset
    }

    static Stream<Arguments> invalidAccountIdAndTicker() {
        return Stream.of(
                Arguments.of(null, "AAPL", "Account ID cannot be null or empty"),
                Arguments.of("   ", "AAPL", "Account ID cannot be null or empty"),
                Arguments.of("testAccount123", null, "Ticker cannot be null or empty"),
                Arguments.of("testAccount123", "   ", "Ticker cannot be null or empty")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAccountIdAndTicker")
    void createNewOrder_shouldThrowException_whenAccountIdOrTickerIsBlank(String accountId, String ticker, String expectedMessage) {
        OrderSide side = OrderSide.BUY;
        BigDecimal quantity = BigDecimal.TEN;
        Money price = Money.of(BigDecimal.valueOf(150.50));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Order.createNew(accountId, ticker, side, quantity, price));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void createNewOrder_shouldThrowException_whenSideIsNull() {
        String accountId = "testAccount123";
        String ticker = "AAPL";
        OrderSide side = null;
        BigDecimal quantity = BigDecimal.TEN;
        Money price = Money.of(BigDecimal.valueOf(150.50));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Order.createNew(accountId, ticker, side, quantity, price));

        assertEquals("Order side must be specified (BUY/SELL)", exception.getMessage());
    }

    @Test
    void createNewOrder_shouldThrowException_whenQuantityIsNull() {
        String accountId = "testAccount123";
        String ticker = "AAPL";
        OrderSide side = OrderSide.BUY;
        BigDecimal quantity = null;
        Money price = Money.of(BigDecimal.valueOf(150.50));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Order.createNew(accountId, ticker, side, quantity, price));

        assertEquals("Quantity must be strictly greater than zero", exception.getMessage());
    }

    @Test
    void createNewOrder_shouldThrowException_whenQuantityIsZero() {
        String accountId = "testAccount123";
        String ticker = "AAPL";
        OrderSide side = OrderSide.BUY;
        BigDecimal quantity = BigDecimal.ZERO;
        Money price = Money.of(BigDecimal.valueOf(150.50));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Order.createNew(accountId, ticker, side, quantity, price));

        assertEquals("Quantity must be strictly greater than zero", exception.getMessage());
    }

    @Test
    void createNewOrder_shouldThrowException_whenQuantityIsNegative() {
        String accountId = "testAccount123";
        String ticker = "AAPL";
        OrderSide side = OrderSide.BUY;
        BigDecimal quantity = BigDecimal.valueOf(-5);
        Money price = Money.of(BigDecimal.valueOf(150.50));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Order.createNew(accountId, ticker, side, quantity, price));

        assertEquals("Quantity must be strictly greater than zero", exception.getMessage());
    }

    @Test
    void createNewOrder_shouldThrowException_whenPriceIsNull() {
        String accountId = "testAccount123";
        String ticker = "AAPL";
        OrderSide side = OrderSide.BUY;
        BigDecimal quantity = BigDecimal.TEN;
        Money price = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Order.createNew(accountId, ticker, side, quantity, price));

        assertEquals("Price must be strictly greater than zero", exception.getMessage());
    }

    @Test
    void createNewOrder_shouldThrowException_whenPriceIsNotPositive() {
        String accountId = "testAccount123";
        String ticker = "AAPL";
        OrderSide side = OrderSide.BUY;
        BigDecimal quantity = BigDecimal.TEN;
        Money price = Money.of(BigDecimal.ZERO); // Not positive

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Order.createNew(accountId, ticker, side, quantity, price));

        assertEquals("Price must be strictly greater than zero", exception.getMessage());
    }

    @Test
    void restoreOrder_shouldReturnValidOrder() {
        String id = "some-uuid";
        String accountId = "testAccount123";
        String ticker = "GOOG";
        OrderSide side = OrderSide.SELL;
        BigDecimal quantity = BigDecimal.valueOf(5);
        Money price = Money.of(BigDecimal.valueOf(1000.00));
        Money totalAmount = Money.of(BigDecimal.valueOf(5000.00));
        OrderStatus status = OrderStatus.COMPLETED;
        OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);

        Order order = Order.restore(id, accountId, ticker, side, quantity, price, totalAmount, status, createdAt);

        assertNotNull(order);
        assertEquals(id, order.getId());
        assertEquals(accountId, order.getAccountId());
        assertEquals(ticker, order.getTicker());
        assertEquals(side, order.getSide());
        assertEquals(quantity, order.getQuantity());
        assertEquals(price, order.getPrice());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(status, order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
    }
}