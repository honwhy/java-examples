package com.honwhy.examples.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
public class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ClassPathResource resource = new ClassPathResource("sqls/create_table_score.sql");
            String sql = new String(Files.readAllBytes(resource.getFile().toPath()));
            jdbcTemplate.execute(sql);
            resource = new ClassPathResource("sqls/insert_data_score.sql");
            sql = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
            jdbcTemplate.execute(sql);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
