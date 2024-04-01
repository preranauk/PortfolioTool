package marketdata.security;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class SecurityInfo {
    private final int _id;
    private final String _ticker;
    private final String _identifier;
    private final SecurityType _type;
    private final LocalDate _maturityDate;
    private final BigDecimal _strikePrice;
    private final BigDecimal _currentPrice;
    private final BigDecimal _standardDeviation;
    private final BigDecimal _returnOnStock;

    public SecurityInfo(int id_, String ticker_, String identifier_, SecurityType type_, LocalDate maturityDate_,
                        BigDecimal strikePrice_,
                        BigDecimal currentPrice_, BigDecimal standardDeviation_, BigDecimal returnOnStock_) {
        _id = id_;
        _ticker = ticker_;
        _identifier = identifier_;
        _type = type_;
        _maturityDate = maturityDate_;
        _strikePrice = strikePrice_;
        _currentPrice = currentPrice_;
        _standardDeviation = standardDeviation_;
        _returnOnStock = returnOnStock_;
    }

    public int getId() {
        return _id;
    }

    public String getTicker() {
        return _ticker;
    }

    public String getIdentfier() {
        return _identifier;
    }

    public SecurityType getType() {
        return _type;
    }

    public LocalDate getMaturityDate() {
        return _maturityDate;
    }

    public BigDecimal getStrikePrice() {
        return _strikePrice;
    }

    public BigDecimal getCurrentPrice() {
        return _currentPrice;
    }

    public BigDecimal getStandardDeviation() {
        return _standardDeviation;
    }

    public BigDecimal getReturnOnStock() {
        return _returnOnStock;
    }

    public double getTimeToMaturity() {
        long daysToMaturity = ChronoUnit.DAYS.between(LocalDate.now(), _maturityDate);
        return daysToMaturity / 365.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityInfo that = (SecurityInfo) o;
        return _id == that._id && Objects.equals(_ticker, that._ticker) && Objects.equals(_identifier,
                that._identifier) && _type == that._type && Objects.equals(_maturityDate,
                that._maturityDate) && Objects.equals(_strikePrice, that._strikePrice) && Objects.equals(_currentPrice,
                that._currentPrice) && Objects.equals(_standardDeviation, that._standardDeviation) && Objects.equals(
                _returnOnStock, that._returnOnStock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _ticker, _identifier, _type, _maturityDate, _strikePrice, _currentPrice,
                _standardDeviation, _returnOnStock);
    }

    @Override
    public String toString() {
        return "SecurityInfo{" +
                "_id=" + _id +
                ", _ticker='" + _ticker + '\'' +
                ", _identifier='" + _identifier + '\'' +
                ", _type=" + _type +
                ", _maturityDate=" + _maturityDate +
                ", _strikePrice=" + _strikePrice +
                ", _currentPrice=" + _currentPrice +
                ", _standardDeviation=" + _standardDeviation +
                ", _returnOnStock=" + _returnOnStock +
                '}';
    }

}

