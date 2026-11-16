package vn.edu.hcmute.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmute.dao.IVideoDao;
import vn.edu.hcmute.dao.VideoDao;
import vn.edu.hcmute.util.Constants;

/** Trang chu: 10 video moi nhat. */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IVideoDao videoDao = new VideoDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("latestVideos", videoDao.findLatest(Constants.HOME_VIDEO_LIMIT));
        req.getRequestDispatcher("/views/home.jsp").forward(req, resp);
    }
}
