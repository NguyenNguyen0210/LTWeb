package vn.edu.hcmute.service;

import java.util.List;

import vn.edu.hcmute.dao.IVideoDao;
import vn.edu.hcmute.dao.VideoDao;
import vn.edu.hcmute.entity.Video;
import vn.edu.hcmute.util.FormValidation;

public class VideoServiceImpl implements IVideoService {

    private final IVideoDao videoDao = new VideoDao();

    @Override
    public List<Video> findLatest(int limit) {
        return videoDao.findLatest(limit);
    }

    @Override
    public List<Video> findByUser(int userid, int page, int pageSize) {
        return videoDao.findByUser(userid, page, pageSize);
    }

    @Override
    public int countByUser(int userid) {
        return videoDao.countByUser(userid);
    }

    @Override
    public List<Video> searchByUser(int userid, String keyword) {
        return videoDao.searchByUser(userid, keyword);
    }

    @Override
    public Video findById(String videoId) {
        return videoId == null ? null : videoDao.findById(videoId);
    }

    @Override
    public void insert(Video video) {
        validate(video);
        if (video.getUser() == null) {
            throw new IllegalArgumentException("Video phải thuộc về một user");
        }
        videoDao.insert(video);
    }

    @Override
    public void update(Video video) {
        validate(video);
        videoDao.update(video);
    }

    private void validate(Video video) {
        video.setTitle(FormValidation.required(video.getTitle(), "Tiêu đề video", 200));
        video.setDescription(FormValidation.optional(video.getDescription(), "Mô tả", 500));
        if (video.getActive() != 0 && video.getActive() != 1) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
    }

    @Override
    public void delete(String videoId) throws Exception {
        videoDao.delete(videoId);
    }

    @Override
    public void increaseViews(String videoId) {
        videoDao.incrementViews(videoId);
    }
}
