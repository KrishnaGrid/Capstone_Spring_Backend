package com.capstone.store;
import com.capstone.store.Repository.OrderRepository;
import com.capstone.store.dto.CartItemResponse;
import com.capstone.store.exception.BadRequestException;
import com.capstone.store.model.Order;
import com.capstone.store.model.OrderItem;
import com.capstone.store.model.Product;
import com.capstone.store.service.CartService;
import com.capstone.store.service.OrderService;
import com.capstone.store.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;


    private final Long userId=1L;
    private Product product;
    private CartItemResponse cartItem;

    @BeforeEach
    void setUp(){
        product = new Product();
        product.setId(3L);
        product.setTitle("Test Product");
        product.setPrice(4000);

        cartItem = new CartItemResponse(3L, "Test Product", 2,4000, 8000 );

    }

    @Test
    void processCheckout_Success(){
        List<CartItemResponse> cartItems = Arrays.asList(cartItem);
        when(cartService.viewCart(userId)).thenReturn(cartItems);
        when(productService.getProductById(cartItem.getProductId())).thenReturn(product);
        doNothing().when(productService).validateStockAvailability(cartItem.getProductId(),cartItem.getQuantity());
        doNothing().when(productService).updateProductStock(cartItem.getProductId(),cartItem.getQuantity());

        orderService.processCheckout(userId);

        verify(productService, times(1)).validateStockAvailability(cartItem.getProductId(),cartItem.getQuantity());
        verify(productService, times(1)).updateProductStock(cartItem.getProductId(),cartItem.getQuantity());

        verify(orderRepository,times(1)).save(any(Order.class));
    }

    @Test
    void processCheckout_ThrowsException_IfCartEmpty(){
        when(cartService.viewCart(userId)).thenReturn(List.of());
        BadRequestException exception = assertThrows(BadRequestException.class, () -> orderService.processCheckout(userId));
        assertEquals("Cart not found", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }


    @Test
    void createOrder_Success(){
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new Date());
        OrderItem orderItem = new OrderItem(cartItem.getProductId(), cartItem.getQuantity(), cartItem.getPricePerUnit());
        orderItem.setOrder(order);

        List<CartItemResponse> cartItems = Arrays.asList(cartItem);
        orderService.createOrder(userId, cartItems);
        verify(orderRepository, times(1)).save(any(Order.class));
    }






}
