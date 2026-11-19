package vn.edu.hcmute.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.edu.hcmute.config.JpaConfig;
import vn.edu.hcmute.entity.Video;

public class VideoDao implements IVideoDao {

    @Override
    public void insert(Video video) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.persist(video);
            trans.commit();
        } catch (RuntimeException e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void update(Video video) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.merge(video);
            trans.commit();
        } catch (RuntimeException e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void delete(String videoId) throws Exception {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            Video video = enma.find(Video.class, videoId);
            if (video == null) {
                throw new Exception("Không tìm thấy video có id = " + videoId);
            }
            enma.remove(video);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public Video findById(String videoId) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            return enma.find(Video.class, videoId);
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findAll() {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            return enma.createNamedQuery("Video.findAll", Video.class).getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public int count() {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            Query query = enma.createQuery("SELECT COUNT(v) FROM Video v");
            return ((Long) query.getSingleResult()).intValue();
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findLatest(int limit) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<Video> query = enma.createQuery(
                    "SELECT v FROM Video v ORDER BY v.createdAt DESC, v.videoId DESC", Video.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findByUser(int userid, int page, int pageSize) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<Video> query = enma.createQuery(
                    "SELECT v FROM Video v WHERE v.user.userid = :uid "
                            + "ORDER BY v.createdAt DESC, v.videoId DESC", Video.class);
            query.setParameter("uid", userid);
            query.setFirstResult(page * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public int countByUser(int userid) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            Query query = enma.createQuery("SELECT COUNT(v) FROM Video v WHERE v.user.userid = :uid");
            query.setParameter("uid", userid);
            return ((Long) query.getSingleResult()).intValue();
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> searchByUser(int userid, String keyword) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<Video> query = enma.createQuery(
                    "SELECT v FROM Video v WHERE v.user.userid = :uid AND LOWER(v.title) LIKE :kw "
                            + "ORDER BY v.createdAt DESC", Video.class);
            query.setParameter("uid", userid);
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public void incrementViews(String videoId) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            Query query = enma.createQuery("UPDATE Video v SET v.views = v.views + 1 WHERE v.videoId = :id");
            query.setParameter("id", videoId);
            query.executeUpdate();
            trans.commit();
        } catch (RuntimeException e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }
}
