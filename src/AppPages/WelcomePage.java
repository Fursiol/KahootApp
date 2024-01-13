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
public class WelcomePage extends JFrame{
    public void welcomePage(String username, String IPAddress, String portNumber) {
        setTitle("Witaj " + username + "!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        JPanel panel = new JPanel(new FlowLayout());

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Dodanie marginesów


        JPanel createQuizPanel = new JPanel(new FlowLayout());
        JButton createQuizButton = new JButton("Utwórz quiz");
        createQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    CreateQuizPage createQuizPage = new CreateQuizPage();
                    createQuizPage.createQuizPage(username, IPAddress, portNumber);
                    createQuizPage.setVisible(true);
                });
                SwingUtilities.getWindowAncestor(panel).setVisible(false);
            }
        });
        createQuizPanel.add(createQuizButton);
        panel.add(createQuizPanel);

        JLabel joinLabel = new JLabel("lub dołącz, jeśli posiadasz kod quizu");
        panel.add(joinLabel);

        JTextField codeTextField = new JTextField(20);
        panel.add(codeTextField);

        JPanel joinQuizPanel = new JPanel(new FlowLayout());
        JButton joinQuizButton = new JButton("Dołącz do quizu");
        joinQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = codeTextField.getText();
                if(code.isEmpty()) JOptionPane.showMessageDialog(null, "Uzupelnij kod quizu!", "Brakujace informacje!", JOptionPane.ERROR_MESSAGE);
                else {
                    try (Socket socket = new Socket(IPAddress, Integer.parseInt(portNumber));
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                        out.println("join|" + code + "|" + username + "|");
                        System.out.println("Sent quiz-code to server: " + code);

                        String response = in.readLine();
                        System.out.println("Received response from server: " + response);

                        if(response.equals("N")){
                            JOptionPane.showMessageDialog(null, "Nie ma takiego quizu!", "Zly kod quizu!", JOptionPane.ERROR_MESSAGE);
                        }
                        else{
                            SwingUtilities.invokeLater(() -> {
                                WaitingRoomPage waitingRoomPage = new WaitingRoomPage();
                                waitingRoomPage.createWaitingRoomPage(username, IPAddress, portNumber, code, false);
                                waitingRoomPage.setVisible(true);
                            });
                            SwingUtilities.getWindowAncestor(panel).setVisible(false);
                        }

                    } catch (IOException err) {
                        JOptionPane.showMessageDialog(null, "Nie nawiazano polaczenia:\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        joinQuizPanel.add(joinQuizButton);
        panel.add(joinQuizPanel);

        getContentPane().add(BorderLayout.CENTER, panel);
        setLocationRelativeTo(null);
    }
}
