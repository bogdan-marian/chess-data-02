/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package eu.chessdata.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.http.*;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        resp.getWriter().println(getFileContent());
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
        if(name == null) {
            resp.getWriter().println("Please enter a name");
        }
        resp.getWriter().println("Hello " + name);
    }

    private String getFileContent(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("simpleFile.txt").getFile());
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            StringBuffer sb = new StringBuffer();
            int content;
            while ((content = fileInputStream.read())!=-1){
                sb.append((char)content);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Should not be able to reach this point");//return "No content yet:";
    }
}
