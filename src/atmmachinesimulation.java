import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

class ATM implements Serializable {
    private double balance;
    private int pin;
    private ArrayList<String> transactionHistory;

    public ATM(int pin) {
        this.balance = 5000.00; // Default balance
        this.pin = pin;
        this.transactionHistory = new ArrayList<>();
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add("Deposited: " + amount);
    }

    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        } else {
            balance -= amount;
            transactionHistory.add("Withdrew: " + amount);
            return true;
        }
    }

    public void changePin(int newPin) {
        this.pin = newPin;
        transactionHistory.add("PIN changed successfully");
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public boolean verifyPin(int enteredPin) {
        return this.pin == enteredPin;
    }
}

    class ATMSimulation {
    private static ATM userAccount;
    private static final String FILE_NAME = "atm_data.ser";

    public static void main(String[] args) {
        loadATMData();
        SwingUtilities.invokeLater(ATMSimulation::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("ATM Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 1));

        JLabel label = new JLabel("Enter PIN:", SwingConstants.CENTER);
        JPasswordField pinField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        frame.add(label);
        frame.add(pinField);
        frame.add(loginButton);

        loginButton.addActionListener(e -> {
            int enteredPin = Integer.parseInt(new String(pinField.getPassword()));
            if (userAccount.verifyPin(enteredPin)) {
                frame.dispose();
                showATMMenu();
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect PIN. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static void showATMMenu() {
        JFrame frame = new JFrame("ATM Menu");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 1));

        JButton balanceBtn = new JButton("Check Balance");
        JButton depositBtn = new JButton("Deposit Cash");
        JButton withdrawBtn = new JButton("Withdraw Cash");
        JButton changePinBtn = new JButton("Change PIN");
        JButton historyBtn = new JButton("Transaction History");

        balanceBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Current Balance: " + userAccount.getBalance()));

        depositBtn.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog("Enter deposit amount:");
            if (amountStr != null) {
                double amount = Double.parseDouble(amountStr);
                userAccount.deposit(amount);
                saveATMData();
                JOptionPane.showMessageDialog(frame, "Deposited Successfully!");
            }
        });

        withdrawBtn.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog("Enter withdrawal amount:");
            if (amountStr != null) {
                double amount = Double.parseDouble(amountStr);
                if (userAccount.withdraw(amount)) {
                    saveATMData();
                    JOptionPane.showMessageDialog(frame, "Withdrawal Successful!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Insufficient Funds!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        changePinBtn.addActionListener(e -> {
            String newPinStr = JOptionPane.showInputDialog("Enter new PIN:");
            if (newPinStr != null) {
                int newPin = Integer.parseInt(newPinStr);
                userAccount.changePin(newPin);
                saveATMData();
                JOptionPane.showMessageDialog(frame, "PIN Changed Successfully!");
            }
        });

        historyBtn.addActionListener(e -> {
            String history = String.join("\n", userAccount.getTransactionHistory());
            JOptionPane.showMessageDialog(frame, history.isEmpty() ? "No transactions yet." : history);
        });

        frame.add(balanceBtn);
        frame.add(depositBtn);
        frame.add(withdrawBtn);
        frame.add(changePinBtn);
        frame.add(historyBtn);
        frame.setVisible(true);
    }

    private static void saveATMData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(userAccount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadATMData() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                userAccount = (ATM) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            userAccount = new ATM(1234); // Default PIN
            saveATMData();
        }
    }
}
