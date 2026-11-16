package vn.edu.hcmute.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Dong EntityManagerFactory khi Tomcat stop/reload undeploy.
 * Neu khong dong, thread cua Hibernate + connection pool se con treo lai
 * va gay canh bao memory leak trong catalina.out.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().log("BT2: khoi tao JPA (Hibernate) ...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JpaConfig.shutdown();
        sce.getServletContext().log("BT2: da dong EntityManagerFactory");
    }
}
