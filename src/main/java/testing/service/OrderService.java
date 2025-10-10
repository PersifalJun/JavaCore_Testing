package testing.service;

import lombok.RequiredArgsConstructor;
import testing.exceptions.EmptyOrderException;
import testing.exceptions.OrderSaveFailedException;
import testing.model.Order;
import testing.repository.OrderRepository;

@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public String processOrder(Order order) {
        try {
            repository.saveOrder(order);
            return "Order processed successfully";
        } catch (OrderSaveFailedException ex) {
            return "Order processing failed";

        }

    }

    public double calculateTotal(int id) {

        try {
            return repository.getOrderById(id).orElseThrow().getTotalPrice();
        } catch (RuntimeException ex) {
            throw new EmptyOrderException("No order to calculate total price");
        }

    }

}
