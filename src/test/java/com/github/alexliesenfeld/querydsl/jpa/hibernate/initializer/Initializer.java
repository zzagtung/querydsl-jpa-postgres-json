package com.github.alexliesenfeld.querydsl.jpa.hibernate.initializer;

import com.github.alexliesenfeld.querydsl.jpa.hibernate.support.SqlCapturingStatementInspector;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> DB_CONTAINER = new PostgreSQLContainer<>("postgres:15.3-alpine");

    private static boolean started = false;

    /**
     * Starting a container costs about as much as booting the Spring context, so the whole suite
     * shares one. The start deliberately does not live in a static initializer: a failure there
     * surfaces as {@code ExceptionInInitializerError} once and {@code NoClassDefFoundError}
     * afterwards, both without the Testcontainers cause. Teardown is an explicit shutdown hook
     * because Ryuk, which would otherwise reap the container, is disabled for this suite.
     */
    private static synchronized void startContainerOnce() {
        if (started) {
            return;
        }
        DB_CONTAINER.start();
        Runtime.getRuntime().addShutdownHook(new Thread(DB_CONTAINER::stop));
        started = true;
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        startContainerOnce();

        TestPropertyValues.of(
                        "spring.datasource.url=" + DB_CONTAINER.getJdbcUrl(),
                        "spring.datasource.username=" + DB_CONTAINER.getUsername(),
                        "spring.datasource.password=" + DB_CONTAINER.getPassword(),
                        "spring.datasource.driverClassName=org.postgresql.Driver",
                        "spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true",
                        "spring.jpa.properties.hibernate.dialect=com.github.alexliesenfeld.querydsl.jpa.hibernate.PostgreSQLJsonDialect",
                        "spring.jpa.properties.hibernate.session_factory.statement_inspector="
                                + SqlCapturingStatementInspector.class.getName(),
                        "spring.jpa.open-in-view=false")
                .applyTo(configurableApplicationContext.getEnvironment());
    }
}
