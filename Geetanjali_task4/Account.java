import java.util.ArrayList;

public class Account {
    private String userId;
    private String pin;
    private double balance;
    private ArrayList<Transaction> transactions;
    
    public Account(String userId, String pin, double initialBalance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        
        if (initialBalance > 0) {
            addTransaction(initialBalance, "Deposit", "Initial Deposit");
        }
    }
    
    public String getUserId() {
        return userId;
    }
    
    public boolean validatePin(String pinInput) {
        return this.pin.equals(pinInput);
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void addTransaction(double amount, String type, String memo) {
        Transaction newTrans = new Transaction(amount, type, memo);
        this.transactions.add(newTrans);
    }
    
    public boolean withdraw(double amount, String memo) {
        if (amount > balance) {
            return false;
        }
        this.balance -= amount;
        addTransaction(amount, "Withdrawal", memo);
        return true;
    }
    
    public void deposit(double amount, String memo) {
        this.balance += amount;
        addTransaction(amount, "Deposit", memo);
    }
    
    public String getTransactionsJson() {
        StringBuilder sb = new StringBuilder("[");
        // reverse loop for newest first
        for (int i = transactions.size() - 1; i >= 0; i--) {
            sb.append(transactions.get(i).toJson());
            if (i != 0) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
