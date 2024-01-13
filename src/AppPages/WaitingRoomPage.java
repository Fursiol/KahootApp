package AppPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class WaitingRoomPage extends JFrame {
    public void createWaitingRoomPage(String username, String IPAddress, String portNumber, String code, Boolean owner){
        setTitle("Poczekalnia quizu: " + code);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        JPanel panel = new JPanel(new BorderLayout());

        // Etykieta z wynikiem
        JLabel resultLabel = new JLabel("Gracze:");
        panel.add(resultLabel, BorderLayout.CENTER);

        // Przycisk "Aktualizuj"
        JButton updateButton = new JButton("Aktualizuj");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Socket socket = new Socket(IPAddress, Integer.parseInt(portNumber));
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    out.println("w8info|" + code + "|");
                    System.out.println("Sent for info to server: " + code);

                    String response = in.readLine();
                    System.out.println("Received response from server: " + response);

                    resultLabel.setText("Gracze: " + response);
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "Nie nawiazano polaczenia:\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Przycisk "Gotowy"
        JButton doneButton = new JButton("Gotowy");
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Socket socket = new Socket(IPAddress, Integer.parseInt(portNumber));
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    out.println("start|" + code + "|");
                    System.out.println("Sent for info to server: " + code);

                    String response = in.readLine();
                    System.out.println("Received response from server: " + response);

                    if (owner){
                        System.out.println("Your quiz has started!");

                        SwingUtilities.invokeLater(() -> {
                            SummaryPage summaryPage = new SummaryPage();
                            summaryPage.createSummaryPage(IPAddress, portNumber, code, username);
                            summaryPage.setVisible(true);
                        });

                        SwingUtilities.getWindowAncestor(panel).setVisible(false);
                    }
                    else {
                        System.out.println("Your quiz has started! Good luck!");

                        Socket socket2 = new Socket(IPAddress, Integer.parseInt(portNumber));
                        BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
                        PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);

                        out2.println("answer|" + code + "|" + username + "|" + "0|");
                        System.out.println("Sent for question to server: 0");

                        response = in2.readLine();
                        System.out.println("Received response from server: " + response);

                        String[] question = response.split("\\|");

                        SwingUtilities.invokeLater(() -> {
                            AnswerPage answerPage = new AnswerPage();
                            answerPage.createAnswerPage(IPAddress, portNumber, code, username, question, 1);
                            answerPage.setVisible(true);
                        });

                        SwingUtilities.getWindowAncestor(panel).setVisible(false);
                    }

                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "Nie nawiazano polaczenia:\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Panel z przyciskami
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(doneButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(BorderLayout.CENTER, panel);
        setLocationRelativeTo(null);
    }
}
