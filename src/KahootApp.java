import AppPages.LoginPage;
import javax.swing.*;

public class KahootApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::loginPage);
    }
}
