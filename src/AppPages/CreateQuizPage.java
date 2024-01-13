package AppPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CreateQuizPage extends JFrame {

    public void createQuizPage(String username, String IPAddress, String portNumber){
        setTitle("Utworz quiz!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Dodanie marginesów

        JPanel questionPanel = new JPanel(new FlowLayout());
        JLabel questionLabel = new JLabel("Ilość pytań:");
        JTextField questionTextField = new JTextField(10);
        questionPanel.add(questionLabel);
        questionPanel.add(questionTextField);
        panel.add(questionPanel);

        JPanel timePanel = new JPanel(new FlowLayout());
        JLabel timeLabel = new JLabel("Czas na pytanie (s):");
        JTextField timeTextField = new JTextField(10);
        timePanel.add(timeLabel);
        timePanel.add(timeTextField);
        panel.add(timePanel);

        JButton createQuizButton = new JButton("Utwórz quiz");
        createQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String questionCount = questionTextField.getText();
                String timePerQuestion = timeTextField.getText();

                try{
                    Integer.parseInt(questionCount);
                    Integer.parseInt(timePerQuestion);

                    Socket socket = new Socket(IPAddress, Integer.parseInt(portNumber));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    out.println("create|" + username + "|" + questionCount + "|");
                    System.out.println("Sent quiz creation to server");

                    String response = in.readLine();
                    System.out.println("Received response from server: " + response);

                    SwingUtilities.invokeLater(() -> {
                        CreateQuestionPage createQuestionPage = new CreateQuestionPage();
                        createQuestionPage.createQuestionPage(response, Integer.parseInt(questionCount), 1, IPAddress, portNumber, username);
                        createQuestionPage.setVisible(true);
                    });
                    SwingUtilities.getWindowAncestor(panel).setVisible(false);

                } catch (Exception err){
                    JOptionPane.showMessageDialog(null, "Wpisz poprawne wartosci!", "Niepoprawne wartosci", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(createQuizButton);

        getContentPane().add(BorderLayout.CENTER, panel);
        setLocationRelativeTo(null);
    }
}
