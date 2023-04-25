package com.astrog.shootergame.server.internal.database;

import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionFactoryConfiguration {

    private volatile static SessionFactory factory;

    @SneakyThrows
    public static SessionFactory getFactory() {
        if (factory == null) {
            synchronized (SessionFactory.class) {
                if (factory == null) {
                    Class.forName("org.postgresql.Driver");
                    factory = getSessionFactory();
                }
            }
        }
        return factory;
    }

    private static SessionFactory getSessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure()
            .build();

        try {
            return new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }
}
