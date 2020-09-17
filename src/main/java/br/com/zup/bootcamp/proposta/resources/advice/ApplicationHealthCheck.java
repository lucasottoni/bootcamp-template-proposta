package br.com.zup.bootcamp.proposta.resources.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationHealthCheck implements HealthIndicator {

    private final Environment environment;

    public ApplicationHealthCheck(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        details.put("APP_VERSION", environment.getProperty("application.version"));
        details.put("DESCRIPTION", "Checking application");

        return Health.up().withDetails(details).build();
    }
}
