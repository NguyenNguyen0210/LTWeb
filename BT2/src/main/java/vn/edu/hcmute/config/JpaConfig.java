package vn.edu.hcmute.config;

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

    private static final String PERSISTENCE_UNIT = "jpa-hibernate-mysql";

    private static EntityManagerFactory factory;

    private JpaConfig() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return factory;
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
