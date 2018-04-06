package top.yeonon.lmmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class LmMallApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmMallApplication.class, args);
	}
}
