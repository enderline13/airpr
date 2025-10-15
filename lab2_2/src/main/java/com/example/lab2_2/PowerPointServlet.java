package com.example.lab2_2;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.*;

@WebServlet(name = "PowerPointServlet", value = "/PowerPointServlet")
public class PowerPointServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Путь к файлу презентации
        String filePath = getServletContext().getRealPath("/presentations/sample.pptx");
        File file = new File(filePath);

        try {
            if (file.exists()) {
                // Устанавливаем заголовки для скачивания файла
                response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
                response.setHeader("Content-Disposition", "attachment; filename=\"presentation.pptx\"");
                response.setContentLength((int) file.length());

                // Отправляем файл
                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream outputStream = response.getOutputStream()) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                // Если файла нет - создаем простую презентацию программно
                createAndSendPresentation(response);
            }

        } catch (Exception e) {

        }
    }

    private void createAndSendPresentation(HttpServletResponse response) throws Exception {
        // Устанавливаем заголовки
        response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        response.setHeader("Content-Disposition", "attachment; filename=\"generated_presentation.pptx\"");

        // Создаем временный файл презентации
        File tempFile = File.createTempFile("presentation", ".pptx");
        tempFile.deleteOnExit();

        // Здесь можно добавить создание презентации через Apache POI
        // Для простоты просто создадим пустой файл с правильным расширением
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            // Можно добавить минимальное содержимое для валидного PPTX
            fos.write("Minimal PPTX content".getBytes());
        }

        // Отправляем созданный файл
        try (FileInputStream fis = new FileInputStream(tempFile);
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
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