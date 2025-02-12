package com.capstone.store;
import com.capstone.store.Repository.ProductRepository;
import com.capstone.store.dto.ProductResponse;
import com.capstone.store.exception.InsufficientStockException;
import com.capstone.store.exception.ResourceNotFoundException;
import com.capstone.store.model.Product;
import com.capstone.store.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;







@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1, product2;

    @BeforeEach
    void setUp(){
        product1 = new Product(1L,"Laptop",10, 50000);
        product2 = new Product(2L, "Phone", 10, 3000);

    }

    @Test
    void getAllProducts_ReturnsProd_If_Exists(){
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        List<ProductResponse> products = productService.getAllProducts();

        assertEquals("Laptop", products.get(0).getTitle());
        assertEquals("Phone", products.get(1).getTitle());
        verify(productRepository, times(1)).findAll();

    }

    @Test
    void getAllProducts_Exception_IfNoProductExists(){
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, productService::getAllProducts);
    }

    @Test
    void getProductById_Returns_IfExists(){
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals("Laptop",foundProduct.getTitle());
        verify(productRepository,times(1)).findById(1L);
    }


    @Test
    void validateStockAvaialability_ThrowsException_IfNotAvailable(){
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        assertThrows(InsufficientStockException.class,()-> productService.validateStockAvailability(1L,15));
    }

    @Test
    void updateProductStock_ReduceStock_IfSufficientStockExists(){
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        productService.updateProductStock(1L,3);
        assertEquals(7,product1.getAvailable());
        verify(productRepository,times(1)).save(product1);
    }

}
