package marketdata.simulator;

import marketdata.event.MarketDataEvent;
import marketdata.event.MarketDataEvents;
import marketdata.security.SecurityInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class RandomPricingSimulator extends AbstractPricingSimulator {
    private static final Random random = new Random();

    public RandomPricingSimulator(List<SecurityInfo> securities_, BlockingQueue<MarketDataEvents> eventQueue_) {
        super(securities_, eventQueue_);
    }

    @Override
    public void generateMarketDataUpdate() {
        // Randomly pick some securities from the list
        Set<SecurityInfo> selectedSecurities = getRandomSecurities();

        // Generate a combined market data update for selected securities
        ArrayList<MarketDataEvent> _eventList = new ArrayList();
        for (SecurityInfo security : selectedSecurities) {
            double randomChange = (random.nextDouble() - 0.5) * 10; // Random price change between -5 and 5
            BigDecimal newPrice = security.getCurrentPrice().add(BigDecimal.valueOf(randomChange));

            // Create MarketDataEvent
            MarketDataEvent event = new MarketDataEvent(security.getTicker(), newPrice, security.getCurrentPrice());
            _eventList.add(event);
        }
        //Publish update
        publishUpdate(_eventList);
    }

}
