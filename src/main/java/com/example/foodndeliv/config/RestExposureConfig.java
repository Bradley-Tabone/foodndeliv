package com.example.foodndeliv.config;

import com.example.foodndeliv.entity.OrderDelivery;
import com.example.foodndeliv.entity.Rider;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestExposureConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        /* --- Hypermedia base + defaults (adjust if set in properties) --- */
        config.setBasePath("/api");
        config.setDefaultPageSize(20);
        config.setMaxPageSize(100);

        /* --- Expose IDs for convenience in HAL --- */
        config.exposeIdsFor(Rider.class, OrderDelivery.class);

        /* --- R I D E R  : preserve history, force command endpoints for status changes --- */
        config.getExposureConfiguration()
            .forDomainType(Rider.class)
                // Block destructive ops everywhere
                .withCollectionExposure((md, http) -> http.disable(HttpMethod.DELETE))
                .withItemExposure((md, http) -> http.disable(HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.PATCH));

        /* --- O R D E R  D E L I V E R Y  : preserve history, no direct state/association writes --- */
        config.getExposureConfiguration()
            .forDomainType(OrderDelivery.class)
                // Block collection-level delete
                .withCollectionExposure((md, http) -> http.disable(HttpMethod.DELETE))
                // Block item PUT/PATCH/DELETE so clients must use /deliveries/{id}/assign (command)
                .withItemExposure((md, http) -> http.disable(HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.PATCH))
                // Also block writing the rider association via the repository
                .withAssociationExposure((md, http) -> http.disable(HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.POST, HttpMethod.DELETE));

        /* --- CORS (optional; narrow this for your frontend domain) --- */
        cors.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:5173")
            .allowedMethods("GET", "POST", "HEAD", "OPTIONS")
            .allowCredentials(true);
    }
}
