import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class KahootApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KahootApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Kahoot App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Dodanie marginesów

        JLabel titleLabel = new JLabel("Kahoot App", SwingConstants.CENTER);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Pusty obszar dla estetyki

        JPanel ipAddressPanel = createLabeledField("Adres IP:", new JTextField());
        panel.add(ipAddressPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Pusty obszar dla estetyki

        JPanel portPanel = createLabeledField("Numer portu:", new JTextField());
        panel.add(portPanel);

        JPanel usernamePanel = createLabeledField("Nazwa uzytkownika:", new JTextField());
        panel.add(usernamePanel);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Pusty obszar dla estetyki

        JButton connectButton = getButton(ipAddressPanel, portPanel, usernamePanel);
        panel.add(connectButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Pusty obszar dla estetyki

        JLabel authorsLabel = new JLabel("Jakub Furs 148193", SwingConstants.CENTER);
        panel.add(authorsLabel);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private static JButton getButton(JPanel ipAddressPanel, JPanel portPanel, JPanel usernamePanel) {
        JButton connectButton = new JButton("Nawiąż połączenie");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ipAddress = ((JTextField) ipAddressPanel.getComponent(1)).getText();
                String port = ((JTextField) portPanel.getComponent(1)).getText();
                String username = ((JTextField) usernamePanel.getComponent(1)).getText();

                //TODO dodac obsluge tworzenia polaczenia i przy sukcesie przejscie do nastepngo ekranu lub
                //TODO w przypadku niepowodzenia wyswietlenie odpowiedniego komunikatu

                try (Socket socket = new Socket(ipAddress, Integer.parseInt(port));
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    // Przesłanie nazwy użytkownika do serwera
                    out.println(username);
                    System.out.println("Sent username to server: " + username);

                    // Odczytanie odpowiedzi serwera
                    String response = in.readLine();
                    System.out.println("Received response from server: " + response);

                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        });
        return connectButton;
    }

    private static JPanel createLabeledField(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(new JLabel(labelText));
        panel.add(textField);
        return panel;
    }
}