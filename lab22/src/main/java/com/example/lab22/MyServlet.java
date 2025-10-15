package com.example.lab22;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "MyServlet", value = "/MyServlet")
public class MyServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String rus_word = " " + request.getParameter("txt");

        String translation = translateWord(rus_word);

        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MyServlet</title>");
            out.println("</head>");
            out.println("<body bgcolor='#aaccff'>");
            out.println("<form>");
            out.println("<h2> Перевод слова: " + rus_word + "</h2>");
            out.println("<h2> Результат: " + translation + "</h2>");
            out.println("<br><a href='dictionary.html'>Назад</a>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    private String translateWord(String word) {
        switch(word.toLowerCase()) {
            case "привет": return "hello";
            case "мир": return "world";
            case "кот": return "cat";
            case "собака": return "dog";
            default: return "слово не найдено в словаре";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}