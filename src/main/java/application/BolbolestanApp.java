package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "controllers", "application" })
public class BolbolestanApp {

	public static void main(String... args) {
		SpringApplication.run(BolbolestanApp.class, args);
	}
}
