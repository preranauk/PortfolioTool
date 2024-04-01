package marketdata.calculator;

import marketdata.security.SecurityInfo;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.math.BigDecimal;
import java.math.MathContext;

public class OptionsPricingCalculator {
    private final NormalDistribution normalDistribution = new NormalDistribution();

    public BigDecimal calculateCallOptionPrice(SecurityInfo securityInfo_, BigDecimal newPrice_) {
        return calculateOptionPrice(securityInfo_, newPrice_, false);
    }

    public BigDecimal calculatePutOptionPrice(SecurityInfo securityInfo_, BigDecimal newPrice_) {
        return calculateOptionPrice(securityInfo_, newPrice_, true);
    }

    private BigDecimal calculateOptionPrice(SecurityInfo securityInfo_, BigDecimal newPrice_, boolean isPut_) {
        if (securityInfo_ == null || newPrice_ == null || newPrice_.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal timeToMaturity = BigDecimal.valueOf(securityInfo_.getTimeToMaturity());
        BigDecimal strikePrice = securityInfo_.getStrikePrice();
        BigDecimal volatility = securityInfo_.getStandardDeviation();

        BigDecimal d1 = calculateD1(strikePrice, newPrice_, timeToMaturity, volatility);
        BigDecimal d2 = d1.subtract(volatility.multiply(BigDecimal.valueOf(Math.sqrt(timeToMaturity.doubleValue()))));
        BigDecimal riskFreeRate = BigDecimal.valueOf(0.02); // Risk-free interest rate (2%)

        BigDecimal t1 = newPrice_.multiply(BigDecimal.valueOf(
                normalDistribution.cumulativeProbability(isPut_ ? -d1.doubleValue() : d1.doubleValue())));
        BigDecimal t2 = strikePrice.multiply(
                        BigDecimal.valueOf(Math.exp(-riskFreeRate.doubleValue() * timeToMaturity.doubleValue())))
                .multiply(BigDecimal.valueOf(
                        normalDistribution.cumulativeProbability(isPut_ ? -d2.doubleValue() : d2.doubleValue())));

        return isPut_ ? t2.subtract(t1) : t1.subtract(t2);
    }

    private BigDecimal calculateD1(BigDecimal strikePrice, BigDecimal newPrice, BigDecimal timeToMaturity,
                                   BigDecimal volatility) {
        BigDecimal rate = BigDecimal.valueOf(0.02); // Risk-free interest rate (2%)

        BigDecimal numerator = BigDecimal.valueOf(Math.log(newPrice.doubleValue() / strikePrice.doubleValue()))
                .add(rate.add(volatility.pow(2).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128))
                        .multiply(timeToMaturity));
        BigDecimal denominator = volatility.multiply(BigDecimal.valueOf(Math.sqrt(timeToMaturity.doubleValue())));

        return numerator.divide(denominator, MathContext.DECIMAL128);
    }
}
