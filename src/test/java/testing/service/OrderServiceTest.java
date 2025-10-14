package testing.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import testing.exceptions.EmptyOrderException;
import testing.exceptions.OrderSaveFailedException;
import testing.model.Order;
import testing.repository.OrderRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private OrderRepository repositoryMock;
    private OrderService service;

    @BeforeEach
    void init() {
        repositoryMock = Mockito.mock(OrderRepository.class);
        service = new OrderService(repositoryMock);
    }

    //Tests for processOrder()
    @Test
    void processOrder_WithValidOrder_ShouldProcessSuccessfully() {
        Order order = new Order(1, "Bottle", 10, 100.0);
        when(repositoryMock.saveOrder(order)).thenReturn(1);
        String message = service.processOrder(order);
        assertEquals("Order processed successfully", message);
        verify(repositoryMock, times(1)).saveOrder(order);
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void processOrder_WithValidOrder_ShouldReturnOrderProcessingFailed() {
        Order order = new Order(1, "Bottle", 10, 100.0);
        OrderSaveFailedException exception = assertThrows( OrderSaveFailedException.class, () -> service.processOrder(order) );
        String errorMessage = "Order processing failed";
        assertEquals(errorMessage, exception.getMessage());
        verify(repositoryMock).saveOrder(order);
    }

    @Test
    void shouldThrowExceptionWithMessage_OrderIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.processOrder(null));
        assertEquals("Order is null!", exception.getMessage());
        verifyNoInteractions(repositoryMock);
    }

    //Tests for calculateTotal()
    @Test
    void calculateTotal_WithQuantity3AndPrice100_ShouldReturn300() {
        Order order = new Order(2, "Bottle", 3, 100.0);
        when(repositoryMock.getOrderById(2)).thenReturn(Optional.of(order));
        double result = service.calculateTotal(2);
        double expectedResult = 300.0;
        assertEquals(expectedResult, result, 1e-9);
        verify(repositoryMock, times(1)).getOrderById(2);
    }

    @Test
    void shouldThrowExceptionWithMessage_NoOrderToCalculateTotalPrice() {
        when(repositoryMock.getOrderById(2)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EmptyOrderException.class, () -> service.calculateTotal(2));
        String errorMessage = "No order to calculate total price";
        assertEquals("errorMessage", exception.getMessage());
        verify(repositoryMock).getOrderById(2);
    }

    @Test
    void calculateTotal_WhenQuantityAndPriceAreZero_ShouldReturnZero() {
        Order order = new Order(2, "Pen", 0, 0);
        when(repositoryMock.getOrderById(2)).thenReturn(Optional.of(order));
        double result = service.calculateTotal(2);
        double expectedResult = 0.0;
        assertEquals(expectedResult , result, 1e-9);
        verify(repositoryMock).getOrderById(2);
        verifyNoMoreInteractions();
    }

    //инициализацию можно вынести в отдельный метод, потому что у тебя уже идут дубли строк, а это DRY).
    // либо на сильное будущее (если заинтересует), можно вот так через параметризованные тесты
    // Тест на успешное выполнение
    @ParameterizedTest
    @MethodSource("validOrdersProvider")
    void processOrder_WithValidOrder_ShouldProcessSuccessfully(Order order) {
        //Arrange
        when(repositoryMock.saveOrder(order)).thenReturn(order.getId());
        // Act & Assert
        assertDoesNotThrow(() -> service.processOrder(order));
        verify(repositoryMock).saveOrder(order);
    }
    // Тест на исключения
    @ParameterizedTest @MethodSource("invalidOrdersProvider")
    void processOrder_WithInvalidOrder_ShouldThrowException( Order order, Class<? extends Exception> expectedException, String expectedMessage ) {
        // Act & Assert
        Exception exception = assertThrows(expectedException, () -> service.processOrder(order));
        assertEquals(expectedMessage, exception.getMessage());
    }
    private static Stream validOrdersProvider() {
        return Stream.of( Arguments.of(new Order(1, "Bottle", 10, 100.0)),
                Arguments.of(new Order(2, "Pen", 1, 50.0)) );
    }
    private static Stream invalidOrdersProvider() {
        return Stream.of( Arguments.of( null, IllegalArgumentException.class, "Order cannot be null" ),
                Arguments.of( new Order(3, null, 1, 10.0),
                        IllegalArgumentException.class, "Product name cannot be null or empty" ) );
    }

}