package com.commerceflow.category.controller;

import com.commerceflow.category.dto.CategoryRequest;
import com.commerceflow.category.dto.CategoryResponse;
import com.commerceflow.category.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE CATEGORY
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {
        return categoryService.createCategory(request);
    }

    // GET ALL CATEGORIES
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // GET CATEGORY BY ID
    @Operation(summary = "Get category by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category found successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category ID"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(
            @PathVariable
            @Min(value = 1, message = "Category ID must be at least 1")
            Long id
    ) {
        return categoryService.getCategoryById(id);
    }

    // UPDATE CATEGORY
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        return categoryService.updateCategory(id, request);
    }

    // DELETE CATEGORY
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(id);
    }
}