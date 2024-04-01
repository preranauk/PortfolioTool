package marketdata.simulator;

import marketdata.event.MarketDataEvent;
import marketdata.event.MarketDataEvents;
import marketdata.security.SecurityInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class BrownianMotionSimulator extends AbstractPricingSimulator {
    private static final BigDecimal SECONDS_IN_YEAR = BigDecimal.valueOf(7257600.0); // Seconds in a year
    private Random random = new Random();

    public BrownianMotionSimulator(List<SecurityInfo> securities_, BlockingQueue<MarketDataEvents> eventQueue_) {
        super(securities_, eventQueue_);
    }

    @Override
    public void generateMarketDataUpdate() {
        // Randomly pick some securities from the list
        Set<SecurityInfo> selectedSecurities = getRandomSecurities();

        // Generate a random variable
        double rand = random.nextGaussian();

        // Generate a combined market data update for selected securities
        ArrayList<MarketDataEvent> _eventList = new ArrayList();
        for (SecurityInfo security : selectedSecurities) {
            // Generate a random time interval between 0.5 and 2 seconds
            BigDecimal interval = BigDecimal.valueOf(0.5 + random.nextDouble() * 1.5);

            // Calculate price update based on Brownian motion
            BigDecimal deltaChange = security.getReturnOnStock()
                    .multiply(interval.divide(SECONDS_IN_YEAR, RoundingMode.HALF_UP))
                    .add(security.getStandardDeviation()
                            .multiply(BigDecimal.valueOf(rand))
                            .multiply(BigDecimal.valueOf(
                                    Math.sqrt(interval.doubleValue() / SECONDS_IN_YEAR.doubleValue()))));

            // Update security price
            BigDecimal newPrice = security.getCurrentPrice().add(deltaChange.multiply(security.getCurrentPrice()));

            // Create MarketDataEvent
            MarketDataEvent event = new MarketDataEvent(security.getTicker(), newPrice, security.getCurrentPrice());
            _eventList.add(event);
        }

        //Publish update
        publishUpdate(_eventList);
    }

}
