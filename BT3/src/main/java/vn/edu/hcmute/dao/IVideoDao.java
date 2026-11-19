package vn.edu.hcmute.dao;

import java.util.List;

import vn.edu.hcmute.entity.Video;

public interface IVideoDao {

    void insert(Video video);

    void update(Video video);

    void delete(String videoId) throws Exception;

    Video findById(String videoId);

    List<Video> findAll();

    int count();

    /** 10 video moi nhat cho trang chu. */
    List<Video> findLatest(int limit);

    /** Video cua 1 user, co phan trang. */
    List<Video> findByUser(int userid, int page, int pageSize);

    int countByUser(int userid);

    /** Tim theo tieu de trong pham vi video cua user. */
    List<Video> searchByUser(int userid, String keyword);

    void incrementViews(String videoId);
}
