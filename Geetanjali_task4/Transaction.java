import java.util.Date;
import java.text.SimpleDateFormat;

public class Transaction {
    private double amount;
    private Date timestamp;
    private String memo;
    private String type;
    
    public Transaction(double amount, String type, String memo) {
        this.amount = amount;
        this.type = type;
        this.memo = memo;
        this.timestamp = new Date();
    }
    
    public String toJson() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
        String dateStr = sdf.format(this.timestamp);
        return String.format("{\"amount\":%.2f, \"type\":\"%s\", \"memo\":\"%s\", \"date\":\"%s\"}",
                this.amount, this.type, this.memo, dateStr);
    }
}
