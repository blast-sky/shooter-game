package com.astrog.shootergame.server.internal.database;

import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public class SessionFactoryConfiguration {

    private volatile static SessionFactory factory;

    @SneakyThrows
    public static SessionFactory getFactory() {
        if (factory == null) {
            synchronized (SessionFactoryConfiguration.class) {
                if (factory == null) {
                    factory = getSessionFactory();
                }
            }
        }
        return factory;
    }

    private static SessionFactory getSessionFactory() {
        Configuration configuration = getConfiguration();
        return configuration.buildSessionFactory();
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();

        Properties propertiesWithEnvironmentVariables = getProperties();
        configuration.setProperties(propertiesWithEnvironmentVariables);

        configuration.addAnnotatedClass(Score.class);

        return configuration;
    }

    private static Properties getProperties() {
        Properties properties = new Properties();

        properties.put(Environment.DRIVER, "org.postgresql.Driver");

        //jdbc:postgresql://${hibernate_db_host}/${hibernate_db_name}
        properties.put(Environment.URL, "jdbc:postgresql://" +
            System.getenv("hibernate_db_host") + "/" + System.getenv("hibernate_db_name"));

        properties.put(Environment.CONNECTION_PROVIDER, "org.hibernate.connection.C3P0ConnectionProvider");

        properties.put(Environment.USER, "postgres");
        properties.put(Environment.PASS, System.getenv("hibernate_password"));

        //properties.put(Environment.C3P0_MAX_SIZE, "2");

        properties.put(Environment.SHOW_SQL, "true");

        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

        properties.put(Environment.HBM2DDL_AUTO, "update");

        return properties;
    }
}
