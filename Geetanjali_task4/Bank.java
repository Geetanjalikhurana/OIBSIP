import java.util.ArrayList;

public class Bank {
    private String name;
    private ArrayList<Account> accounts;
    
    public Bank(String name) {
        this.name = name;
        this.accounts = new ArrayList<>();
    }
    
    public void addAccount(Account newAccount) {
        this.accounts.add(newAccount);
    }
    
    public Account getAccount(String userId) {
        for (Account a : accounts) {
            if (a.getUserId().equals(userId)) {
                return a;
            }
        }
        return null;
    }
}
