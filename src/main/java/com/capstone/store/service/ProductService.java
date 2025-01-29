package com.capstone.store.service;


import com.capstone.store.Repository.ProductRepository;
import com.capstone.store.dto.ProductResponse;
import com.capstone.store.exception.InsufficientStockException;
import com.capstone.store.exception.ResourceNotFoundException;
import com.capstone.store.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();
        if(products.isEmpty()){
            throw new ResourceNotFoundException("No Products are Available");
        }
        return products.stream().
                map(product -> new ProductResponse(
                        product.getId(),
                        product.getTitle(),
                        product.getAvailable(),
                        product.getPrice()
                )).collect(Collectors.toList());
    }

    public Product getProductById(Long productId){
        return productRepository.findById(productId).
                orElseThrow(()-> new ResourceNotFoundException("No product available with ID:"+ productId));
    }

    public void validateStockAvailability(Long productId, int requestedQuantity){
        Product product = getProductById(productId);
        if(product.getAvailable() < requestedQuantity){
            throw new InsufficientStockException("Insufficient Stock for this given product:- "+product.getTitle());
        }
    }

    public void updateProductStock(Long productId, int quantity){
        Product product = getProductById(productId);
        product.setAvailable(product.getAvailable()-quantity);
        productRepository.save(product);
    }


}
