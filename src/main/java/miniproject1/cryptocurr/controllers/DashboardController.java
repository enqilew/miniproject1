package miniproject1.cryptocurr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import miniproject1.cryptocurr.models.Article;
import miniproject1.cryptocurr.models.LatestRatesDTO;
import miniproject1.cryptocurr.models.RateDTO;
import miniproject1.cryptocurr.models.User;
import miniproject1.cryptocurr.services.CurrencyService;
import miniproject1.cryptocurr.services.UserPreferenceService;
import miniproject1.cryptocurr.utilities.CurrencyFlagLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Value("${newsapi.api.base.url}")
    private String newsApiBaseUrl;

    @Value("${newsapi.api.key}")
    private String apiKey;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyFlagLoader currencyFlagLoader;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "baseCurrency", defaultValue = "USD") String baseCurrency, 
                            Model model, Principal principal) {
        model.addAttribute("base", baseCurrency);
        model.addAttribute("date", java.time.LocalDate.now().toString());

        // Fetch news data
        String url = newsApiBaseUrl + "?q=cryptocurrency+OR+forex+OR+finance&language=en&pageSize=5&apiKey=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            if (json.has("articles")) {
                ObjectMapper mapper = new ObjectMapper();
                List<Article> articles = mapper.readValue(
                        json.getJSONArray("articles").toString(),
                        new TypeReference<List<Article>>() {}
                );

                articles = articles.stream()
                        .filter(article -> article.getTitle() != null && !article.getTitle().equalsIgnoreCase("[Removed]"))
                        .filter(article -> article.getUrl() != null && article.getUrlToImage() != null)
                        .collect(Collectors.toList());

                model.addAttribute("news", articles);
            } else {
                model.addAttribute("news", new ArrayList<>());
                model.addAttribute("error", "No articles found for cryptocurrency or forex.");
            }
        } catch (HttpClientErrorException ex) {
            model.addAttribute("error", "Failed to fetch news: " + ex.getStatusCode());
        } catch (Exception ex) {
            model.addAttribute("error", "An unexpected error occurred while fetching news.");
        }

        // Fetch user favorites and rates with the updated base currency
        try {
            String username = (principal != null) ? principal.getName() : null;

            if (username != null) {
                User user = userPreferenceService.getUser(username);
                if (user != null) {
                    LatestRatesDTO rates = currencyService.getLatestRates(baseCurrency);

                    List<RateDTO> favoriteFiatRates = rates.getFiatRates().entrySet().stream()
                            .filter(entry -> user.getFiatFavorites().contains(entry.getKey()))
                            .sorted(Map.Entry.comparingByKey())
                            .map(entry -> new RateDTO(entry.getKey(), entry.getValue(), currencyFlagLoader.getFlagOrIcon(entry.getKey(), false)))
                            .collect(Collectors.toList());

                    List<RateDTO> favoriteCryptoRates = rates.getCryptoRates().entrySet().stream()
                            .filter(entry -> user.getCryptoFavorites().contains(entry.getKey()))
                            .sorted(Map.Entry.comparingByKey())
                            .map(entry -> new RateDTO(entry.getKey(), entry.getValue(), currencyFlagLoader.getFlagOrIcon(entry.getKey(), true)))
                            .collect(Collectors.toList());

                    model.addAttribute("fiatFavorites", favoriteFiatRates);
                    model.addAttribute("cryptoFavorites", favoriteCryptoRates);
                } else {
                    model.addAttribute("fiatFavorites", Collections.emptyList());
                    model.addAttribute("cryptoFavorites", Collections.emptyList());
                }
            } else {
                model.addAttribute("fiatFavorites", Collections.emptyList());
                model.addAttribute("cryptoFavorites", Collections.emptyList());
            }
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to fetch favorites or rates: " + ex.getMessage());
        }

        return "dashboard";
    }

    @GetMapping("/news")
    public String getNews(Model model) {
    
        String url = newsApiBaseUrl + "?q=cryptocurrency+OR+forex+OR+finance&language=en&pageSize=20&apiKey=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            if (json.has("articles")) {
                ObjectMapper mapper = new ObjectMapper();
                List<Article> articles = mapper.readValue(
                        json.getJSONArray("articles").toString(),
                        new TypeReference<List<Article>>() {}
                );

                articles = articles.stream()
                        .filter(article -> article.getTitle() != null && !article.getTitle().equalsIgnoreCase("[Removed]"))
                        .filter(article -> article.getUrl() != null && article.getUrlToImage() != null)
                        .collect(Collectors.toList());

                model.addAttribute("news", articles);
            } else {
                model.addAttribute("news", new ArrayList<>());
                model.addAttribute("error", "No articles found for cryptocurrency or forex.");
            }
        } catch (HttpClientErrorException ex) {
            model.addAttribute("error", "Failed to fetch news: " + ex.getStatusCode());
        } catch (Exception ex) {
            model.addAttribute("error", "An unexpected error occurred while fetching news.");
        }

        return "news"; // Ensure this maps to news.html
    }

}

