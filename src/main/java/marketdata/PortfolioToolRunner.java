package marketdata;

import marketdata.simulator.MarketDataSimulator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "marketdata")
public class PortfolioToolRunner {

    public static void main(String[] args) {
        // Initialize Spring context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityDataConfig.class);

        // Get MarketDataSimulator bean from the context
        MarketDataSimulator simulator = context.getBean(MarketDataSimulator.class);

        // Start simulation
        simulator.startSimulation(getUserChoice());

        // Close context when simulation ends
        context.close();
    }

    private static boolean getUserChoice() {
        // Create a scanner to read user input
        Scanner scanner = new Scanner(System.in);

        // Choose between Brownian and random simulation, by default use Brownian
        System.out.println("Choose simulation type:");
        System.out.println("1. Brownian Motion");
        System.out.println("2. Random Pricing");
        System.out.print("Enter your choice (1/2): ");

        // Read the user's choice
        int choice = scanner.nextInt();
        scanner.close();

        return choice == 2;
    }
}

