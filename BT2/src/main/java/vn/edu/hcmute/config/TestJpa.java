package vn.edu.hcmute.config;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.hcmute.entity.Category;
import vn.edu.hcmute.entity.Video;

/**
 * Buoc 5 cua huong dan: test nhanh JPA khong can chay web.
 *
 * Cach chay trong VS Code / Eclipse: chuot phai > Run Java (hoac Run As > Java Application).
 * Cach chay bang terminal (sau khi da "mvn package"):
 *   java -cp "target/classes;target/ltweb-bt2-category-jpa/WEB-INF/lib/*" vn.edu.hcmute.config.TestJpa
 *
 * Yeu cau: SQL Server dang chay va database webst2 da ton tai.
 */
public class TestJpa {

    public static void main(String[] args) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();

        try {
            trans.begin();

            Category cate = new Category();
            cate.setCategoryname("Iphone");
            cate.setImages("abc.jpg");
            cate.setStatus(1);

            Video video = new Video();
            video.setVideoId("v01");
            video.setTitle("test");
            video.setCategory(cate);

            enma.persist(cate);   // nho: persist cate TRUOC de co categoryid
            enma.persist(video);

            trans.commit();
            System.out.println(">>> Insert OK: categoryid = " + cate.getCategoryid());

            List<Category> list = enma.createNamedQuery("Category.findAll", Category.class).getResultList();
            System.out.println(">>> Tong so category = " + list.size());
            for (Category c : list) {
                System.out.println("    " + c.getCategoryid() + " | " + c.getCategoryname()
                        + " | status=" + c.getStatus() + " | images=" + c.getImages());
            }
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            e.printStackTrace();
        } finally {
            enma.close();
            JpaConfig.shutdown();
        }
    }
}
