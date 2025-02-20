package com.recruitment.google.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import java.io.InputStream;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCalendarConfig {

    @Value("${google.service.account.keypath}")
    private String keyPath;

    @Value("${google.calendar.application.name}")
    private String applicationName;

    @Bean
    public com.google.api.services.calendar.Calendar calendarService() throws Exception {
        InputStream credentialsStream = getClass().getResourceAsStream(keyPath);
        GoogleCredential credential = GoogleCredential.fromStream(credentialsStream)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/calendar"));
        return new com.google.api.services.calendar.Calendar.Builder(
                credential.getTransport(),
                credential.getJsonFactory(),
                credential
        ).setApplicationName(applicationName).build();
    }
}