import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class BankServer {
    public static void main(String[] args) {
        try {
            // Membuat instance BankImpl
            BankImpl bank = new BankImpl();

            // Membuat registry pada port default 1099
            LocateRegistry.createRegistry(1099);

            // Mendaftarkan objek remote ke RMI registry dengan nama "BankService"
            Naming.rebind("BankService", bank);

            System.out.println("Bank Server is ready.");
        } catch (Exception e) {
            System.out.println("Bank Server failed: " + e);
        }
    }
}
