package com.company.timesheets;

import com.company.timesheets.component.slider.Slider;
import com.company.timesheets.component.slider.SliderLoader;
import com.company.timesheets.component.themetoggle.ThemeToggle;
import com.company.timesheets.component.themetoggle.ThemeToggleLoader;
import com.google.common.base.Strings;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import io.jmix.flowui.sys.registration.ComponentRegistration;
import io.jmix.flowui.sys.registration.ComponentRegistrationBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Push
@Theme(value = "timesheets")
@PWA(name = "Timesheets", shortName = "Timesheets")
@SpringBootApplication
public class TimesheetsApplication implements AppShellConfigurator {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(TimesheetsApplication.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource.hikari")
    DataSource dataSource(final DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @EventListener
    public void printApplicationUrl(final ApplicationStartedEvent event) {
        LoggerFactory.getLogger(TimesheetsApplication.class).info("Application started at "
                + "http://localhost:"
                + environment.getProperty("local.server.port")
                + Strings.nullToEmpty(environment.getProperty("server.servlet.context-path")));
    }

    @Bean
    public ComponentRegistration themeToggle() {
        return ComponentRegistrationBuilder.create(ThemeToggle.class)
                .withComponentLoader("themeToggle", ThemeToggleLoader.class)
                .build();
    }

    @Bean
    public ComponentRegistration slider() {
        return ComponentRegistrationBuilder.create(Slider.class)
                .withComponentLoader("slider", SliderLoader.class)
                .build();
    }
}
