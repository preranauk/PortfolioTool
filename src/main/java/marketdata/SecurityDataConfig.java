package marketdata;

import com.opencsv.exceptions.CsvValidationException;
import marketdata.event.MarketDataEvent;
import marketdata.event.MarketDataEvents;
import marketdata.event.PositionUpdateEvent;
import marketdata.position.PositionDataProvider;
import marketdata.security.MockSecurityData;
import marketdata.security.SecurityInfoProvider;
import marketdata.simulator.MarketDataSimulator;
import marketdata.subscriber.MarketDataSubscriber;
import marketdata.subscriber.PortfolioUpdateSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class SecurityDataConfig {

    public final BlockingQueue<MarketDataEvents> _eventQueue = new LinkedBlockingQueue();
    public final BlockingQueue<Map<MarketDataEvent, List<PositionUpdateEvent>>> _portfolioUpdateQueue = new LinkedBlockingQueue();
    public DBConnectionProvider _dbConnectionProvider;
    private final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"; // JDBC URL for the embedded H2 database
    private final String USERNAME = "sa"; // Default username for H2
    private final String PASS = ""; // Default password for H2
    private final String SECURITY_FILE_PATH = "marketdata/security/SecurityData.csv";
    private final String POSITION_FILE_PATH = "marketdata/position/positions.csv";

    public SecurityDataConfig() throws SQLException {
        _dbConnectionProvider = new DBConnectionProvider(JDBC_URL, USERNAME, PASS);
        MockSecurityData mockSecurityData = new MockSecurityData(_dbConnectionProvider.getConnection(),
                SECURITY_FILE_PATH);
        // mock data
        mockSecurityData.mockData();
    }

    @Bean
    public SecurityInfoProvider securityInfoProvider() {
        try {
            return new SecurityInfoProvider(_dbConnectionProvider.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException("Error creating SecurityInfoProvider bean", e);
        }
    }

    @Bean
    public PositionDataProvider positionDataProcessor() {
        PositionDataProvider processor = new PositionDataProvider(POSITION_FILE_PATH, securityInfoProvider());
        return processor;
    }

    @Bean
    public MarketDataSubscriber marketDataSubscriber() {
        MarketDataSubscriber subscriber = new MarketDataSubscriber(_eventQueue, securityInfoProvider(),
                positionDataProcessor(), _portfolioUpdateQueue);
        return subscriber;
    }

    @Bean
    public PortfolioUpdateSubscriber portfolioUpdateSubscriber() {
        PortfolioUpdateSubscriber subscriber = new PortfolioUpdateSubscriber(_portfolioUpdateQueue);
        return subscriber;
    }

    @Bean
    public MarketDataSimulator simulator() {
        return new MarketDataSimulator(securityInfoProvider().getSecurityList(), _eventQueue);
    }

    @Bean
    public TaskManager taskManager() throws CsvValidationException, IOException {
        return new TaskManager(positionDataProcessor(), marketDataSubscriber(), portfolioUpdateSubscriber());
    }
}
