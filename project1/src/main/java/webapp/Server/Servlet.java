package webapp.Server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;

@WebServlet(name = "Servlet", urlPatterns = "/Servlet")
public class Servlet extends HttpServlet {
  private Gson gson = new Gson();

  @Override
  protected void doPost(
    HttpServletRequest request, 
    HttpServletResponse response) throws IOException {
      System.out.println("hey I am working");
      String jsonString = this.gson.toJson("I am a Servlet");

      PrintWriter out = response.getWriter();
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      out.print(jsonString);
      out.flush();   
  }
}