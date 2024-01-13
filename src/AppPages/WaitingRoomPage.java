package AppPages;

import javax.swing.*;
import java.awt.*;

public class WaitingRoomPage extends JFrame {
    public void createWaitingRoomPage(String username, String IPAddress, String portNumber){
        setTitle("Poczekalnia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        JPanel panel = new JPanel(new FlowLayout());

        getContentPane().add(BorderLayout.CENTER, panel);
        setLocationRelativeTo(null);
    }
}
