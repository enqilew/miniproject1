package miniproject1.cryptocurr.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class CurrencyFlagLoader {

    private static final String JSON_FILE = "/currency-config.json";

    private Map<String, String> fiatFlagMappings;
    private Map<String, String> cryptoIconMappings;

    @PostConstruct
    public void init() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream(JSON_FILE);
            if (inputStream == null) {
                throw new IOException("JSON file not found: " + JSON_FILE);
            }

            Map<String, Object> mappings = objectMapper.readValue(inputStream, new TypeReference<>() {});
            fiatFlagMappings = (Map<String, String>) mappings.get("flags");
            cryptoIconMappings = (Map<String, String>) mappings.get("icons");

            System.out.println("Fiat Flags Loaded: " + fiatFlagMappings);
            System.out.println("Crypto Icons Loaded: " + cryptoIconMappings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load currency mappings", e);
        }
    }

    public Map<String, String> loadFiatFlags(Set<String> fiatCurrencies) {
        Map<String, String> fiatFlags = new HashMap<>();
        for (String currency : fiatCurrencies) {
            String normalizedCurrency = currency.toUpperCase();
            String flagPath = fiatFlagMappings.getOrDefault(normalizedCurrency, "default") + ".png";
            fiatFlags.put(normalizedCurrency, "/flags/" + flagPath);
        }
        return fiatFlags;
    }

    public Map<String, String> loadCryptoIcons(Set<String> cryptoCurrencies) {
        Map<String, String> cryptoIcons = new HashMap<>();
        for (String currency : cryptoCurrencies) {
            String normalizedCurrency = currency.toUpperCase();
            String iconPath = cryptoIconMappings.getOrDefault(normalizedCurrency, "default") + ".png";
            cryptoIcons.put(normalizedCurrency, "/icons/crypto/" + iconPath);
        }
        return cryptoIcons;
    }

    public String getFlagOrIcon(String currency, boolean isCrypto) {
        String normalizedCurrency = currency.toUpperCase();

        if (isCrypto) {
            return "/icons/crypto/" + cryptoIconMappings.getOrDefault(normalizedCurrency, "default") + ".png";
        } else {
            return "/flags/" + fiatFlagMappings.getOrDefault(normalizedCurrency, "default") + ".png";
        }
    }
}

