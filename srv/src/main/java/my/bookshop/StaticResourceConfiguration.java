package my.bookshop;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * A resource configuration for static serving of the UI files by the spring boot app.
 * This is convenient for local testing, as the UI is just served together with the
 * backend by just starting the spring boot app.
 */
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String appPath = new File("../app").toURI().toString();
		registry.addResourceHandler("/**").addResourceLocations(appPath);
	}
}