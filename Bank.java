import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bank extends Remote {
    double checkBalance(String accountNumber) throws RemoteException;
    String transferFunds(String fromAccount, String toAccount, double amount) throws RemoteException;
    String topup(String accountNumber, double amount) throws RemoteException;
    boolean login(String username, int pin) throws RemoteException;
    
    // Get Account dan Username
    String getAccountForUser(String username) throws RemoteException; 
}
