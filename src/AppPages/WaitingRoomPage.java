package AppPages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WaitingRoomPage extends JFrame {
    public void createWaitingRoomPage(String username, String IPAddress, String portNumber, String code){
        setTitle("Poczekalnia quizu: " + code);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        JPanel panel = new JPanel(new BorderLayout());

        // Etykieta z wynikiem
        JLabel resultLabel = new JLabel("Wynik aktualizacji:");
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
