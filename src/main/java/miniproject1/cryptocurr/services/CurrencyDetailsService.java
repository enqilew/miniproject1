package miniproject1.cryptocurr.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import miniproject1.cryptocurr.models.CurrencyDetailsDTO;

import java.util.Map;

@Service
public class CurrencyDetailsService {

    @Value("${currencyfreaks.api.key}")
    private String apiKey;

    @Value("${currencyfreaks.api.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public CurrencyDetailsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> getCurrencyDetails(String baseCurrency) {
        // Construct the API URL
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/latest")
                .queryParam("apikey", apiKey)
                .queryParam("base", baseCurrency)
                .toUriString();

        // Fetch the response from the API
        CurrencyDetailsDTO response = restTemplate.getForObject(url, CurrencyDetailsDTO.class);

        if (response == null || response.getRates() == null) {
            throw new RuntimeException("Invalid response from CurrencyFreaks API");
        }

        return response.getRates();
    }
}

