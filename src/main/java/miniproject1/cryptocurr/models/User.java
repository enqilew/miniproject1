package miniproject1.cryptocurr.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.*;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Email(message = "Must be a valid email.")
    @NotBlank(message = "Email is required.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
    private String password;

    private List<String> fiatFavorites = new ArrayList<>();
    private List<String> cryptoFavorites = new ArrayList<>();

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFiatFavorites() {
        return fiatFavorites;
    }

    public void setFiatFavorites(List<String> fiatFavorites) {
        this.fiatFavorites = fiatFavorites;
    }

    public List<String> getCryptoFavorites() {
        return cryptoFavorites;
    }

    public void setCryptoFavorites(List<String> cryptoFavorites) {
        this.cryptoFavorites = cryptoFavorites;
    }
}