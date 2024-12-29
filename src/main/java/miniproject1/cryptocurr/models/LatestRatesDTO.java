package miniproject1.cryptocurr.models;

import java.util.Map;

public class LatestRatesDTO {
    private String base;
    private String date;
    private Map<String, Double> fiatRates;
    private Map<String, Double> cryptoRates;

    // Getters and Setters
    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Double> getFiatRates() {
        return fiatRates;
    }

    public void setFiatRates(Map<String, Double> fiatRates) {
        this.fiatRates = fiatRates;
    }

    public Map<String, Double> getCryptoRates() {
        return cryptoRates;
    }

    public void setCryptoRates(Map<String, Double> cryptoRates) {
        this.cryptoRates = cryptoRates;
    }
}
