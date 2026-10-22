package com.example.webapp.spa;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Vite writes every bundle file under {@code assets/} with a content hash in its name, so those
 * may be cached for a year. {@code index.html} names the current hashes and stays on Boot's default
 * handling, uncached, so a deploy is picked up on the next page load.
 */
@Configuration
class StaticAssets implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic());
    }
}
