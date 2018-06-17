package top.yeonon.lmmall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import top.yeonon.lmmall.interceptor.UserAuthenticationInterceptor;

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
@EnableElasticsearchRepositories
public class LmMallApplication {

	public static void main(String[] args) {
		System.setProperty("es.set.netty.runtime.available.processors", "false");
		SpringApplication.run(LmMallApplication.class, args);
	}


	@Configuration
	static class WebMvcConfigurer extends WebMvcConfigurerAdapter {

		@Autowired
		private UserAuthenticationInterceptor userAuthenticationInterceptor;

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(userAuthenticationInterceptor);
		}

	}
}
