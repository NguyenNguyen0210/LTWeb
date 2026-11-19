package vn.edu.hcmute.config;

import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Lop cau hinh JPA.
 *
 * LUU Y QUAN TRONG: EntityManagerFactory la doi tuong "nang", chi nen tao MOT LAN
 * cho ca ung dung (no chua connection pool + metadata cua tat ca entity).
 * Vi vay o day dung singleton. Neu tao factory moi lan goi getEntityManager()
 * thi moi request se mo mot pool moi -> ro ri ket noi, cham, va sau vai chuc
 * request SQL Server se bao "too many connections".
 */
public class JpaConfig {

    private static final String PERSISTENCE_UNIT = "ltweb-bt3";

    private static EntityManagerFactory factory;

    private JpaConfig() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, databaseProperties());
        }
        return factory;
    }

    private static Map<String, String> databaseProperties() {
        return Map.of(
                "jakarta.persistence.jdbc.url", setting("LTWEB_DB_URL",
                        "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=webst3"),
                "jakarta.persistence.jdbc.user", setting("LTWEB_DB_USER", "sa"),
                "jakarta.persistence.jdbc.password", setting("LTWEB_DB_PASSWORD", ""),
                "jakarta.persistence.jdbc.driver", setting("LTWEB_DB_DRIVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
                "hibernate.dialect", setting("LTWEB_DB_DIALECT", "org.hibernate.dialect.SQLServerDialect"));
    }

    private static String setting(String key, String fallback) {
        String value = System.getProperty(key, System.getenv(key));
        return value == null || value.isBlank() ? fallback : value;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /** Goi khi ung dung dung (ServletContextListener) de dong pool sach se. */
    public static synchronized void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
        factory = null;
    }
}
