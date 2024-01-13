package AppPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AnswerPage extends JFrame {
    public void createAnswerPage(String ipAddress, String port, String code, String username, String[] question, Integer count){
        setTitle("Pytanie " + (count + 1) +", czas: " + question[0] + "s");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 300);

        JPanel panel = new JPanel(new BorderLayout());

        Timer timer = new Timer(Integer.parseInt(question[0]) * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Socket socket = new Socket(ipAddress, Integer.parseInt(port));
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    out.println("answer|" + code + "|" + username + "|" + count + "|  |");
                    System.out.println("Times up!");

                    String response = in.readLine();
                    System.out.println("Received response from server: " + response);

                    if(response.split("\\|").length >= question.length) {

                        String[] nextQuestion = response.split("\\|");

                        SwingUtilities.invokeLater(() -> {
                            AnswerPage answerPage = new AnswerPage();
                            answerPage.createAnswerPage(ipAddress, port, code, username, nextQuestion, count + 1);
                            answerPage.setVisible(true);
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            SummaryPage summaryPage = new SummaryPage();
                            summaryPage.createSummaryPage(ipAddress, port, code, username);
                            summaryPage.setVisible(true);
                        });
                    }
                    SwingUtilities.getWindowAncestor(panel).setVisible(false);
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "Nie nawiazano polaczenia:\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        timer.setRepeats(false);
        timer.start();

        JLabel questionLabel = new JLabel(question[1]);
        panel.add(questionLabel, BorderLayout.NORTH);

        JPanel answerPanel = new JPanel(new GridLayout(2, 2));
        for (int i = 2; i < 6; i++) {
            JButton answerButton = new JButton(question[i]);
            answerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    timer.stop();
                    String selectedAnswer = ((JButton) e.getSource()).getText();

                    try (Socket socket = new Socket(ipAddress, Integer.parseInt(port));
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                        out.println("answer|" + code + "|" + username + "|" + count + "|" + selectedAnswer + "|");
                        System.out.println("Sent for info to server: " + code);

                        String response = in.readLine();
                        System.out.println("Received response from server: " + response);

                        if(response.split("\\|").length >= question.length) {

                            String[] nextQuestion = response.split("\\|");

                            SwingUtilities.invokeLater(() -> {
                                AnswerPage answerPage = new AnswerPage();
                                answerPage.createAnswerPage(ipAddress, port, code, username, nextQuestion, count + 1);
                                answerPage.setVisible(true);
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                SummaryPage summaryPage = new SummaryPage();
                                summaryPage.createSummaryPage(ipAddress, port, code, username);
                                summaryPage.setVisible(true);
                            });
                        }
                        SwingUtilities.getWindowAncestor(panel).setVisible(false);
                    } catch (IOException err) {
                        JOptionPane.showMessageDialog(null, "Nie nawiazano polaczenia:\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            answerPanel.add(answerButton);
        }
        panel.add(answerPanel, BorderLayout.CENTER);

        String[] columnNames = {"Nazwa użytkownika", "Wynik"};
        Object[][] data = new Object[(question.length - 6)/2][2];
        int cnt = 0;
        for(int i = 6; i < question.length; i++){
            if(i % 2 == 0){
                data[cnt][0] = question[i];
                data[cnt][1] = question[i+1];
                cnt++;
            }
        }

        JTable scoreTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        panel.add(scrollPane, BorderLayout.EAST);

        getContentPane().add(BorderLayout.CENTER, panel);
        setLocationRelativeTo(null);
    }
}
