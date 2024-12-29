package miniproject1.cryptocurr.models;

public class RateDTO {
    private String currency;
    private Double rate;
    private String iconPath;

    public RateDTO(String currency, Double rate, String iconPath) {
        this.currency = currency;
        this.rate = rate;
        this.iconPath = iconPath;
    }

    // Getters and Setters
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}

