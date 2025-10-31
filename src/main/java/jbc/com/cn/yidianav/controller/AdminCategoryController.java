package jbc.com.cn.yidianav.controller;

import jbc.com.cn.yidianav.dto.ApiResponse;
import jbc.com.cn.yidianav.dto.CategoryDTO;
import jbc.com.cn.yidianav.entity.Category;
import jbc.com.cn.yidianav.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryDTO>> getCategoryTree() {
        return ApiResponse.success(categoryService.getTreeStructure());
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ApiResponse::success)
                .orElse(ApiResponse.error("Category not found"));
    }

    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ApiResponse.success("Category created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        if (updated != null) {
            return ApiResponse.success("Category updated successfully", updated);
        }
        return ApiResponse.error("Category not found");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        if (categoryService.deleteCategory(id)) {
            return ApiResponse.success("Category deleted successfully");
        }
        return ApiResponse.error("Category not found");
    }
}
