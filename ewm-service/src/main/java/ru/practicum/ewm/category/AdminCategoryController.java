package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.exception.CategoryNameNotUniqueException;
import ru.practicum.ewm.exception.CategoryNotFoundException;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto create(@Valid @RequestBody NewCategoryDto categoryDto) throws CategoryNameNotUniqueException {
        return categoryService.create(categoryDto);
    }

    @PatchMapping
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto) throws CategoryNameNotUniqueException,
            CategoryNotFoundException {
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void delete(@PathVariable("catId") Long categoryId) throws CategoryNotFoundException {
        categoryService.delete(categoryId);
    }
}
