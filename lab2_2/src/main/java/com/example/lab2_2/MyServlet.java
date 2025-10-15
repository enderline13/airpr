package com.example.lab2_2;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "MyServlet", value = "/MyServlet")
public class MyServlet extends HttpServlet {
    private Map<String, String> dictionary = new HashMap<>();
    private Map<String, String> reverseDictionary = new HashMap<>();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadDictionary();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String rusWord = request.getParameter("txt");
        String engWord = request.getParameter("trans");

        String result = "";
        String direction = "";
        String originalWord = "";

        try {
            // Определяем направление перевода
            if (rusWord != null && !rusWord.trim().isEmpty()) {
                // Перевод с русского на английский
                originalWord = rusWord.trim();
                result = translate(originalWord, true);
                direction = "русский - английский";
            } else if (engWord != null && !engWord.trim().isEmpty()) {
                // Перевод с английского на русский
                originalWord = engWord.trim();
                result = translate(originalWord, false);
                direction = "английский - русский";
            } else {
                result = "Введите слово для перевода";
            }

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Словарь</title>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            out.println("</head>");
            out.println("<body bgcolor='#aaccff'>");
            out.println("<font color='green' size='6'>");
            out.println("Результат перевода");
            out.println("</font>");
            out.println("<br><br>");

            if (!result.equals("Введите слово для перевода")) {
                out.println("<font color='blue' size='5'>");
                out.println("Направление: " + direction);
                out.println("</font>");
                out.println("<br>");
                out.println("<font color='black' size='5'>");
                out.println("Исходное слово: " + originalWord);
                out.println("<br>");
                out.println("Перевод: " + result);
                out.println("</font>");
            } else {
                out.println("<font color='red' size='5'>");
                out.println(result);
                out.println("</font>");
            }

            out.println("<br><br>");
            out.println("<a href='dictionary.html'>Вернуться к форме перевода</a>");
            out.println("<br><br>");
            out.println("<font color='gray' size='3'>");
            out.println("Всего слов в словаре: " + dictionary.size());
            out.println("</font>");
            out.println("</body>");
            out.println("</html>");

        } finally {
            out.close();
        }
    }

    private void loadDictionary() {
        try {
            URL dictionaryUrl = getServletContext().getResource("/WEB-INF/dictionary.txt");
            if (dictionaryUrl != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(dictionaryUrl.openStream(), "UTF-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String russian = parts[0].trim().toLowerCase();
                            String english = parts[1].trim().toLowerCase();
                            dictionary.put(russian, english);
                            reverseDictionary.put(english, russian);
                        }
                    }
                }
                reader.close();
                System.out.println("Словарь загружен. Записей: " + dictionary.size());
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки словаря: " + e.getMessage());
        }
    }
    private String translate(String word, boolean fromRussianToEnglish) {
        String lowerWord = word.toLowerCase();

        if (fromRussianToEnglish) {
            return dictionary.getOrDefault(lowerWord, "слово не найдено в словаре");
        } else {
            return reverseDictionary.getOrDefault(lowerWord, "word not found in dictionary");
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