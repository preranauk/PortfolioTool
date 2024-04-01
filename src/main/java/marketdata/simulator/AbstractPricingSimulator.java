package marketdata.simulator;

import marketdata.event.MarketDataEvent;
import marketdata.event.MarketDataEvents;
import marketdata.security.SecurityInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

abstract class AbstractPricingSimulator implements IPricingSimulator {
    protected Random random = new Random();
    protected ScheduledExecutorService _executorService;
    protected List<SecurityInfo> _securities;
    protected BlockingQueue<MarketDataEvents> _eventQueue;

    public AbstractPricingSimulator(List<SecurityInfo> securities_, BlockingQueue<MarketDataEvents> eventQueue_) {
        _securities = securities_;
        _eventQueue = eventQueue_;
    }

    public abstract void generateMarketDataUpdate();

    protected Set<SecurityInfo> getRandomSecurities() {
        int numSecurities = random.nextInt(_securities.size()) + 1; // Pick at least one security
        Set<SecurityInfo> selectedSecurities = new HashSet<>();
        for (int i = 0; i < numSecurities; i++) {
            selectedSecurities.add(_securities.get(random.nextInt(_securities.size())));
        }
        return selectedSecurities;
    }

    protected long getRandomInterval() {
        return (long) (random.nextDouble() * 1500 + 500); // Random interval between 0.5 and 2 seconds
    }

    protected void publishUpdate(List<MarketDataEvent> eventList_) {
        _eventQueue.offer(new MarketDataEvents(eventList_));
    }

    public void start() {
        _executorService = Executors.newScheduledThreadPool(1);
        _executorService.scheduleAtFixedRate(this::generateMarketDataUpdate, 0, getRandomInterval(),
                TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (_executorService != null && !_executorService.isShutdown()) {
            _executorService.shutdown();
        }
    }
}