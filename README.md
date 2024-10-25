# RMI

## I. Pendahuluan
Remote Method Invocation (RMI) adalah sebuah mekanisme dalam Java yang memungkinkan objek untuk memanggil metode yang ada pada objek lain yang berada pada JVM yang berbeda, baik di komputer yang sama maupun di komputer yang berbeda. RMI memfasilitasi komunikasi antara objek yang terdistribusi dalam jaringan, yang sangat berguna untuk aplikasi berbasis server-klien seperti sistem perbankan.

## II. Arsitektur Sistem
Sistem perbankan ini dibangun menggunakan arsitektur client-server, di mana server mengelola data akun dan transaksi, sementara klien menyediakan antarmuka pengguna untuk berinteraksi dengan sistem. Berikut adalah komponen utama dari sistem ini:

Antarmuka Bank (Bank.java): Mendefinisikan metode yang dapat dipanggil oleh klien.
Implementasi Bank (BankImpl.java): Mengimplementasikan logika bisnis untuk metode yang didefinisikan dalam antarmuka
Klien Bank GUI (BankClientGUI.java): Menyediakan antarmuka untuk melakukan transaksi.
Server Bank (BankServer.java): Mendaftarkan objek remote di registry RMI.

## III. Penjelasan Kode

**A. Antarmuka Bank (Bank.java)**
```
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bank extends Remote {
    double checkBalance(String accountNumber) throws RemoteException;
    String transferFunds(String fromAccount, String toAccount, double amount) throws RemoteException;
    String topup(String accountNumber, double amount) throws RemoteException;
    boolean login(String username, int pin) throws RemoteException;
    String getAccountForUser(String username) throws RemoteException; 
}
```

Antarmuka Bank mendefinisikan metode yang dapat dipanggil dari klien. Setiap metode melempar RemoteException untuk menangani masalah yang mungkin terjadi selama komunikasi jarak jauh.

**B. Implementasi Bank (BankImpl.java)**
```
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;

public class BankImpl extends UnicastRemoteObject implements Bank {
    // Data akun dan pengguna
    private HashMap<String, Double> accounts;
    private HashMap<String, Integer> users; 
    private HashMap<String, String> userAccounts;

    public BankImpl() throws RemoteException {
        super();
        accounts = new HashMap<>();
        users = new HashMap<>();
        userAccounts = new HashMap<>();

        // Menambahkan data akun
        accounts.put("ACC123", 5000.00);
        accounts.put("ACC456", 3000.00);
        accounts.put("ACC789", 7000.00);

        // Menambahkan pengguna untuk login
        users.put("user123", 1234);
        users.put("user456", 4567);
        userAccounts.put("user123", "ACC123");
        userAccounts.put("user456", "ACC456");
        users.put("admin", 1111);
        userAccounts.put("admin", "ACC789");
    }

    // Implementasi metode
    @Override
    public double checkBalance(String accountNumber) throws RemoteException { ... }
    @Override
    public String transferFunds(String fromAccount, String toAccount, double amount) throws RemoteException { ... }
    @Override
    public String topup(String accountNumber, double amount) throws RemoteException { ... }
    @Override
    public boolean login(String username, int pin) throws RemoteException { ... }
    @Override
    public String getAccountForUser(String username) throws RemoteException { ... }
}
```
Kelas BankImpl mengimplementasikan antarmuka Bank. Data akun, pengguna, dan hubungan antara pengguna dan akun disimpan dalam struktur data HashMap. Metode yang didefinisikan dalam antarmuka diimplementasikan di sini, menangani logika bisnis untuk memeriksa saldo, mentransfer dana, mengisi ulang, dan login.

**C. Klien Bank GUI (BankClientGUI.java)**
```
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

public class BankClientGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField pinField;
    private JTextArea outputArea;
    private Bank bank;
    private String loggedAccount;

    public BankClientGUI() {
        setTitle("Bank Unhan");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel login
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

        // Area output
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

    private void showMenu() { ... }

    private void checkBalance() { ... }
    private void transferFunds() { ... }
    private void topUp() { ... }

    public static void main(String[] args) {
        new BankClientGUI();
    }
}
```
Kelas BankClientGUI menyediakan antarmuka pengguna grafis untuk aplikasi perbankan. Pengguna dapat login dengan memasukkan username dan PIN. Setelah login berhasil, pengguna dapat memeriksa saldo, mentransfer dana, atau mengisi ulang saldo.

**D. Server Bank (BankServer.java)**
```
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class BankServer {
    public static void main(String[] args) {
        try {
            // Membuat instance BankImpl
            BankImpl bank = new BankImpl();
            // Mendaftarkan objek remote ke RMI registry dengan nama "BankService"
            LocateRegistry.createRegistry(1099);
            Naming.rebind("BankService", bank);
            System.out.println("Bank Server is ready.");
        } catch (Exception e) {
            System.out.println("Bank Server failed: " + e);
        }
    }
}
```
Kelas BankServer bertanggung jawab untuk membuat instance dari BankImpl dan mendaftarkan objek remote ke registry RMI pada port default 1099. Klien dapat menggunakan nama "BankService" untuk mencari dan mengakses layanan bank.

## IV. Proses Kompilasi dan Menjalankan Aplikasi RMI
**A. Persiapan Environment**
Pastikan Java Terinstal: Pastikan Anda memiliki JDK (Java Development Kit) terinstal di komputer Anda. Anda dapat memeriksanya dengan menjalankan perintah berikut di terminal atau command prompt:
```
java -version
```
Atur Variabel Lingkungan: Jika belum diatur, pastikan untuk menambahkan JAVA_HOME dan PATH ke variabel lingkungan Anda sehingga Anda dapat menjalankan perintah javac dan java dari mana saja.

**B. Struktur Proyek**
Pastikan Anda memiliki struktur folder yang jelas untuk menyimpan file .java Anda. Misalnya:
```
/Bank Unhan
  ├── Bank.java
  ├── BankImpl.java
  ├── BankClientGUI.java
  ├── BankServer.java
```
**C. Kompilasi Kode**
Buka Terminal atau Command Prompt: Arahkan ke direktori tempat Anda menyimpan file Java menggunakan perintah cd.
```
cd path/Bank Unhan
```
Kompilasi Semua File Java: Jalankan perintah berikut untuk mengompilasi semua file .java. Ini akan menghasilkan file .class yang sesuai untuk setiap file .java.
```
javac Bank.java BankImpl.java BankServer.java BankClientGUI.java
```
**D. Menjalankan Server**
Jalankan Server: Di jendela terminal baru (biarkan RMI Registry tetap terbuka), arahkan ke direktori yang sama dan jalankan server dengan perintah berikut:
```
java BankServer
```
Jika semuanya berjalan dengan baik, Anda akan melihat pesan "Bank Server is ready." di terminal.
**E. Menjalankan Klien**
Jalankan Klien: Di jendela terminal baru, arahkan ke direktori yang sama dan jalankan klien dengan perintah berikut:
```
java BankClientGUI
```
Antarmuka pengguna grafis (GUI) untuk klien bank akan terbuka, memungkinkan Anda untuk melakukan login dan transaksi.

**G. Menguji Aplikasi**
Login: Masukkan username dan PIN yang telah Anda tetapkan dalam BankImpl.java (contoh: user123 dengan PIN 1234).
Lakukan Transaksi: Setelah berhasil login, Anda dapat mencoba memeriksa saldo, mentransfer dana, atau mengisi ulang saldo.
untuk username dan pin dapat diatur melalui BankImpl.java

## V. Kesimpulan
Sistem perbankan yang dibangun menggunakan RMI dalam Java menyediakan antarmuka yang sederhana dan efisien untuk melakukan transaksi perbankan secara jarak jauh. Dengan memisahkan logika bisnis (server) dan antarmuka pengguna (klien), sistem ini mendukung pengembangan aplikasi terdistribusi yang lebih terstruktur dan mudah dikelola. RMI memungkinkan komunikasi yang efektif antara klien dan server, menjadikannya pilihan yang tepat untuk aplikasi berbasis jaringan seperti sistem perbankan ini.



