package marketdata;

import com.opencsv.exceptions.CsvValidationException;
import marketdata.position.PositionDataProvider;
import marketdata.subscriber.MarketDataSubscriber;
import marketdata.subscriber.PortfolioUpdateSubscriber;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManager {
    private final PositionDataProvider _positionDataProcessor;
    private final MarketDataSubscriber _marketDataSubscriber;
    private final PortfolioUpdateSubscriber _portfolioUpdateSubscriber;

    private final ExecutorService _executor = Executors.newFixedThreadPool(3);

    public TaskManager(PositionDataProvider positionDataProcessor_, MarketDataSubscriber marketDataSubscriber_,
                       PortfolioUpdateSubscriber portfolioUpdateSubscriber_) throws CsvValidationException, IOException {

        _positionDataProcessor = positionDataProcessor_;
        _marketDataSubscriber = marketDataSubscriber_;
        _portfolioUpdateSubscriber = portfolioUpdateSubscriber_;

        submitTask();
    }

    private void submitTask() throws CsvValidationException, IOException {
        //First process existing position
        _positionDataProcessor.processData();
        // Now simulate market data
        _executor.submit(_marketDataSubscriber);
        //Generate portfolio updates
        _executor.submit(_portfolioUpdateSubscriber);
    }


}
