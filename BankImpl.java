import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;

public class BankImpl extends UnicastRemoteObject implements Bank {

    private HashMap<String, Double> accounts;
    private HashMap<String, Integer> users; // Simulasi data username dan PIN
    private HashMap<String, String> userAccounts; // Map user to their account

    public BankImpl() throws RemoteException {
        super();
        accounts = new HashMap<>();
        users = new HashMap<>();
        userAccounts = new HashMap<>(); // Initialize the mapping
        
        // Tambahkan data akun untuk simulasi
        accounts.put("ACC123", 5000.00);
        accounts.put("ACC456", 3000.00);
        accounts.put("ACC789", 7000.00);

        // Tambahkan data pengguna untuk simulasi login
        users.put("user123", 1234); // username: user123, PIN: 1234
        users.put("user456", 4567); // username: user456, PIN: 4567
        
        // Map users to accounts
        userAccounts.put("user123", "ACC123"); // user123 can access ACC123
        userAccounts.put("user456", "ACC456"); // user456 can access ACC456
        // ACC789 sebagai Admin
        users.put("admin", 1111); // username: admin, PIN: 1111
        userAccounts.put("admin", "ACC789"); // admin can access ACC789
    }

    @Override
    public double checkBalance(String accountNumber) throws RemoteException {
        if (accounts.containsKey(accountNumber)) {
            return accounts.get(accountNumber);
        } else {
            throw new RemoteException("Account not found.");
        }
    }

    @Override
    public String transferFunds(String fromAccount, String toAccount, double amount) throws RemoteException {
        if (!accounts.containsKey(fromAccount) || !accounts.containsKey(toAccount)) {
            return "One or both accounts not found.";
        }

        if (accounts.get(fromAccount) < amount) {
            return "Insufficient funds in the account.";
        }

        accounts.put(fromAccount, accounts.get(fromAccount) - amount);
        accounts.put(toAccount, accounts.get(toAccount) + amount);
        return "Transfer of $" + amount + " from " + fromAccount + " to " + toAccount + " successful.";
    }

    @Override
    public String topup(String accountNumber, double amount) throws RemoteException {
        if (accounts.containsKey(accountNumber)) {
            accounts.put(accountNumber, accounts.get(accountNumber) + amount);
            return "Top-up of $" + amount + " to " + accountNumber + " successful. New Balance: $" + accounts.get(accountNumber);
        } else {
            return "Account not found.";
        }
    }

    // Implementasi method login
    @Override
    public boolean login(String username, int pin) throws RemoteException {
        return users.containsKey(username) && users.get(username) == pin;
    }

    // Method untuk mendapatkan account number berdasarkan username
    @Override
    public String getAccountForUser(String username) throws RemoteException {
        return userAccounts.get(username);
    }
}
