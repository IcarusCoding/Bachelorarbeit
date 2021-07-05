package de.intelligence.bachelorarbeit.demoapplications;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSpringModule {

    @Bean
    public ITestService testService() {
        return new TestServiceImpl();
    }

}
