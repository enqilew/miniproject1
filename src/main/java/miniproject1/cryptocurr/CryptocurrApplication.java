package miniproject1.cryptocurr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "miniproject1.cryptocurr")
public class CryptocurrApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptocurrApplication.class, args);
	}

}
