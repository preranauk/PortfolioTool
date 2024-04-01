package marketdata.position;

import marketdata.security.SecurityType;

import java.util.Objects;

public class PositionData {
    private final String _ticker;
    private final String _identifier;
    private final SecurityType _type;
    private final int _quantity;

    public PositionData(String ticker_, String identifier_, int quantity_, SecurityType type_) {
        _ticker = ticker_;
        _identifier = identifier_;
        _quantity = quantity_;
        _type = type_;
    }

    public String getTicker() {
        return _ticker;
    }

    public String getIdentifier() {
        return _identifier;
    }

    public int getQuantity() {
        return _quantity;
    }

    public SecurityType getType() {
        return _type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionData that = (PositionData) o;
        return _quantity == that._quantity && Objects.equals(_ticker, that._ticker) && Objects.equals(_identifier,
                that._identifier) && _type == that._type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_ticker, _identifier, _type, _quantity);
    }

    @Override
    public String toString() {
        return "PositionData{" +
                "_ticker='" + _ticker + '\'' +
                ", _identifier='" + _identifier + '\'' +
                ", _type=" + _type +
                ", _quantity=" + _quantity +
                '}';
    }
}

