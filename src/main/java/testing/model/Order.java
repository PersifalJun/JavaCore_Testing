package testing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Order {
    private int id;
    private String productName;
    private int quantity;
    private double unitPrice;

    public double getTotalPrice() {
        return this.quantity * this.unitPrice;
    }
}
