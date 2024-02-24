package uk.co.imperatives.exercise.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${exercise.tables}")
    private int tables;

    @Value("${exercise.capacity}")
    private int capacity;
}
