package eu.chessdata.backend.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Bogdan Oloeriu on 05/07/2016.
 */
public class BasicApi extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String jsonPlayLoad = "";
        if (bufferedReader != null) {
            jsonPlayLoad = jsonPlayLoad + bufferedReader.readLine();
        }
        resp.getWriter().print(jsonPlayLoad);
        resp.getWriter().println("OK");
    }
}
