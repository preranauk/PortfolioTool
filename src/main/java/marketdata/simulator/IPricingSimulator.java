package marketdata.simulator;

public interface IPricingSimulator {
    void start();

    void generateMarketDataUpdate();

    void stop();
}
