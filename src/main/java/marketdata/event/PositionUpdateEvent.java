package marketdata.event;

import java.math.BigDecimal;
import java.util.Objects;

public class PositionUpdateEvent {

    private final String _symbol;
    private final BigDecimal _newPrice;
    private final int _positionQuantity;
    private final BigDecimal _profitLoss;
    private final BigDecimal _newPosition;

    public PositionUpdateEvent(String symbol_, BigDecimal newPrice_, int positionQuantity_, BigDecimal profitLoss_, BigDecimal newPosition_) {
        _symbol = symbol_;
        _newPrice = newPrice_;
        _positionQuantity = positionQuantity_;
        _profitLoss = profitLoss_;
        _newPosition = newPosition_;
    }

    public String getSymbol() {
        return _symbol;
    }

    public BigDecimal getNewPrice() {
        return _newPrice;
    }

    public int getPositionQuantity() {
        return _positionQuantity;
    }

    public BigDecimal getProfitLoss() {
        return _profitLoss;
    }

    public BigDecimal getNewPosition() {
        return _newPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionUpdateEvent that = (PositionUpdateEvent) o;
        return _positionQuantity == that._positionQuantity && Objects.equals(_symbol, that._symbol) && Objects.equals(_newPrice, that._newPrice) && Objects.equals(_profitLoss, that._profitLoss) && Objects.equals(_newPosition, that._newPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_symbol, _newPrice, _positionQuantity, _profitLoss, _newPosition);
    }

    @Override
    public String toString() {
        return "PositionUpdate{" +
                "_symbol='" + _symbol + '\'' +
                ", _newPrice=" + _newPrice +
                ", _positionQuantity=" + _positionQuantity +
                ", _profitLoss=" + _profitLoss +
                ", _newPosition=" + _newPosition +
                '}';
    }
}
