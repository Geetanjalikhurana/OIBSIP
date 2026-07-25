import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Bank myBank = new Bank("Global Trust Bank");
        myBank.addAccount(new Account("user1", "1234", 1000.0));
        myBank.addAccount(new Account("user2", "5678", 500.0));
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new ATM(myBank));
        server.setExecutor(null); 
        server.start();
        
        System.out.println("==================================================");
        System.out.println("ATM Java Web Server started!");
        System.out.println("Open your web browser and go to: http://localhost:8080");
        System.out.println("Demo Accounts: (user1, 1234) | (user2, 5678)");
        System.out.println("Press Ctrl+C to stop the server.");
        System.out.println("==================================================");
    }
}
