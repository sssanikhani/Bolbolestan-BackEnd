package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = { "controllers", "application" })
@ComponentScan(basePackages = { "controllers", "application" })
public class BolbolestanApp {

	public static void main(String... args) {
		SpringApplication.run(BolbolestanApp.class, args);
	}
}
