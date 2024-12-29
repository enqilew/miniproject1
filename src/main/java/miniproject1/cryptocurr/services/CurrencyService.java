package miniproject1.cryptocurr.services;

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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import miniproject1.cryptocurr.models.LatestRatesDTO;


@Service
public class CurrencyService {

    @Value("${currencyfreaks.api.key}")
    private String apiKey;

    @Value("${currencyfreaks.api.base.url}")
    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;

    // Define fiat and crypto currencies as Sets for efficient lookup
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

    public LatestRatesDTO getLatestRates(String baseCurrency) {
        String url = baseUrl + "/latest?apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("rates")) {
            throw new RuntimeException("Invalid response from API");
        }

        Map<String, Object> rates = (Map<String, Object>) response.get("rates");

        // Parse rates into Double safely
        Map<String, Double> parsedRates = parseRates(rates);

        // Adjust rates for non-USD base currency if necessary
        if (!"USD".equalsIgnoreCase(baseCurrency)) {
            parsedRates = recalculateRatesForNewBase(parsedRates, baseCurrency);
        }

        // Populate DTO
        LatestRatesDTO dto = new LatestRatesDTO();
        dto.setBase(baseCurrency);
        dto.setDate((String) response.get("date"));
        dto.setFiatRates(filterFiatRates(parsedRates));
        dto.setCryptoRates(filterCryptoRates(parsedRates));

        return dto;
    }

    /**
     * Check if the given currency is a fiat currency.
     */
    public boolean isFiatCurrency(String currency) {
        return FIAT_CURRENCIES.contains(currency.toUpperCase());
    }

    /**
     * Check if the given currency is a cryptocurrency.
     */
    public boolean isCryptoCurrency(String currency) {
        return CRYPTO_CURRENCIES.contains(currency.toUpperCase());
    }

    private Map<String, Double> recalculateRatesForNewBase(Map<String, Double> rates, String newBaseCurrency) {
        if (!rates.containsKey(newBaseCurrency.toUpperCase())) {
            throw new IllegalArgumentException("New base currency " + newBaseCurrency + " is not available in the rates data");
        }

        // Rate of the new base currency relative to USD
        double newBaseRate = rates.get(newBaseCurrency.toUpperCase());

        // Recalculate all rates relative to the new base currency
        Map<String, Double> recalculatedRates = new HashMap<>();
        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            recalculatedRates.put(entry.getKey(), entry.getValue() / newBaseRate);
        }

        return recalculatedRates;
    }

    public Map<String, Double> getRatesForFavorites(List<String> favorites, Map<String, Double> allRates) {
        return favorites.stream()
            .filter(allRates::containsKey)
            .collect(Collectors.toMap(favorite -> favorite, allRates::get));
    }

    private Map<String, Double> parseRates(Map<String, Object> rates) {
        Map<String, Double> parsedRates = new HashMap<>();
        for (Map.Entry<String, Object> entry : rates.entrySet()) {
            try {
                parsedRates.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse rate for " + entry.getKey() + ": " + entry.getValue());
            }
        }
        return parsedRates;
    }

    private Map<String, Double> filterFiatRates(Map<String, Double> rates) {
        return rates.entrySet().stream()
                .filter(entry -> isFiatCurrency(entry.getKey()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }

    private Map<String, Double> filterCryptoRates(Map<String, Double> rates) {
        return rates.entrySet().stream()
                .filter(entry -> isCryptoCurrency(entry.getKey()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }

}
