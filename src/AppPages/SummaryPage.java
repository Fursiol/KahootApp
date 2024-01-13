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

public class SummaryPage extends JFrame {
    public void createSummaryPage(String ipAddress, String portNumber, String code, String username){
        setTitle("Podsumowanie quizu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        Object[][] data = new Object[0][0];

        try (Socket socket = new Socket(ipAddress, Integer.parseInt(portNumber));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("quizinfo|" + code + "|");
            System.out.println("Sent request for info to server: " + username);

            String response = in.readLine();
            System.out.println("Received response from server: " + response);

            String[] info = response.split(("\\|"));

            data = new Object[info.length/3][3];

            int cnt = 0;
            for (int i = 0; i< info.length; i++){
                if(i % 3 == 0) data[cnt][0] = info[i];
                if(i % 3 == 1) data[cnt][1] = info[i];
                if(i % 3 == 2){
                    data[cnt][2] = info[i];
                    cnt++;
                }
            }

        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "Nie nawiazano polaczenia:\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Nazwa użytkownika", "Punktacja", "Odpowiedzi"};

        JTable resultsTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton updateButton = new JButton("Aktualizuj");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Socket socket = new Socket(ipAddress, Integer.parseInt(portNumber));
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    out.println("quizinfo|" + code + "|");
                    System.out.println("Sent request for info to server: " + username);

                    String response = in.readLine();
                    System.out.println("Received response from server: " + response);

                    String[] info = response.split(("\\|"));

                    int cnt = 0;
                    for (int i = 0; i < info.length; i++){
                        if(i % 3 == 0) resultsTable.getModel().setValueAt(info[i], cnt, 0);
                        if(i % 3 == 1) resultsTable.getModel().setValueAt(info[i], cnt, 1);
                        if(i % 3 == 2){
                            resultsTable.getModel().setValueAt(info[i], cnt, 2);
                            cnt++;
                        }
                    }

                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "Nie nawiazano polaczenia:\n" + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton exitButton = new JButton("Wyjdź do menu");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    WelcomePage welcomePage = new WelcomePage();
                    welcomePage.welcomePage(username, ipAddress, portNumber);
                    welcomePage.setVisible(true);
                });
                SwingUtilities.getWindowAncestor(panel).setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(exitButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(BorderLayout.CENTER, panel);
        setLocationRelativeTo(null);
    }
}
