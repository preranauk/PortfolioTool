package marketdata.security;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SecurityInfoProvider {
    private static final long CACHE_REFRESH_TIME = Duration.ofMinutes(60).toMillis();
    private static final String QUERY_ALL = "SELECT * FROM securities";
    private final Connection _connection;
    private final Map<String, SecurityInfo> _securityCache;
    private final List<SecurityInfo> _securityList;
    ScheduledExecutorService _executor = Executors.newScheduledThreadPool(1);

    public SecurityInfoProvider(Connection connection_) throws SQLException {
        _connection = connection_;
        _securityCache = new HashMap();
        _securityList = new ArrayList();

        loadSecurityDataIntoCache();

        // Schedule cache refresh task to periodically refresh values in security cache
        scheduleCacheRefresh();
    }

    private void scheduleCacheRefresh() {
        _executor.scheduleAtFixedRate(() -> refreshCache(), CACHE_REFRESH_TIME, CACHE_REFRESH_TIME,
                TimeUnit.MILLISECONDS);
    }

    private void refreshCache() {
        // Clear existing cache
        _securityCache.clear();
        // Reload data into cache
        loadSecurityDataIntoCache();
    }

    private void loadSecurityDataIntoCache() {
        try (PreparedStatement preparedStatement = _connection.prepareStatement(QUERY_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                SecurityInfo securityInfo = createSecurityInfoFromResultSet(resultSet);
                if (securityInfo != null) {
                    _securityCache.put(securityInfo.getIdentfier(), securityInfo);
                    if (securityInfo.getType() == SecurityType.Stock) {
                        _securityList.add(securityInfo);
                    }
                }
            }
        } catch (SQLException e_) {
            throw new RuntimeException("Error loading security data into cache", e_);
        }
    }

    public SecurityInfo getSecurity(String identifier_) {
        return _securityCache.get(identifier_);
    }

    public List<SecurityInfo> getSecuritiesForTicker(String ticker_) {
        return _securityCache.values().stream().filter(x -> x.getTicker().equalsIgnoreCase(ticker_))
                .collect(Collectors.toList());
    }

    public List<SecurityInfo> getSecurityList() {
        return _securityList;
    }

    private SecurityInfo createSecurityInfoFromResultSet(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("id");
            String ticker = resultSet.getString("ticker");
            String identifier = resultSet.getString("identifier");
            SecurityType type = SecurityType.valueOf(resultSet.getString("type"));
            LocalDate maturityDate = resultSet.getDate("maturity_date").toLocalDate();
            BigDecimal strikePrice = resultSet.getBigDecimal("strike_price");
            BigDecimal currentPrice = resultSet.getBigDecimal("current_price");
            BigDecimal stockVolatility = resultSet.getBigDecimal("stock_volatility");
            BigDecimal stockReturn = resultSet.getBigDecimal("stock_return");
            return new SecurityInfo(id, ticker, identifier, type, maturityDate, strikePrice, currentPrice,
                    stockVolatility, stockReturn);
        } catch (Exception e_) {
            System.out.println("Error while parsing result set:" + e_);
        }
        return null;
    }

}
