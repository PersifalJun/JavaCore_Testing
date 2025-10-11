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
            throw new IllegalArgumentException("Order is null!");
        }
        try {
            repository.saveOrder(order);
            return "Order processed successfully";
        }
        catch (OrderSaveFailedException ex) {
            return "Order processing failed";
        }
    }

    public double calculateTotal(int id) {
        Order order = repository.getOrderById(id).orElseThrow(() -> new EmptyOrderException("No order to calculate total price"));
        return order.getTotalPrice();
    }
}
