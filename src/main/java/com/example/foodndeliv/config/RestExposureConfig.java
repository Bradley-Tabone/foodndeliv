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
        config.setBasePath("/api");
        config.setDefaultPageSize(20);
        config.setMaxPageSize(100);

        config.exposeIdsFor(Rider.class, OrderDelivery.class);

        config.getExposureConfiguration()
            .forDomainType(Rider.class)
                .withCollectionExposure((md, http) -> http.disable(HttpMethod.DELETE))
                .withItemExposure((md, http) -> http.disable(HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.PATCH));

        config.getExposureConfiguration()
            .forDomainType(OrderDelivery.class)
                .withCollectionExposure((md, http) -> http.disable(HttpMethod.DELETE))
                .withItemExposure((md, http) -> http.disable(HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.PATCH))
                .withAssociationExposure((md, http) -> http.disable(HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.POST, HttpMethod.DELETE));
    }
}
