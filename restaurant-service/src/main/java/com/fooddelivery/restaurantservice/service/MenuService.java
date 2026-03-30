package com.fooddelivery.restaurantservice.service;

import com.fooddelivery.restaurantservice.dto.MenuCategoryDto;
import com.fooddelivery.restaurantservice.dto.MenuItemDto;
import com.fooddelivery.restaurantservice.entity.MenuCategory;
import com.fooddelivery.restaurantservice.entity.MenuItem;
import com.fooddelivery.restaurantservice.entity.Restaurant;
import com.fooddelivery.restaurantservice.exception.ResourceNotFoundException;
import com.fooddelivery.restaurantservice.repository.MenuCategoryRepository;
import com.fooddelivery.restaurantservice.repository.MenuItemRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    
    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    
    // Menu Category operations
    public MenuCategoryDto createCategory(MenuCategoryDto categoryDto) {
        MenuCategory category = new MenuCategory();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setRestaurantId(categoryDto.getRestaurantId());
        category.setDisplayOrder(categoryDto.getDisplayOrder());
        
        MenuCategory savedCategory = menuCategoryRepository.save(category);
        return mapCategoryToDto(savedCategory);
    }
    
    public List<MenuCategoryDto> getCategoriesByRestaurant(Long restaurantId) {
        return menuCategoryRepository.findByRestaurantIdOrderByDisplayOrder(restaurantId).stream()
                .map(this::mapCategoryToDto)
                .collect(Collectors.toList());
    }
    
    public MenuCategoryDto updateCategory(Long id, MenuCategoryDto categoryDto) {
        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setDisplayOrder(categoryDto.getDisplayOrder());
        
        MenuCategory updatedCategory = menuCategoryRepository.save(category);
        return mapCategoryToDto(updatedCategory);
    }
    
    public void deleteCategory(Long id) {
        menuCategoryRepository.deleteById(id);
    }
    
    // Menu Item operations
    public MenuItemDto createMenuItem(MenuItemDto itemDto) {
        Restaurant restaurant = restaurantRepository.findById(itemDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        MenuItem menuItem = new MenuItem();
        updateMenuItemFromDto(menuItem, itemDto);
        menuItem.setRestaurant(restaurant);
        
        if (itemDto.getCategoryId() != null) {
            MenuCategory category = menuCategoryRepository.findById(itemDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            menuItem.setCategory(category);
        }
        
        MenuItem savedItem = menuItemRepository.save(menuItem);
        return mapMenuItemToDto(savedItem);
    }
    
    public MenuItemDto updateMenuItem(Long id, MenuItemDto itemDto) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        
        updateMenuItemFromDto(menuItem, itemDto);
        
        if (itemDto.getCategoryId() != null) {
            MenuCategory category = menuCategoryRepository.findById(itemDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            menuItem.setCategory(category);
        }
        
        MenuItem updatedItem = menuItemRepository.save(menuItem);
        return mapMenuItemToDto(updatedItem);
    }
    
    public MenuItemDto getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        return mapMenuItemToDto(menuItem);
    }
    
    public List<MenuItemDto> getMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapMenuItemToDto)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemDto> getAvailableMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId).stream()
                .map(this::mapMenuItemToDto)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemDto> getMenuItemsByCategory(Long categoryId) {
        return menuItemRepository.findByCategoryId(categoryId).stream()
                .map(this::mapMenuItemToDto)
                .collect(Collectors.toList());
    }
    
    public MenuItemDto toggleItemAvailability(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        
        menuItem.setAvailable(!menuItem.isAvailable());
        MenuItem updatedItem = menuItemRepository.save(menuItem);
        return mapMenuItemToDto(updatedItem);
    }
    
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
    
    private MenuItemDto mapMenuItemToDto(MenuItem menuItem) {
        return MenuItemDto.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .categoryId(menuItem.getCategory() != null ? menuItem.getCategory().getId() : null)
                .categoryName(menuItem.getCategory() != null ? menuItem.getCategory().getName() : null)
                .restaurantId(menuItem.getRestaurant().getId())
                .imageUrl(menuItem.getImageUrl())
                .foodType(menuItem.getFoodType())
                .isAvailable(menuItem.isAvailable())
                .preparationTime(menuItem.getPreparationTime())
                .discount(menuItem.getDiscount())
                .build();
    }
    
    private void updateMenuItemFromDto(MenuItem menuItem, MenuItemDto dto) {
        menuItem.setName(dto.getName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice());
        menuItem.setImageUrl(dto.getImageUrl());
        menuItem.setFoodType(dto.getFoodType());
        menuItem.setAvailable(dto.isAvailable());
        menuItem.setPreparationTime(dto.getPreparationTime());
        menuItem.setDiscount(dto.getDiscount());
    }
    
    private MenuCategoryDto mapCategoryToDto(MenuCategory category) {
        return MenuCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .restaurantId(category.getRestaurantId())
                .displayOrder(category.getDisplayOrder())
                .build();
    }
}
