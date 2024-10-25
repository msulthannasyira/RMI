import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

public class BankClientGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField pinField;
    private JTextArea outputArea;
    private JTextField accountField;
    private JTextField amountField;

    private Bank bank;
    private String loggedAccount;

    public BankClientGUI() {
        setTitle("Bank Unhan");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Welcome Message
        JOptionPane.showMessageDialog(this, "Selamat datang di Bank Unhan", "Welcome", JOptionPane.INFORMATION_MESSAGE);

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("PIN:"));
        pinField = new JPasswordField();
        loginPanel.add(pinField);

        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton);

        JButton exitButton = new JButton("Exit");
        loginPanel.add(exitButton);

        add(loginPanel, BorderLayout.NORTH);

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Action listeners
        loginButton.addActionListener(new LoginAction());
        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            int pin = Integer.parseInt(new String(pinField.getPassword()));

            try {
                bank = (Bank) Naming.lookup("rmi://localhost/BankService");

                if (bank.login(username, pin)) {
                    loggedAccount = bank.getAccountForUser(username);
                    showMenu();
                } else {
                    outputArea.setText("Invalid login credentials.");
                }
            } catch (Exception ex) {
                outputArea.setText("Bank Client failed: " + ex);
            }
        }
    }

    private void showMenu() {
        String menu = "Welcome! Your account: " + loggedAccount + "\n"
                + "1. Check Balance\n"
                + "2. Transfer Funds\n"
                + "3. Top Up\n"
                + "4. Exit";
        String choice = (String) JOptionPane.showInputDialog(this, menu, "Menu", JOptionPane.QUESTION_MESSAGE, null, null, null);

        if (choice != null) {
            switch (choice) {
                case "1":
                    checkBalance();
                    break;
                case "2":
                    transferFunds();
                    break;
                case "3":
                    topUp();
                    break;
                case "4":
                    System.exit(0);
                    break;
            }
        }
    }

    private void checkBalance() {
        try {
            double balance = bank.checkBalance(loggedAccount);
            outputArea.setText("Balance for " + loggedAccount + ": $" + balance);
        } catch (Exception e) {
            outputArea.setText("Error checking balance: " + e);
        }
    }

    private void transferFunds() {
        JTextField toAccountField = new JTextField();
        JTextField amountField = new JTextField();
        Object[] message = {
            "To Account:", toAccountField,
            "Amount:", amountField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Transfer Funds", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String toAccount = toAccountField.getText();
            double amount = Double.parseDouble(amountField.getText());

            try {
                String result = bank.transferFunds(loggedAccount, toAccount, amount);
                outputArea.setText(result);
            } catch (Exception e) {
                outputArea.setText("Error transferring funds: " + e);
            }
        }
    }

    private void topUp() {
        JTextField amountField = new JTextField();
        Object[] message = {
            "Amount:", amountField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Top Up", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            double amount = Double.parseDouble(amountField.getText());

            try {
                String result = bank.topup(loggedAccount, amount);
                outputArea.setText(result);
            } catch (Exception e) {
                outputArea.setText("Error topping up: " + e);
            }
        }
    }

    public static void main(String[] args) {
        new BankClientGUI();
    }
}
