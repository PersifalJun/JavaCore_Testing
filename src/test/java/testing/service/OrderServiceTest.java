package testing.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import testing.exceptions.EmptyOrderException;
import testing.exceptions.OrderSaveFailedException;
import testing.model.Order;
import testing.repository.OrderRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private static OrderRepository repositoryMock;
    private static OrderService service;

    @BeforeAll
    static void init() {
        repositoryMock = Mockito.mock(OrderRepository.class);
        service = new OrderService(repositoryMock);
    }

    @Test
    void shouldReturnOrderProcessedSuccessfully() {
        Order order = new Order(1, "Bottle", 10, 100.0);
        when(repositoryMock.saveOrder(order)).thenReturn(1);
        String message = service.processOrder(order);
        assertEquals("Order processed successfully", message);
        verify(repositoryMock, times(1)).saveOrder(order);
    }

    @Test
    void shouldThrowExceptionWithMessage_OrderProcessingFailed() {
        Order order = new Order(1, "Bottle", 10, 100.0);
        when(repositoryMock.saveOrder(order)).thenThrow(new OrderSaveFailedException("Order processing failed"));
        String result = service.processOrder(order);
        assertEquals("Order processing failed", result);
        verify(repositoryMock, times(1)).saveOrder(order);
    }

    @Test
    void shouldThrowExceptionWithMessage_OrderIsNull() {
        Order order = null;
        when(repositoryMock.saveOrder(order)).thenThrow(new NullPointerException("Order is null!"));
        Exception exception = assertThrows(NullPointerException.class, () -> service.processOrder(order));
        assertEquals("Order is null!", exception.getMessage());
        verify(repositoryMock, times(1)).saveOrder(order);
    }

    @Test
    void shouldReturn300() {
        Order order = new Order(2, "Bottle", 3, 100.0);
        when(repositoryMock.getOrderById(2)).thenReturn(Optional.of(order));
        double result = service.calculateTotal(2);
        assertEquals(300.0, result);
        verify(repositoryMock, times(1)).getOrderById(2);
    }

    @Test
    void shouldThrowExceptionWithMessage_NoOrderToCalculateTotalPrice() {
        Order order = new Order(2, "Pen", 2, 50.0);
        when(repositoryMock.getOrderById(2)).thenThrow(new EmptyOrderException("No order to calculate total price"));
        Exception exception = assertThrows(EmptyOrderException.class, () -> service.calculateTotal(2));
        assertEquals("No order to calculate total price", exception.getMessage());
        verify(repositoryMock, times(1)).getOrderById(2);
    }

    @Test
    void shouldReturnZeroIfQuantity0AndUnitPrice0() {
        Order order = new Order(2, "Pen", 0, 0);
        when(repositoryMock.getOrderById(2)).thenReturn(Optional.of(order));
        double result = service.calculateTotal(2);
        assertEquals(0.0, result);
        verify(repositoryMock, times(1)).getOrderById(2);
    }
}