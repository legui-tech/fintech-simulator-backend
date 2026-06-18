package ar.com.leguitech.fintechcoreservice.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    private static final String USD = "USD";
    private static final String EUR = "EUR";
    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    @Test
    void of_withAmountAndCurrency_shouldCreateMoneyInstance() {
        BigDecimal amount = new BigDecimal("100.12345");
        Money money = Money.of(amount, USD);

        assertNotNull(money);
        assertEquals(new BigDecimal("100.1234").setScale(SCALE, ROUNDING_MODE), money.getAmount());
        assertEquals(USD, money.getCurrency());
    }

    @Test
    void of_withAmountAndCurrency_shouldHandleNullAmount() {
        Money money = Money.of(null, USD);

        assertNotNull(money);
        assertEquals(BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE), money.getAmount());
        assertEquals(USD, money.getCurrency());
    }

    @Test
    void of_withAmountAndCurrency_shouldThrowExceptionForNullCurrency() {
        BigDecimal amount = new BigDecimal("100");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Money.of(amount, null));
        assertEquals("Currency code cannot be null or blank", exception.getMessage());
    }

    @Test
    void of_withAmountAndCurrency_shouldThrowExceptionForBlankCurrency() {
        BigDecimal amount = new BigDecimal("100");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Money.of(amount, "   "));
        assertEquals("Currency code cannot be null or blank", exception.getMessage());
    }

    @Test
    void of_withAmountAndCurrency_shouldThrowExceptionForInvalidCurrency() {
        BigDecimal amount = new BigDecimal("100");
        assertThrows(IllegalArgumentException.class, () ->
                Money.of(amount, "XYZ"));
    }

    @Test
    void of_withAmount_shouldCreateMoneyInstanceWithDefaultUSDCurrency() {
        BigDecimal amount = new BigDecimal("200.56789");
        Money money = Money.of(amount);

        assertNotNull(money);
        assertEquals(new BigDecimal("200.5679").setScale(SCALE, ROUNDING_MODE), money.getAmount());
        assertEquals(USD, money.getCurrency());
    }

    @Test
    void multiply_shouldReturnCorrectResult() {
        Money baseMoney = Money.of(new BigDecimal("10.50"), USD);
        BigDecimal factor = new BigDecimal("2.5");
        Money result = baseMoney.multiply(factor);

        assertNotNull(result);
        assertEquals(new BigDecimal("26.2500").setScale(SCALE, ROUNDING_MODE), result.getAmount());
        assertEquals(USD, result.getCurrency());
    }

    @Test
    void multiply_shouldHandleZeroFactor() {
        Money baseMoney = Money.of(new BigDecimal("10.50"), USD);
        BigDecimal factor = BigDecimal.ZERO;
        Money result = baseMoney.multiply(factor);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE), result.getAmount());
        assertEquals(USD, result.getCurrency());
    }

    @Test
    void multiply_shouldHandleNegativeFactor() {
        Money baseMoney = Money.of(new BigDecimal("10.00"), USD);
        BigDecimal factor = new BigDecimal("-2.0");
        Money result = baseMoney.multiply(factor);

        assertNotNull(result);
        assertEquals(new BigDecimal("-20.0000").setScale(SCALE, ROUNDING_MODE), result.getAmount());
        assertEquals(USD, result.getCurrency());
    }

    @Test
    void multiply_shouldThrowExceptionForNullFactor() {
        Money baseMoney = Money.of(new BigDecimal("10.00"), USD);
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                baseMoney.multiply(null));
        assertEquals("Multiplication factor must not be null", exception.getMessage());
    }

    @Test
    void add_shouldReturnCorrectResultWithSameCurrency() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        Money moneyB = Money.of(new BigDecimal("5.25"), USD);
        Money result = moneyA.add(moneyB);

        assertNotNull(result);
        assertEquals(new BigDecimal("15.7500").setScale(SCALE, ROUNDING_MODE), result.getAmount());
        assertEquals(USD, result.getCurrency());
    }

    @Test
    void add_shouldThrowExceptionForDifferentCurrencies() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        Money moneyB = Money.of(new BigDecimal("5.25"), EUR);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                moneyA.add(moneyB));
        assertEquals("Currency mismatch: Cannot operate between USD and EUR", exception.getMessage());
    }

    @Test
    void add_shouldThrowExceptionForNullOtherMoney() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                moneyA.add(null));
        assertEquals("Addition value must not be null", exception.getMessage());
    }

    @Test
    void subtract_shouldReturnCorrectResultWithSameCurrency() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        Money moneyB = Money.of(new BigDecimal("5.25"), USD);
        Money result = moneyA.subtract(moneyB);

        assertNotNull(result);
        assertEquals(new BigDecimal("5.2500").setScale(SCALE, ROUNDING_MODE), result.getAmount());
        assertEquals(USD, result.getCurrency());
    }

    @Test
    void subtract_shouldThrowExceptionForDifferentCurrencies() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        Money moneyB = Money.of(new BigDecimal("5.25"), EUR);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                moneyA.subtract(moneyB));
        assertEquals("Currency mismatch: Cannot operate between USD and EUR", exception.getMessage());
    }

    @Test
    void subtract_shouldThrowExceptionForNullOtherMoney() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                moneyA.subtract(null));
        assertEquals("Subtraction value must not be null", exception.getMessage());
    }

    @Test
    void isPositive_shouldReturnTrueForPositiveAmount() {
        Money money = Money.of(new BigDecimal("10.00"), USD);
        assertTrue(money.isPositive());
    }

    @Test
    void isPositive_shouldReturnFalseForZeroAmount() {
        Money money = Money.of(BigDecimal.ZERO, USD);
        assertFalse(money.isPositive());
    }

    @Test
    void isPositive_shouldReturnFalseForNegativeAmount() {
        Money money = Money.of(new BigDecimal("-10.00"), USD);
        assertFalse(money.isPositive());
    }

    @Test
    void compareTo_shouldReturnZeroForEqualAmountsAndCurrencies() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        Money moneyB = Money.of(new BigDecimal("10.5000"), USD);
        assertEquals(0, moneyA.compareTo(moneyB));
    }

    @Test
    void compareTo_shouldReturnPositiveForGreaterAmount() {
        Money moneyA = Money.of(new BigDecimal("15.00"), USD);
        Money moneyB = Money.of(new BigDecimal("10.00"), USD);
        assertTrue(moneyA.compareTo(moneyB) > 0);
    }

    @Test
    void compareTo_shouldReturnNegativeForSmallerAmount() {
        Money moneyA = Money.of(new BigDecimal("5.00"), USD);
        Money moneyB = Money.of(new BigDecimal("10.00"), USD);
        assertTrue(moneyA.compareTo(moneyB) < 0);
    }

    @Test
    void compareTo_shouldThrowExceptionForDifferentCurrencies() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        Money moneyB = Money.of(new BigDecimal("10.50"), EUR);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                moneyA.compareTo(moneyB));
        assertEquals("Currency mismatch: Cannot operate between USD and EUR", exception.getMessage());
    }

    @Test
    void compareTo_shouldThrowExceptionForNullOtherMoney() {
        Money moneyA = Money.of(new BigDecimal("10.50"), USD);
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                moneyA.compareTo(null));
        assertEquals("Comparison target must not be null", exception.getMessage());
    }
}