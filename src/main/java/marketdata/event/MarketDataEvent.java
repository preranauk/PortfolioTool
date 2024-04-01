package marketdata.event;

import java.math.BigDecimal;
import java.util.Objects;

public class MarketDataEvent {
    private final String _ticker;
    private final BigDecimal _newPrice;
    private final BigDecimal _oldPrice;

    public MarketDataEvent(String ticker_, BigDecimal newPrice_, BigDecimal oldPrice_) {
        _ticker = ticker_;
        _newPrice = newPrice_;
        _oldPrice = oldPrice_;
    }

    // Getter methods
    public String getTicker() {
        return _ticker;
    }

    public BigDecimal getNewPrice() {
        return _newPrice;
    }

    public BigDecimal getOldPrice() {
        return _oldPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketDataEvent that = (MarketDataEvent) o;
        return Objects.equals(_ticker, that._ticker) && Objects.equals(_newPrice, that._newPrice) && Objects.equals(
                _oldPrice, that._oldPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_ticker, _newPrice, _oldPrice);
    }

    @Override
    public String toString() {
        return "MarketDataEvent{" +
                "_ticker='" + _ticker + '\'' +
                ", _newPrice=" + _newPrice +
                ", _oldPrice=" + _oldPrice +
                '}';
    }
}

