package marketdata.subscriber;

import marketdata.event.MarketDataEvent;
import marketdata.event.PositionUpdateEvent;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PortfolioUpdateSubscriber implements Runnable {
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_ORANGE = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW_BACK = "\u001B[103m";
    public static final String ANSI_UNDERLINE = "\u001B[4m";
    public static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private static final String[] TABLE_HEADERS = {"Symbol", "Price", "Qty", "Value"};
    private final BlockingQueue<Map<MarketDataEvent, List<PositionUpdateEvent>>> _portfolioUpdateQueue;
    private int eventCounter = 1;

    public PortfolioUpdateSubscriber(
            BlockingQueue<Map<MarketDataEvent, List<PositionUpdateEvent>>> portfolioUpdateQueue_) {
        _portfolioUpdateQueue = portfolioUpdateQueue_;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Block until an event is available in the queue
                Map<MarketDataEvent, List<PositionUpdateEvent>> event = _portfolioUpdateQueue.take();

                // Process the event
                printData(event);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void printData(Map<MarketDataEvent, List<PositionUpdateEvent>> updates_) {
        System.out.println(
                ANSI_UNDERLINE + ANSI_BOLD + ANSI_BLUE + "##" + (eventCounter++) + " Market Data Update" + ANSI_RESET);
        updates_.keySet().forEach(key -> System.out.println(
                key.getTicker() + " change to " + colorizeValue(key.getNewPrice().doubleValue(),
                        key.getOldPrice().doubleValue())));

        System.out.println("## Portfolio" + ANSI_RESET);
        printTableHeader();
        AtomicReference<Double> totalPortfolioValue = new AtomicReference<>((double) 0);
        for (List<PositionUpdateEvent> values : updates_.values()) {
            values.forEach(val -> {
                String symbol = val.getSymbol();
                int quantity = val.getPositionQuantity();
                double price = val.getNewPrice().doubleValue();
                double value = val.getNewPosition().doubleValue();
                totalPortfolioValue.getAndUpdate(v -> v + value);
                // Print the colors separately from the formatted strings
                printData(symbol, price, quantity, value);
            });
        }
        System.out.printf("%-102s ", ANSI_YELLOW_BACK + "#Total portfolio" + ANSI_RESET);
        System.out.printf("%-20s ", colorizePortfolio(totalPortfolioValue.get()) + "\n\n");
        System.out.println();
    }


    private void printData(String symbol, double price, int quantity, double value) {
        // Print the table header with colors
        System.out.print(ANSI_CYAN);
        System.out.printf("|%-30s", symbol);
        System.out.printf("|%-30s", formatPrice(price));
        System.out.printf("|%-30s", quantity);
        System.out.printf("|%-30s", colorizeValue(value));
        System.out.println();
        System.out.print(ANSI_RESET);
    }

    private void printTableHeader() {
        // Print the table header with colors
        System.out.print(ANSI_ORANGE);
        for (String header : TABLE_HEADERS) {
            System.out.printf("|%-30s", header);
        }
        System.out.println();
        System.out.print(ANSI_RESET);
    }

    private String colorizePortfolio(double price) {
        String formattedPrice = decimalFormat.format(price);
        if (price < 0) {
            return ANSI_YELLOW_BACK + ANSI_RED + formattedPrice + ANSI_RESET;
        } else {
            return ANSI_YELLOW_BACK + ANSI_GREEN + formattedPrice + ANSI_RESET;
        }
    }

    private String colorizeValue(double price) {
        String formattedPrice = String.format("%.2f", price);
        if (price < 0) {
            return ANSI_RED + formattedPrice + ANSI_RESET;
        } else {
            return ANSI_GREEN + formattedPrice + ANSI_RESET;
        }
    }

    private String colorizeValue(double newPrice_, double oldPrice_) {
        String formattedPrice = String.format("%.2f", newPrice_);
        if (oldPrice_ > newPrice_) {
            return ANSI_RED + formattedPrice + ANSI_RESET;
        } else {
            return ANSI_GREEN + formattedPrice + ANSI_RESET;
        }
    }

    private String formatPrice(double price_) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(price_);
    }

}
