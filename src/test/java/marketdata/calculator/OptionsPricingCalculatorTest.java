package marketdata.calculator;

import marketdata.security.SecurityInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionsPricingCalculatorTest {

    private OptionsPricingCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new OptionsPricingCalculator();
    }

    @Test
    public void testCalculateCallOptionPrice() {
        SecurityInfo securityInfo = createMockSecurityInfo();
        BigDecimal newPrice = BigDecimal.valueOf(100);

        BigDecimal callOptionPrice = calculator.calculateCallOptionPrice(securityInfo, newPrice);

        // Verify the call option price
        assertEquals(BigDecimal.valueOf(12.454), callOptionPrice.setScale(3, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void testCalculatePutOptionPrice() {
        SecurityInfo securityInfo = createMockSecurityInfo();
        BigDecimal newPrice = BigDecimal.valueOf(100);

        BigDecimal putOptionPrice = calculator.calculatePutOptionPrice(securityInfo, newPrice);

        // Verify the put option price
        assertEquals(BigDecimal.valueOf(1.558), putOptionPrice.setScale(3, BigDecimal.ROUND_HALF_UP));
    }

    private SecurityInfo createMockSecurityInfo() {
        // Mock necessary parameters
        BigDecimal strikePrice = BigDecimal.valueOf(100.0);
        double timeToMaturity = 0.5; // Example value in years
        BigDecimal volatility = BigDecimal.valueOf(0.2); // Example volatility

        // Stub values
        SecurityInfo securityInfo = Mockito.mock(SecurityInfo.class);
        Mockito.when(securityInfo.getStrikePrice()).thenReturn(strikePrice);
        Mockito.when(securityInfo.getStandardDeviation()).thenReturn(volatility);
        Mockito.when(securityInfo.getTimeToMaturity()).thenReturn(timeToMaturity);
        Mockito.when(securityInfo.getStrikePrice()).thenReturn(BigDecimal.valueOf(90));

        return securityInfo;
    }
}

