package com.fooddelivery.restaurantservice.controller;

import com.fooddelivery.restaurantservice.dto.MenuCategoryDto;
import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Tag(name = "Menu Management", description = "Menu management APIs")
public class MenuController {
    
    private final MenuService menuService;
    
    // Category endpoints
    @PostMapping("/categories")
    @Operation(summary = "Create menu category")
    public ResponseEntity<MenuCategoryDto> createCategory(@Valid @RequestBody MenuCategoryDto categoryDto) {
        MenuCategoryDto createdCategory = menuService.createCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }
    
    @GetMapping("/categories/restaurant/{restaurantId}")
    @Operation(summary = "Get categories by restaurant")
    public ResponseEntity<List<MenuCategoryDto>> getCategoriesByRestaurant(@PathVariable Long restaurantId) {
        List<MenuCategoryDto> categories = menuService.getCategoriesByRestaurant(restaurantId);
        return ResponseEntity.ok(categories);
    }
    
    @PutMapping("/categories/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<MenuCategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody MenuCategoryDto categoryDto) {
        MenuCategoryDto updatedCategory = menuService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }
    
    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        menuService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    // Menu item endpoints
    @PostMapping("/items")
    @Operation(summary = "Create menu item")
    public ResponseEntity<MenuItemDto> createMenuItem(@Valid @RequestBody MenuItemDto itemDto) {
        MenuItemDto createdItem = menuService.createMenuItem(itemDto);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }
    
    @PutMapping("/items/{id}")
    @Operation(summary = "Update menu item")
    public ResponseEntity<MenuItemDto> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemDto itemDto) {
        MenuItemDto updatedItem = menuService.updateMenuItem(id, itemDto);
        return ResponseEntity.ok(updatedItem);
    }
    
    @GetMapping("/items/{id}")
    @Operation(summary = "Get menu item by ID")
    public ResponseEntity<MenuItemDto> getMenuItemById(@PathVariable Long id) {
        MenuItemDto item = menuService.getMenuItemById(id);
        return ResponseEntity.ok(item);
    }
    
    @GetMapping("/items/restaurant/{restaurantId}")
    @Operation(summary = "Get menu items by restaurant")
    public ResponseEntity<List<MenuItemDto>> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        List<MenuItemDto> items = menuService.getMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/items/restaurant/{restaurantId}/available")
    @Operation(summary = "Get available menu items")
    public ResponseEntity<List<MenuItemDto>> getAvailableMenuItems(@PathVariable Long restaurantId) {
        List<MenuItemDto> items = menuService.getAvailableMenuItems(restaurantId);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/items/category/{categoryId}")
    @Operation(summary = "Get menu items by category")
    public ResponseEntity<List<MenuItemDto>> getMenuItemsByCategory(@PathVariable Long categoryId) {
        List<MenuItemDto> items = menuService.getMenuItemsByCategory(categoryId);
        return ResponseEntity.ok(items);
    }
    
    @PatchMapping("/items/{id}/availability")
    @Operation(summary = "Toggle item availability")
    public ResponseEntity<MenuItemDto> toggleItemAvailability(@PathVariable Long id) {
        MenuItemDto updatedItem = menuService.toggleItemAvailability(id);
        return ResponseEntity.ok(updatedItem);
    }
    
    @DeleteMapping("/items/{id}")
    @Operation(summary = "Delete menu item")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
