package marketdata.simulator;

import marketdata.event.MarketDataEvents;
import marketdata.security.SecurityInfo;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MarketDataSimulator {

    private final List<SecurityInfo> _securities;
    private final BlockingQueue<MarketDataEvents> _eventQueue;

    public MarketDataSimulator(List<SecurityInfo> securities_, BlockingQueue<MarketDataEvents> eventQueue_) {
        _securities = securities_;
        _eventQueue = eventQueue_;
    }

    public void startSimulation(boolean useRandomPricing_) {
        if (useRandomPricing_) {
            IPricingSimulator randomPricingSimulator = new RandomPricingSimulator(_securities, _eventQueue);
            randomPricingSimulator.start();
        } else {
            IPricingSimulator brownianMotionSimulator = new BrownianMotionSimulator(_securities, _eventQueue);
            brownianMotionSimulator.start();
        }
    }
}
