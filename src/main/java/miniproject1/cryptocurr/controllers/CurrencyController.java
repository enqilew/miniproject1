package miniproject1.cryptocurr.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import miniproject1.cryptocurr.models.LatestRatesDTO;
import miniproject1.cryptocurr.models.RateDTO;
import miniproject1.cryptocurr.models.User;
import miniproject1.cryptocurr.services.CurrencyDetailsService;
import miniproject1.cryptocurr.services.CurrencyService;
import miniproject1.cryptocurr.services.UserPreferenceService;
import miniproject1.cryptocurr.utilities.CurrencyFlagLoader;

@Controller
@RequestMapping("/")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyFlagLoader currencyFlagLoader;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private CurrencyDetailsService currencyDetailsService;

    @GetMapping("/api/latest-rates")
    @ResponseBody
    public LatestRatesDTO getLatestRates(@RequestParam(required = false, defaultValue = "USD") String baseCurrency) {
        return currencyService.getLatestRates(baseCurrency);
    }

    /**
     * Endpoint to add a currency to favorites.
     */
    @PostMapping("/favorites/add")
    public String addFavorite(@RequestParam("currency") String currency, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "User not logged in.");
            return "redirect:/latest-rates";
        }
        try {
            User user = userPreferenceService.getUser(username);
            if (user == null) {
                user = new User();
                user.setUsername(username);
                // Initialize other fields if necessary
            }

            if (currencyService.isFiatCurrency(currency)) {
                if (!user.getFiatFavorites().contains(currency)) {
                    user.getFiatFavorites().add(currency);
                }
            } else if (currencyService.isCryptoCurrency(currency)) {
                if (!user.getCryptoFavorites().contains(currency)) {
                    user.getCryptoFavorites().add(currency);
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid currency code.");
                return "redirect:/latest-rates";
            }

            userPreferenceService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Added " + currency + " to favorites.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add favorite: " + e.getMessage());
        }
        return "redirect:/latest-rates";
    }

    /**
     * Endpoint to remove a currency from favorites.
     */
    @PostMapping("/favorites/remove")
    public String removeFavorite(@RequestParam("currency") String currency, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "User not logged in.");
            return "redirect:/latest-rates";
        }
        try {
            User user = userPreferenceService.getUser(username);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User data not found.");
                return "redirect:/latest-rates";
            }

            boolean removed = false;
            if (currencyService.isFiatCurrency(currency)) {
                removed = user.getFiatFavorites().remove(currency);
            } else if (currencyService.isCryptoCurrency(currency)) {
                removed = user.getCryptoFavorites().remove(currency);
            }

            if (removed) {
                userPreferenceService.saveUser(user);
                redirectAttributes.addFlashAttribute("success", "Removed " + currency + " from favorites.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Currency not found in favorites.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove favorite: " + e.getMessage());
        }
        return "redirect:/latest-rates";
    }

    /**
     * Updated method to display latest rates along with favorites.
     */
    @GetMapping("/latest-rates")
    public String showLatestRates(@RequestParam(defaultValue = "USD") String baseCurrency, Model model, Principal principal) {
        try {
            // Validate the base currency
            if (!currencyService.isFiatCurrency(baseCurrency) && !currencyService.isCryptoCurrency(baseCurrency)) {
                model.addAttribute("error", "Invalid base currency code.");
                return "latest-rates";
            }

            // Fetch rates with the selected base currency
            LatestRatesDTO rates = currencyService.getLatestRates(baseCurrency);

            // Sort fiat rates alphabetically
            List<RateDTO> fiatRatesList = rates.getFiatRates().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // Sort by currency code
                    .map(entry -> new RateDTO(entry.getKey(), entry.getValue(), currencyFlagLoader.getFlagOrIcon(entry.getKey(), false)))
                    .collect(Collectors.toList());

            // Sort crypto rates alphabetically
            List<RateDTO> cryptoRatesList = rates.getCryptoRates().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // Sort by currency code
                    .map(entry -> new RateDTO(entry.getKey(), entry.getValue(), currencyFlagLoader.getFlagOrIcon(entry.getKey(), true)))
                    .collect(Collectors.toList());

            // Fetch user data from Redis
            String username = (principal != null) ? principal.getName() : null;

            final List<String> fiatFavorites;
            final List<String> cryptoFavorites;

            if (username != null) {
                User user = userPreferenceService.getUser(username);
                if (user != null) {
                    fiatFavorites = user.getFiatFavorites();
                    cryptoFavorites = user.getCryptoFavorites();
                } else {
                    fiatFavorites = Collections.emptyList();
                    cryptoFavorites = Collections.emptyList();
                }
            } else {
                fiatFavorites = Collections.emptyList();
                cryptoFavorites = Collections.emptyList();
            }

            List<RateDTO> favoriteFiatRates = rates.getFiatRates().entrySet().stream()
                    .filter(entry -> fiatFavorites.contains(entry.getKey()))
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> new RateDTO(entry.getKey(), entry.getValue(), currencyFlagLoader.getFlagOrIcon(entry.getKey(), false)))
                    .collect(Collectors.toList());

            List<RateDTO> favoriteCryptoRates = rates.getCryptoRates().entrySet().stream()
                    .filter(entry -> cryptoFavorites.contains(entry.getKey()))
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> new RateDTO(entry.getKey(), entry.getValue(), currencyFlagLoader.getFlagOrIcon(entry.getKey(), true)))
                    .collect(Collectors.toList());

            model.addAttribute("fiatCurrencies", fiatRatesList);
            model.addAttribute("cryptocurrencies", cryptoRatesList);
            model.addAttribute("base", rates.getBase());
            model.addAttribute("date", rates.getDate());
            model.addAttribute("fiatFavorites", favoriteFiatRates);
            model.addAttribute("cryptoFavorites", favoriteCryptoRates);

            return "latest-rates";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve rates: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/searchCurrency")
    public String searchCurrency(@RequestParam String currencyCode,
                                @RequestParam(defaultValue = "USD") String baseCurrency,
                                Model model,
                                Principal principal) {
        try {
            // Fetch rates with the specified base currency
            LatestRatesDTO rates = currencyService.getLatestRates(baseCurrency);
            String currencyCodeUpper = currencyCode.toUpperCase();

            // Check if the currency exists in fiat or crypto rates
            Double fiatRate = rates.getFiatRates().get(currencyCodeUpper);
            Double cryptoRate = rates.getCryptoRates().get(currencyCodeUpper);

            // Prepare search result
            List<RateDTO> searchResult = new ArrayList<>();
            if (fiatRate != null) {
                searchResult.add(new RateDTO(currencyCodeUpper, fiatRate, currencyFlagLoader.getFlagOrIcon(currencyCodeUpper, false)));
            }
            if (cryptoRate != null) {
                searchResult.add(new RateDTO(currencyCodeUpper, cryptoRate, currencyFlagLoader.getFlagOrIcon(currencyCodeUpper, true)));
            }

            // Add attributes for rendering
            model.addAttribute("searchResult", searchResult);
            model.addAttribute("currencyCode", currencyCode);
            model.addAttribute("baseCurrency", baseCurrency);

            return "search-result";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to search currency: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/currency/{currencyCode}")
    public String getCurrencyDetails(@PathVariable String currencyCode, Model model) {
        try {
            // Convert currency code to uppercase for consistency
            String upperCaseCurrencyCode = currencyCode.toUpperCase();

            // Fetch rates from your service
            Map<String, String> rates = currencyDetailsService.getCurrencyDetails("USD");

            // Check if the currency exists in the rates
            if (!rates.containsKey(upperCaseCurrencyCode)) {
                model.addAttribute("error", "Currency code not found.");
                return "error";
            }

            // Get the rate and flag/icon path
            String rate = rates.get(upperCaseCurrencyCode);
            boolean isCrypto = isCryptoCurrency(upperCaseCurrencyCode);
            String flagOrIconPath = currencyFlagLoader.getFlagOrIcon(upperCaseCurrencyCode, isCrypto);

            // Add attributes to the model
            model.addAttribute("currencyCode", upperCaseCurrencyCode);
            model.addAttribute("rate", rate);
            model.addAttribute("currencyIcon", flagOrIconPath);

            return "currency-details";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve currency details: " + e.getMessage());
            return "error";
        }
    }

    private boolean isCryptoCurrency(String currencyCode) {
        // Logic to determine if the currency is a crypto (can be replaced with actual validation)
        return currencyCode.matches("BTC|ETH|LTC|XRP|DOGE"); // Example crypto codes
    }
    
}

