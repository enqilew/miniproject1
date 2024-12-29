package miniproject1.cryptocurr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import miniproject1.cryptocurr.models.HistoricalRates;
import miniproject1.cryptocurr.utilities.CurrencyFlagLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

@Controller
public class HistoricalRatesController {

    @Value("${currencyfreaks.api.key}")
    private String apiKey;

    @Value("${currencyfreaks.api.base.url}")
    private String baseUrl;

    @Autowired
    private CurrencyFlagLoader currencyFlagLoader;

     private static final Set<String> FIAT_CURRENCIES = new HashSet<>(Arrays.asList(
        "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN", "BAM", "BBD",
        "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTN", "BWP", "BYN",
        "BYR", "BZD", "CAD", "CDF", "CHF", "CLF", "CLP", "CNH", "CNY", "COP", "CRC", "CUC",
        "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR", "FJD",
        "FKP", "GBP", "GEL", "GGP", "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL",
        "HRK", "HTG", "HUF", "IDR", "ILS", "IMP", "INR", "IQD", "IRR", "ISK", "JEP", "JMD",
        "JOD", "JPY", "KES", "KGS", "KHR", "KMF", "KPW", "KRW", "KWD", "KYD", "KZT", "LAK",
        "LBP", "LKR", "LRD", "LSL", "LTL", "LVL", "LYD", "MAD", "MDL", "MGA", "MKD", "MMK",
        "MNT", "MOP", "MRO", "MRU", "MUR", "MVR", "MWK", "MXN", "MYR", "MZN", "NAD", "NGN",
        "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG",
        "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG", "SEK", "SGD", "SHP",
        "SLL", "SOS", "SRD", "SSP", "STD", "STN", "SVC", "SYP", "SZL", "THB", "TJS", "TMT",
        "TND", "TOP", "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VEF",
        "VES", "VND", "VUV", "WST", "XAF", "XCD", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL"
    ));

    private static final Set<String> CRYPTO_CURRENCIES = new HashSet<>(Arrays.asList(
        "$PAC", "0XBTC", "1INCH", "2GIVE", "AAVE", "ABT", "ACT", "ACTN", "ADA", "ADD", "ADX", "AE", "AEON",
        "AEUR", "AGI", "AGRS", "AION", "ALGO", "AMB", "AMP", "AMPL", "ANKR", "ANT", "APE", "APEX", "APPC",
        "ARDR", "ARG", "ARK", "ARN", "ARNX", "ARY", "AST", "ATLAS", "ATM", "ATOM", "AUDR", "AURY", "AUTO",
        "AVAX", "AYWA", "BAB", "BAL", "BAND", "BAT", "BAY", "B CBC", "BCC", "BCD", "BCH", "BCIO", "BCN",
        "BCO", "BCPT", "BDL", "BEAM", "BELA", "BIX", "BLCN", "BLK", "BLOCK", "BLZ", "BNB", "BNT", "BNTY",
        "BOOTY", "BOS", "BPT", "BQ", "BRD", "BSV", "BTC", "BTCD", "BTCH", "BTCP", "BTCZ", "BTDX", "BTG",
        "BTM", "BTS", "BTT", "BTX", "BURST", "BZE", "CALL", "CC", "CDN", "CDT", "CENZ", "CHAIN", "CHAT",
        "CHIPS", "CHSB", "CHZ", "CIX", "CLAM", "CLOAK", "CMM", "CMT", "CND", "CNX", "COB", "COLX", "COMP",
        "COQUI", "CRED", "CRPT", "CRV", "CRW", "CS", "CTR", "CTXC", "CVC", "D", "DAI", "DASH", "DAT",
        "DATA", "DBC", "DCN", "DCR", "DEEZ", "DENT", "DEW", "DGB", "DGD", "DLT", "DNT", "DOCK", "DOGE",
        "DOT", "DRGN", "DROP", "DTA", "DTH", "DTR", "EBST", "ECA", "EDG", "EDO", "EDOGE", "ELA", "ELEC",
        "ELF", "ELIX", "ELLA", "EMB", "EMC", "EMC2", "ENG", "ENJ", "ENTRP", "EON", "EOP", "EOS", "EQLI",
        "EQUA", "ETC", "ETH", "ETHOS", "ETN", "ETP", "EVX", "EXMO", "EXP", "FAIR", "FCT", "FIDA", "FIL",
        "FJC", "FLDC", "FLO", "FLUX", "FSN", "FTC", "FUEL", "FUN", "GAME", "GAS", "GBX", "GBYTE", "GENERIC",
        "GIN", "GLXT", "GMR", "GMT", "GNO", "GNT", "GOLD", "GRC", "GRIN", "GRS", "GRT", "GSC", "GTO",
        "GUP", "GUSD", "GVT", "GXS", "GZR", "HIGHT", "HNS", "HODL", "HOT", "HPB", "HSR", "HT", "HTML",
        "HUC", "HUSD", "HUSH", "ICN", "ICP", "ICX", "IGNIS", "ILK", "INK", "INS", "ION", "IOP", "IOST",
        "IOTX", "IQ", "ITC", "JNT", "KCS", "KIN", "KLOWN", "KMD", "KNC", "KRB", "KSM", "LBC", "LEND",
        "LEO", "LINK", "LKK", "LOOM", "LPT", "LRC", "LSK", "LTC", "LUN", "MAID", "MANA", "MATIC", "MAX",
        "MCAP", "MCO", "MDA", "MDS", "MED", "MEETONE", "MFT", "MIOTA", "MITH", "MKR", "MLN", "MNX", "MNZ",
        "MOAC", "MOD", "MONA", "MSR", "MTH", "MUSIC", "MZC", "NANO", "NAS", "NAV", "NCASH", "NDZ",
        "NEBL", "NEO", "NEOS", "NEU", "NEXO", "NGC", "NKN", "NLC2", "NLG", "NMC", "NMR", "NPXS", "NTBC",
        "NULS", "NXS", "NXT", "OAX", "OK", "OMG", "OMNI", "ONE", "ONG", "ONT", "OOT", "OST", "OX", "OXT",
        "OXY", "PART", "PASC", "PASL", "PAX", "PAXG", "PAY", "PAYX", "PINK", "PIRL", "PIVX", "PLR",
        "POA", "POE", "POLIS", "POLY", "POT", "POWR", "PPC", "PPP", "PPT", "PRE", "PRL", "PUNGO",
        "PURA", "QASH", "QIWI", "QLC", "QNT", "QRL", "QSP", "QTUM", "R", "RADS", "RAP", "RAY",
        "RCN", "RDD", "RDN", "REN", "REP", "REPV2", "REQ", "RHOC", "RIC", "RISE", "RLC", "RPX",
        "RVN", "RYO", "SAFE", "SAFECOIN", "SAI", "SALT", "SAN", "SAND", "SBERBANK", "SC", "SER",
        "SHIFT", "SIB", "SIN", "SKL", "SKY", "SLR", "SLS", "SMART", "SNGLS", "SNM", "SNT", "SNX",
        "SOC", "SOL", "SPACEHBIT", "SPANK", "SPHTX", "SRN", "STAK", "START", "STEEM", "STORJ",
        "STORM", "STOX", "STQ", "STRAT", "STX", "SUB", "SUMO", "SUSHI", "SYS", "TAAS", "TAU",
        "TBX", "TEL", "TEN", "TERN", "TGCH", "THETA", "TIX", "TKN", "TKS", "TNB", "TNC", "TNT",
        "TOMO", "TPAY", "TRIG", "TRTL", "TRX", "TUSD", "TZC", "UBQ", "UMA", "UNI", "UNITY",
        "USDC", "USDT", "UTK", "VERI", "VET", "VIA", "VIB", "VIBE", "VIVO", "VRC", "VRSC",
        "VTC", "VTHO", "WABI", "WAN", "WAVES", "WAX", "WBTC", "WGR", "WICC", "WINGS",
        "WPR", "WTC", "X", "XAS", "XBC", "XBP", "XBY", "XCP", "XDN", "XEM", "XIN",
        "XLM", "XMCC", "XMG", "XMO", "XMR", "XMY", "XP", "XPA", "XPM", "XPR", "XRP",
        "XSG", "XTZ", "XUC", "XVC", "XVG", "XZC", "YFI", "YOYOW", "ZCL", "ZEC",
        "ZEL", "ZEN", "ZEST", "ZIL", "ZILLA", "ZRX"
    ));

    // Fetch historical rates for a specific date
    @GetMapping("/historical-rates")
    public String getHistoricalRates(
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "USD") String baseCurrency,
            @RequestParam(required = false, defaultValue = "") String searchQuery,
            Model model) {

        String url = String.format("%s/v2.0/rates/historical?apikey=%s&date=%s", baseUrl, apiKey, date);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<HistoricalRates> response = restTemplate.getForEntity(url, HistoricalRates.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                HistoricalRates historicalData = response.getBody();

                // Convert rates from Map<String, String> to Map<String, Double>
                Map<String, Double> originalRates = historicalData.getRates().entrySet().stream()
                        .filter(entry -> FIAT_CURRENCIES.contains(entry.getKey()) || CRYPTO_CURRENCIES.contains(entry.getKey())) // Filter currencies
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> Double.parseDouble(entry.getValue()) // Convert String to Double
                        ));

                // Perform base currency conversion
                Double newBaseRate = originalRates.get(baseCurrency);

                if (newBaseRate == null) {
                    model.addAttribute("error", "Base currency not found in the fetched data.");
                } else {
                    // Apply search query filter and split into fiat and crypto
                    String search = searchQuery.toUpperCase();

                    Map<String, Double> fiatRates = originalRates.entrySet().stream()
                            .filter(entry -> FIAT_CURRENCIES.contains(entry.getKey()) &&
                                    (search.isEmpty() || entry.getKey().contains(search)))
                            .sorted(Map.Entry.comparingByKey()) // Sort alphabetically
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue() / newBaseRate,
                                    (e1, e2) -> e1,
                                    LinkedHashMap::new
                            ));

                    Map<String, Double> cryptoRates = originalRates.entrySet().stream()
                            .filter(entry -> CRYPTO_CURRENCIES.contains(entry.getKey()) &&
                                    (search.isEmpty() || entry.getKey().contains(search)))
                            .sorted(Map.Entry.comparingByKey()) // Sort alphabetically
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue() / newBaseRate,
                                    (e1, e2) -> e1,
                                    LinkedHashMap::new
                            ));

                    // Fetch icons/flags for fiat and crypto currencies
                    Map<String, String> fiatFlags = currencyFlagLoader.loadFiatFlags(fiatRates.keySet());
                    Map<String, String> cryptoIcons = currencyFlagLoader.loadCryptoIcons(cryptoRates.keySet());

                    // Create a combined list with rates and icons for fiat currencies
                    List<Map<String, Object>> fiatDisplayRates = fiatRates.entrySet().stream()
                            .map(entry -> {
                                Map<String, Object> rateWithFlag = new HashMap<>();
                                rateWithFlag.put("currency", entry.getKey());
                                rateWithFlag.put("rate", entry.getValue());
                                rateWithFlag.put("iconPath", fiatFlags.getOrDefault(entry.getKey(), "/flags/default.png"));
                                return rateWithFlag;
                            })
                            .collect(Collectors.toList());

                    // Create a combined list with rates and icons for crypto currencies
                    List<Map<String, Object>> cryptoDisplayRates = cryptoRates.entrySet().stream()
                            .map(entry -> {
                                Map<String, Object> rateWithIcon = new HashMap<>();
                                rateWithIcon.put("currency", entry.getKey());
                                rateWithIcon.put("rate", entry.getValue());
                                rateWithIcon.put("iconPath", cryptoIcons.getOrDefault(entry.getKey(), "/icons/crypto/default.png"));
                                return rateWithIcon;
                            })
                            .collect(Collectors.toList());

                    model.addAttribute("fiatRates", fiatDisplayRates);
                    model.addAttribute("cryptoRates", cryptoDisplayRates);
                    model.addAttribute("historicalData", historicalData);
                    model.addAttribute("baseCurrency", baseCurrency);
                    model.addAttribute("searchQuery", searchQuery); // Preserve search query in the model
                }
            } else {
                model.addAttribute("error", "Failed to retrieve data from the API.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            e.printStackTrace(); // Optional: For debugging purposes
        }

        model.addAttribute("selectedDate", date); // Add selected date to model
        return "historical-rates";
    }


    // Fetch historical rates over a time period
    @GetMapping("/timeseries-rates")
    public String getTimeSeriesRates(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String baseCurrency,
            @RequestParam String symbols,
            Model model) {
        String url = baseUrl + "/v2.0/timeseries?apikey=" + apiKey + "&startDate=" + startDate + "&endDate=" + endDate +
                     "&base=" + baseCurrency + "&symbols=" + symbols;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            model.addAttribute("timeSeriesData", response);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("baseCurrency", baseCurrency);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch historical data for the period: " + startDate + " to " + endDate);
        }

        return "timeseries-rates";
    }
}
