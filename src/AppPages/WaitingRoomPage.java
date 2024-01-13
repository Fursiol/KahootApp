package AppPages;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WaitingRoomPage extends JFrame {
    public void createWaitingRoomPage(String username, String IPAddress, String portNumber, String code){
        setTitle("Poczekalnia quizu: " + code);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        JPanel panel = new JPanel(new FlowLayout());

        getContentPane().add(BorderLayout.CENTER, panel);
        setLocationRelativeTo(null);
    }
}
