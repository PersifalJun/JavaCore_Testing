package testing.service;

import lombok.RequiredArgsConstructor;
import testing.exceptions.EmptyOrderException;
import testing.exceptions.OrderSaveFailedException;
import testing.model.Order;
import testing.repository.OrderRepository;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public String processOrder(Order order) {
        if (isNull(order)) {
            throw new EmptyOrderException("Order is null!");
        }
        if (isNull(order.getProductName())) {
            throw new IllegalArgumentException("Product name is null!");
        }
        if (isNull(order.getUnitPrice())) {
            throw new IllegalArgumentException("Price is null!");
        }
        try {
            repository.saveOrder(order);
            return "Order processed successfully";
        }
        catch (Exception ex) {
            throw new OrderSaveFailedException("Order processing failed");
        }
    }

    public double calculateTotal(int id) {
        Order order = repository.getOrderById(id).orElseThrow(() -> new EmptyOrderException("No order to calculate total price"));
        return order.getTotalPrice();
    }
}
