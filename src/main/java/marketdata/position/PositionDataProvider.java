package marketdata.position;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import marketdata.security.SecurityInfo;
import marketdata.security.SecurityInfoProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PositionDataProvider {
    private final String _positionFilePath;
    private final SecurityInfoProvider _securityInfoProvider;
    private final Map<String, PositionData> _positionCache;

    public PositionDataProvider(String positionsFilePath_, SecurityInfoProvider securityInfoProvider_) {
        _positionFilePath = positionsFilePath_;
        _securityInfoProvider = securityInfoProvider_;
        _positionCache = new HashMap();
    }

    public void processData() throws IOException, CsvValidationException {
        //Read and load position data from csv
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(_positionFilePath);
        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
        String[] line;
        // Skip header line if present
        if (reader.readNext() == null) {
            System.out.println("Empty file or invalid format.");
            return;
        }
        while ((line = reader.readNext()) != null) {
            String identifier = line[0].trim(); // Trim to remove extra spaces
            int positionQuantity = Integer.parseInt(line[1].trim()); // Trim to remove extra spaces

            // Fetch the corresponding Security object from the security cache
            SecurityInfo security = _securityInfoProvider.getSecurity(identifier);
            if (security != null) {
                // Create a new PositionData object with the fetched Security object
                _positionCache.put(identifier,
                        new PositionData(security.getTicker(), identifier, positionQuantity, security.getType()));
            } else {
                // Handle the case where the security symbol is not found in the cache
                System.err.println("Security not found in cache for symbol: " + identifier);
            }
        }
    }

    public Map<String, PositionData> getPositionCache() {
        return _positionCache;
    }
}
