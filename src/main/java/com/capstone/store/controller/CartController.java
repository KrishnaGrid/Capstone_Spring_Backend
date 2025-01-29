package com.capstone.store.controller;

import com.capstone.store.dto.*;
import com.capstone.store.service.CartService;
import com.capstone.store.service.UserService;
import com.capstone.store.constants.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    UserService userService;
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(HttpSession session, @RequestBody CartItemRequest request) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        cartService.addToCart(userId, request);
        return ResponseEntity.ok(Constants.ITEM_ADDED);
    }


    @GetMapping
    public ResponseEntity<List<CartItemResponse>> viewCart(HttpSession session) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(cartService.viewCart(userId));
    }


    @PutMapping("/modify")
    public ResponseEntity<String> modifyCartItem(HttpSession session, @RequestBody CartItemModifyRequest request) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        cartService.modifyCartItem(userId, request);
        return ResponseEntity.ok(Constants.CART_UPDATED);
    }


    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(HttpSession session, @RequestParam Long itemId) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(Constants.CART_ITEM_REMOVED);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        cartService.clearCart(userId);
        return ResponseEntity.ok(Constants.CART_CLEARED);
    }

}