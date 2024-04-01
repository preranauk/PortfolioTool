package marketdata.subscriber;

import marketdata.calculator.OptionsPricingCalculator;
import marketdata.event.MarketDataEvent;
import marketdata.event.MarketDataEvents;
import marketdata.event.PositionUpdateEvent;
import marketdata.position.PositionData;
import marketdata.position.PositionDataProvider;
import marketdata.security.SecurityInfo;
import marketdata.security.SecurityInfoProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class MarketDataSubscriber implements Runnable {
    private final BlockingQueue<MarketDataEvents> _eventQueue;
    private final SecurityInfoProvider _securityInfoCache;
    private final Map<String, PositionData> _positionCache;
    private final OptionsPricingCalculator _calculator;
    private final BlockingQueue<Map<MarketDataEvent, List<PositionUpdateEvent>>> _portfolioUpdateQueue;

    public MarketDataSubscriber(BlockingQueue<MarketDataEvents> eventQueue_, SecurityInfoProvider securityInfoCache_,
                                PositionDataProvider positionDataProcessor_,
                                BlockingQueue<Map<MarketDataEvent, List<PositionUpdateEvent>>> portfolioUpdateQueue_) {
        _eventQueue = eventQueue_;
        _portfolioUpdateQueue = portfolioUpdateQueue_;
        _securityInfoCache = securityInfoCache_;
        _positionCache = positionDataProcessor_.getPositionCache();
        _calculator = new OptionsPricingCalculator();
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Block until an event is available in the queue
                MarketDataEvents event = _eventQueue.take();

                // Process the event
                Map<MarketDataEvent, List<PositionUpdateEvent>> portfolioUpdate = processMarketDataEvent(event);
                _portfolioUpdateQueue.offer(portfolioUpdate);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Map<MarketDataEvent, List<PositionUpdateEvent>> processMarketDataEvent(MarketDataEvents events) {
        Map<MarketDataEvent, List<PositionUpdateEvent>> portfolioUpdates = new HashMap();
        for (MarketDataEvent event : events.getEvents()) {
            String ticker = event.getTicker();
            List<SecurityInfo> securities = _securityInfoCache.getSecuritiesForTicker(ticker);
            ArrayList<PositionUpdateEvent> positionUpdateList = new ArrayList<>();
            for (SecurityInfo security : securities) {
                PositionData position = _positionCache.get(security.getIdentfier());
                if (position != null) {
                    // Calculate updates for the position
                    BigDecimal newPrice = null;
                    switch (position.getType()) {
                        case Stock:
                            newPrice = event.getNewPrice();
                            break;
                        case Put:
                            newPrice = _calculator.calculatePutOptionPrice(security, event.getNewPrice());
                            break;
                        case Call:
                            newPrice = _calculator.calculateCallOptionPrice(security, event.getNewPrice());
                            break;
                    }
                    PositionUpdateEvent positionUpdate = calculatePositionUpdate(security, position, newPrice);
                    if (positionUpdate != null) {
                        positionUpdateList.add(positionUpdate);
                    }
                }
            }
            portfolioUpdates.put(event, positionUpdateList);
        }
        return portfolioUpdates;
    }

    private PositionUpdateEvent calculatePositionUpdate(SecurityInfo securityInfo_, PositionData position_,
                                                        BigDecimal newPrice_) {
        if (newPrice_ == null) {
            return null;
        }
        BigDecimal currentPrice = securityInfo_.getCurrentPrice();
        int positionQuantity = position_.getQuantity();

        // Calculate profit/loss
        BigDecimal profitLoss = newPrice_.subtract(currentPrice).multiply(BigDecimal.valueOf(positionQuantity));

        // Update position quantity based on the new price
        BigDecimal newPosition = newPrice_.multiply(BigDecimal.valueOf(positionQuantity));

        // Create PositionUpdateEvent
        PositionUpdateEvent positionUpdate = new PositionUpdateEvent(position_.getIdentifier(), newPrice_,
                positionQuantity, profitLoss, newPosition);
        return positionUpdate;
    }
}
