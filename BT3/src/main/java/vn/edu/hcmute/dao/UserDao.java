package vn.edu.hcmute.dao;

import java.util.List;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.edu.hcmute.config.JpaConfig;
import vn.edu.hcmute.entity.User;

public class UserDao implements IUserDao {

    @Override
    public void insert(User user) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.persist(user);
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
    public void update(User user) {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.merge(user);
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
    public void delete(int userid) throws Exception {
        EntityManager enma = JpaConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            User user = enma.find(User.class, userid);
            if (user == null) {
                throw new Exception("Không tìm thấy user có id = " + userid);
            }
            enma.remove(user);
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
    public User findById(int userid) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            return enma.find(User.class, userid);
        } finally {
            enma.close();
        }
    }

    @Override
    public User findByEmail(String email) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            TypedQuery<User> query = enma.createQuery(
                    "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)", User.class);
            query.setParameter("email", email);
            List<User> list = query.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            enma.close();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            Query query = enma.createQuery("SELECT COUNT(u) FROM User u WHERE LOWER(u.email) = LOWER(:email)");
            query.setParameter("email", email);
            return ((Long) query.getSingleResult()) > 0;
        } finally {
            enma.close();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            return enma.createNamedQuery("User.findAll", User.class).getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public int count() {
        EntityManager enma = JpaConfig.getEntityManager();
        try {
            Query query = enma.createQuery("SELECT COUNT(u) FROM User u");
            return ((Long) query.getSingleResult()).intValue();
        } finally {
            enma.close();
        }
    }

    @Override
    public boolean updateProfileDetails(int userId, String fullname, String phone, String avatar) {
        return updateActiveUser(enma -> {
            String avatarAssignment = avatar == null ? "" : ", u.avatar = :avatar";
            Query query = enma.createQuery("UPDATE User u SET u.fullname = :fullname, u.phone = :phone"
                    + avatarAssignment + ", u.rowVersion = u.rowVersion + 1 WHERE u.userid = :id AND u.status = :active");
            query.setParameter("id", userId);
            query.setParameter("active", User.STATUS_ACTIVE);
            query.setParameter("fullname", fullname);
            query.setParameter("phone", phone);
            if (avatar != null) {
                query.setParameter("avatar", avatar);
            }
            return query;
        });
    }

    @Override
    public boolean changeEmail(int userId, String currentEmail, String newEmail) {
        return updateActiveUser(enma -> enma.createQuery(
                "UPDATE User u SET u.email = :newEmail, u.otp = null, u.otpExpiry = null, u.rowVersion = u.rowVersion + 1 "
                        + "WHERE u.userid = :id AND u.email = :currentEmail AND u.status = :active")
                .setParameter("newEmail", newEmail)
                .setParameter("currentEmail", currentEmail)
                .setParameter("id", userId)
                .setParameter("active", User.STATUS_ACTIVE));
    }

    private boolean updateActiveUser(Function<EntityManager, Query> queryBuilder) {
        try (EntityManager enma = JpaConfig.getEntityManager()) {
            EntityTransaction transaction = enma.getTransaction();
            try {
                transaction.begin();
                int affected = queryBuilder.apply(enma).executeUpdate();
                transaction.commit();
                return affected == 1;
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}
