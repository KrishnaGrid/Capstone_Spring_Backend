package com.capstone.store;
import com.capstone.store.Repository.CartRepository;
import com.capstone.store.dto.CartItemModifyRequest;
import com.capstone.store.dto.CartItemRequest;
import com.capstone.store.dto.CartItemResponse;
import com.capstone.store.exception.BadRequestException;
import com.capstone.store.exception.ResourceNotFoundException;
import com.capstone.store.model.Cart;
import com.capstone.store.model.CartItem;
import com.capstone.store.model.Product;
import com.capstone.store.service.CartService;
import com.capstone.store.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private ProductService productService;
    @InjectMocks private CartService cartService;

    private Product product;
    private Cart cart;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setId(3L);
        product.setTitle("Test New Product");
        product.setPrice(4000);
        cart = new Cart(1L);
    }

    private void mockCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
    }

    private void mockProduct() {
        when(productService.getProductById(3L)).thenReturn(product);
    }

    @Test
    void addToCart_ThrowsExceptionTest() {
        CartItemRequest request = new CartItemRequest(3L, 0);
        assertThrows(BadRequestException.class, () -> cartService.addToCart(1L, request));
    }

    @Test
    void viewCart_ThrowException_CartDoesNotExist() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.viewCart(1L));
    }

    @Test
    void addItemToCart() {
        CartItemRequest request = new CartItemRequest(3L, 2);
        mockCart();
        mockProduct();

        cartService.addToCart(1L, request);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    void viewCartItems() {
        cart.getItems().add(new CartItem(cart, product, 2));
        mockCart();

        List<CartItemResponse> responses = cartService.viewCart(1L);

        assertEquals(1, responses.size());
        assertEquals(2, responses.get(0).getQuantity());
    }


    @Test
    void updateCartItem() {
        CartItemModifyRequest request = new CartItemModifyRequest(3L, 5);
        CartItem cartItem = new CartItem(cart, product, 2);
        cart.getItems().add(cartItem);
        mockCart();

        cartService.modifyCartItem(1L, request);

        assertEquals(5, cartItem.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void removeCartItem() {
        cart.getItems().add(new CartItem(cart, product, 2));
        mockCart();

        cartService.removeCartItem(1L, 3L);

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void clearCartItems() {
        cart.getItems().add(new CartItem(cart, product, 2));
        mockCart();

        cartService.clearCart(1L);

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }
}
