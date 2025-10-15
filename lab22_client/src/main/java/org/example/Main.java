package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import javax.swing.*;
import java.awt.*;


public class ApacheClient extends Frame implements ActionListener {

    Button exitButton = new Button("Выход");
    Button translateButton = new Button("Перевести");
    Button pptButton = new Button("Получить презентацию");
    TextField inputField = new TextField();
    Choice directionChoice = new Choice();

    private static final String SERVLET_URL = "http://localhost:8080/lab2_2_war_exploded/MyServlet";
    private static final String PPT_SERVLET_URL = "http://localhost:8080/lab2_2_war_exploded/PowerPointServlet";

    public ApacheClient() {
        super("Клиент для словаря");
        setLayout(null);
        setBackground(new Color(200, 220, 255));
        setSize(450, 300);

        setupUI();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI() {
        Label titleLabel = new Label("Клиент для онлайн-словаря");
        titleLabel.setBounds(100, 40, 250, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(titleLabel);

        Label directionLabel = new Label("Направление:");
        directionLabel.setBounds(50, 80, 80, 20);
        add(directionLabel);

        directionChoice.add("Русский → Английский");
        directionChoice.add("Английский → Русский");
        directionChoice.setBounds(140, 80, 200, 20);
        add(directionChoice);

        pptButton.setBounds(150, 190, 200, 30);
        pptButton.setBackground(new Color(255, 200, 100));
        pptButton.addActionListener(this);
        add(pptButton);

        Label inputLabel = new Label("Слово для перевода:");
        inputLabel.setBounds(50, 110, 120, 20);
        add(inputLabel);

        inputField.setBounds(180, 110, 160, 25);
        add(inputField);

        translateButton.setBounds(100, 150, 100, 30);
        translateButton.addActionListener(this);
        add(translateButton);

        exitButton.setBounds(220, 150, 80, 30);
        exitButton.addActionListener(this);
        add(exitButton);

        inputField.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == exitButton) {
            System.exit(0);
        } else if (ae.getSource() == translateButton || ae.getSource() == inputField) {
            openTranslationInBrowser();
        } else if (ae.getSource() == pptButton) {
            downloadAndOpenPowerPoint();
        }
    }

    private void downloadAndOpenPowerPoint() {
        try {

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(PPT_SERVLET_URL);

                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    byte[] fileContent = EntityUtils.toByteArray(response.getEntity());

                    // Сохраняем во временный файл
                    File tempFile = File.createTempFile("presentation", ".pptx");
                    tempFile.deleteOnExit();

                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        fos.write(fileContent);
                    }

                    statusLabel.setText("Открываем презентацию...");

                    // Открываем файл с помощью Desktop
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.OPEN)) {
                        desktop.open(tempFile);
                    } else {
                        // Альтернатива: показываем диалог открытия файла
                        JOptionPane.showMessageDialog(this,
                                "Файл сохранен: " + tempFile.getAbsolutePath(),
                                "Файл скачан", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    throw new Exception("Сервер вернул ошибку: " + statusCode);
                }
            }
        } catch (Exception e) {

        }
    }

    private void openTranslationInBrowser() {
        String word = inputField.getText().trim();

        if (word.isEmpty()) {
            return;
        }

        try {
            String url;
            if (directionChoice.getSelectedIndex() == 0) {
                // Русский → Английский
                url = SERVLET_URL + "?txt=" + URLEncoder.encode(word, "UTF-8") + "&trans=";
            } else {
                // Английский → Русский
                url = SERVLET_URL + "?txt=&trans=" + URLEncoder.encode(word, "UTF-8");
            }

            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ApacheClient();
    }
}