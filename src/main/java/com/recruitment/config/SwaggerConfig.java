package com.recruitment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@PropertySource(value = "classpath:/open-api.properties")
public class SwaggerConfig {

    private final Environment environment;

    private List<Tag> getTagList() {
        return List.of(new Tag().name("candidate-controller")
                        .description(environment.getProperty("swagger.candidate-controller-v1.description")),
                new Tag().name("recruiter-controller")
                        .description(environment.getProperty("swagger.recruiter-controller-v1.description")),
                new Tag().name("interview-slot-controller")
                        .description(environment.getProperty("swagger.interview-slot-controller-v1.description")),
                new Tag().name("vacation-controller")
                        .description(environment.getProperty("swagger.vacation-controller-v1.description")),
                new Tag().name("reservation-controller")
                        .description(environment.getProperty("swagger.reservation-controller-v1.description")),
                new Tag().name("user-controller")
                        .description(environment.getProperty("swagger.user-controller-v1.description")));
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(apiInfo()).tags(getTagList());
    }

    private Info apiInfo() {
        return new Info().title("Recruitment Scheduler")
                .description("API for Recruitment Scheduler Application")
                .version("1.0");
    }
}