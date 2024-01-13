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

public class CreateQuestionPage extends JFrame {

    public void createQuestionPage(String code, Integer questions, Integer count, String ipAddress, String port, String username){
        setTitle("Tworzenie pytania nr. " + count.toString());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);

        JPanel panel = new JPanel(new GridLayout(7, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Dodanie marginesów

        JPanel contentPanel = new JPanel(new FlowLayout());
        JLabel contentLabel = new JLabel("Treść pytania:");
        JTextField contentTextField = new JTextField(30);
        contentPanel.add(contentLabel);
        contentPanel.add(contentTextField);
        panel.add(contentPanel);

        JPanel correctAnswerPanel = new JPanel(new FlowLayout());
        JLabel correctAnswerLabel = new JLabel("Poprawna odpowiedź:");
        JTextField correctAnswerTextField = new JTextField(30);
        correctAnswerPanel.add(correctAnswerLabel);
        correctAnswerPanel.add(correctAnswerTextField);
        panel.add(correctAnswerPanel);

        JPanel incorrectAnswer1Panel = new JPanel(new FlowLayout());
        JLabel incorrectAnswer1Label = new JLabel("Niepoprawna odpowiedź 1:");
        JTextField incorrectAnswer1TextField = new JTextField(30);
        incorrectAnswer1Panel.add(incorrectAnswer1Label);
        incorrectAnswer1Panel.add(incorrectAnswer1TextField);
        panel.add(incorrectAnswer1Panel);

        JPanel incorrectAnswer2Panel = new JPanel(new FlowLayout());
        JLabel incorrectAnswer2Label = new JLabel("Niepoprawna odpowiedź 2:");
        JTextField incorrectAnswer2TextField = new JTextField(30);
        incorrectAnswer2Panel.add(incorrectAnswer2Label);
        incorrectAnswer2Panel.add(incorrectAnswer2TextField);
        panel.add(incorrectAnswer2Panel);

        JPanel incorrectAnswer3Panel = new JPanel(new FlowLayout());
        JLabel incorrectAnswer3Label = new JLabel("Niepoprawna odpowiedź 3:");
        JTextField incorrectAnswer3TextField = new JTextField(30);
        incorrectAnswer3Panel.add(incorrectAnswer3Label);
        incorrectAnswer3Panel.add(incorrectAnswer3TextField);
        panel.add(incorrectAnswer3Panel);

        JButton createQuestionButton = new JButton("Utwórz pytanie");
        createQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String content = contentTextField.getText();
                String correctAnswer = correctAnswerTextField.getText();
                String incorrectAnswer1 = incorrectAnswer1TextField.getText();
                String incorrectAnswer2 = incorrectAnswer2TextField.getText();
                String incorrectAnswer3 = incorrectAnswer3TextField.getText();

                String sendQuestion = "add|" + code + "|" + content + "|" + correctAnswer
                        + "|" + incorrectAnswer1 + "|" + incorrectAnswer2 + "|"
                        + incorrectAnswer3;

                try (Socket socket = new Socket(ipAddress, Integer.parseInt(port));
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    out.println(sendQuestion);
                    System.out.println("Sent question to server!");

                    String response = in.readLine();
                    System.out.println("Received response from server: " + response);

                    if(count < questions) {
                        SwingUtilities.invokeLater(() -> {
                            CreateQuestionPage createQuestionPage = new CreateQuestionPage();
                            createQuestionPage.createQuestionPage(code, questions, count + 1, ipAddress, port, username);
                            createQuestionPage.setVisible(true);
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            WaitingRoomPage waitingRoomPage = new WaitingRoomPage();
                            waitingRoomPage.createWaitingRoomPage(username, ipAddress, port, code);
                            waitingRoomPage.setVisible(true);
                        });
                    }
                    SwingUtilities.getWindowAncestor(panel).setVisible(false);
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "Nie nawiazano polaczenia:\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(createQuestionButton);

        getContentPane().add(BorderLayout.CENTER, panel);
        setLocationRelativeTo(null);
    }
}
