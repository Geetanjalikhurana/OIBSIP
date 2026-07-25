import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ATM implements HttpHandler {
    private Bank bank;
    private Map<String, Account> sessions = new HashMap<>();
    
    public ATM(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        if (method.equals("GET") && path.equals("/")) {
            serveStaticFile(exchange, "index.html", "text/html");
        } else if (method.equals("POST") && path.equals("/api/login")) {
            handleLogin(exchange);
        } else if (method.equals("GET") && path.equals("/api/data")) {
            handleGetData(exchange);
        } else if (method.equals("POST") && path.equals("/api/withdraw")) {
            handleWithdraw(exchange);
        } else if (method.equals("POST") && path.equals("/api/deposit")) {
            handleDeposit(exchange);
        } else if (method.equals("POST") && path.equals("/api/transfer")) {
            handleTransfer(exchange);
        } else {
            String response = "Not Found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    private void serveStaticFile(HttpExchange exchange, String filename, String contentType) throws IOException {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filename));
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        } catch (Exception e) {
            String res = "Error loading UI. Make sure index.html is in the directory.";
            exchange.sendResponseHeaders(500, res.length());
            OutputStream os = exchange.getResponseBody();
            os.write(res.getBytes());
            os.close();
        }
    }
    
    private Map<String, String> parseForm(String body) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
    
    private String readBody(HttpExchange exchange) throws IOException {
        java.util.Scanner s = new java.util.Scanner(exchange.getRequestBody()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, json.length());
        OutputStream os = exchange.getResponseBody();
        os.write(json.getBytes());
        os.close();
    }
    
    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, String> params = parseForm(body);
        String userId = params.get("userId");
        String pin = params.get("pin");
        
        Account acc = bank.getAccount(userId);
        if (acc != null && acc.validatePin(pin)) {
            String token = java.util.UUID.randomUUID().toString();
            sessions.put(token, acc);
            sendJson(exchange, 200, "{\"success\":true, \"token\":\"" + token + "\"}");
        } else {
            sendJson(exchange, 401, "{\"success\":false, \"message\":\"Invalid credentials\"}");
        }
    }
    
    private Account getSessionAccount(HttpExchange exchange) {
        String token = exchange.getRequestHeaders().getFirst("Authorization");
        if (token != null) {
            return sessions.get(token);
        }
        return null;
    }
    
    private void handleGetData(HttpExchange exchange) throws IOException {
        Account acc = getSessionAccount(exchange);
        if (acc == null) {
            sendJson(exchange, 401, "{\"error\":\"Unauthorized\"}");
            return;
        }
        String json = String.format("{\"userId\":\"%s\", \"balance\":%.2f, \"transactions\":%s}", 
            acc.getUserId(), acc.getBalance(), acc.getTransactionsJson());
        sendJson(exchange, 200, json);
    }
    
    private void handleWithdraw(HttpExchange exchange) throws IOException {
        Account acc = getSessionAccount(exchange);
        if (acc == null) { sendJson(exchange, 401, "{\"error\":\"Unauthorized\"}"); return; }
        
        String body = readBody(exchange);
        Map<String, String> params = parseForm(body);
        double amount = Double.parseDouble(params.get("amount"));
        
        if (amount <= 0) { sendJson(exchange, 400, "{\"success\":false, \"message\":\"Invalid amount\"}"); return; }
        
        if (acc.withdraw(amount, "ATM Withdrawal")) {
            sendJson(exchange, 200, "{\"success\":true}");
        } else {
            sendJson(exchange, 400, "{\"success\":false, \"message\":\"Insufficient funds\"}");
        }
    }
    
    private void handleDeposit(HttpExchange exchange) throws IOException {
        Account acc = getSessionAccount(exchange);
        if (acc == null) { sendJson(exchange, 401, "{\"error\":\"Unauthorized\"}"); return; }
        
        String body = readBody(exchange);
        Map<String, String> params = parseForm(body);
        double amount = Double.parseDouble(params.get("amount"));
        
        if (amount <= 0) { sendJson(exchange, 400, "{\"success\":false, \"message\":\"Invalid amount\"}"); return; }
        
        acc.deposit(amount, "ATM Deposit");
        sendJson(exchange, 200, "{\"success\":true}");
    }
    
    private void handleTransfer(HttpExchange exchange) throws IOException {
        Account acc = getSessionAccount(exchange);
        if (acc == null) { sendJson(exchange, 401, "{\"error\":\"Unauthorized\"}"); return; }
        
        String body = readBody(exchange);
        Map<String, String> params = parseForm(body);
        double amount = Double.parseDouble(params.get("amount"));
        String recipientId = params.get("recipientId");
        
        if (amount <= 0) { sendJson(exchange, 400, "{\"success\":false, \"message\":\"Invalid amount\"}"); return; }
        if (recipientId.equals(acc.getUserId())) { sendJson(exchange, 400, "{\"success\":false, \"message\":\"Cannot transfer to yourself\"}"); return; }
        
        Account rec = bank.getAccount(recipientId);
        if (rec == null) { sendJson(exchange, 400, "{\"success\":false, \"message\":\"Recipient not found\"}"); return; }
        
        if (acc.withdraw(amount, "Transfer to " + recipientId)) {
            rec.deposit(amount, "Transfer from " + acc.getUserId());
            sendJson(exchange, 200, "{\"success\":true}");
        } else {
            sendJson(exchange, 400, "{\"success\":false, \"message\":\"Insufficient funds\"}");
        }
    }
}
