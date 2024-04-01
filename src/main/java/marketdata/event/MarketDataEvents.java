package marketdata.event;

import java.util.List;

public class MarketDataEvents {
    List<MarketDataEvent> _events;

    public MarketDataEvents(List<MarketDataEvent> events_) {
        _events = events_;
    }

    public List<MarketDataEvent> getEvents() {
        return _events;
    }
}
