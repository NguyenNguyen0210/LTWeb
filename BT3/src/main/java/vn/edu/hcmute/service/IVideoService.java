package vn.edu.hcmute.service;

import java.util.List;

import vn.edu.hcmute.entity.Video;

public interface IVideoService {

    List<Video> findLatest(int limit);

    List<Video> findByUser(int userid, int page, int pageSize);

    int countByUser(int userid);

    List<Video> searchByUser(int userid, String keyword);

    Video findById(String videoId);

    void insert(Video video);

    void update(Video video);

    void delete(String videoId) throws Exception;

    void increaseViews(String videoId);
}
