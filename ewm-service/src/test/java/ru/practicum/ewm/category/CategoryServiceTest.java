package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.exception.CategoryNameNotUniqueException;
import ru.practicum.ewm.exception.CategoryNotFoundException;

import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    CategoryService categoryService;

    CategoryMapper categoryMapper = new CategoryMapperImpl();

    private MockitoSession session;

    private static  Category category = new Category(1L, "test");

    private static NewCategoryDto newCategoryDto = new NewCategoryDto("test");
    private static  CategoryDto categoryDto = new CategoryDto(1L, "test");

    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        categoryService = new CategoryServiceImpl(categoryRepository, categoryMapper);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void create() throws CategoryNameNotUniqueException {

        Mockito.when(categoryRepository.save(Mockito.any(Category.class)))
                .thenReturn(category);

        Assertions.assertEquals(categoryDto, categoryService.create(newCategoryDto));

        Mockito.verify(categoryRepository, Mockito.times(1))
                .save(Mockito.any(Category.class));
    }

    @Test
    void createCategoryNameNotUniqueException() {

        Mockito.when(categoryRepository.save(Mockito.any(Category.class)))
                .thenThrow(new DataIntegrityViolationException(""));

        Assertions.assertThrows(CategoryNameNotUniqueException.class, () -> categoryService.create(newCategoryDto));

        Mockito.verify(categoryRepository, Mockito.times(1))
                .save(Mockito.any(Category.class));
    }

    @Test
    void update() throws CategoryNameNotUniqueException, CategoryNotFoundException {
        Mockito.when(categoryRepository.save(Mockito.any(Category.class)))
                .thenReturn(category);

        Mockito.when(categoryRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Assertions.assertEquals(categoryDto, categoryService.update(categoryDto));

        Mockito.verify(categoryRepository, Mockito.times(1))
                .save(Mockito.any(Category.class));
    }

    @Test
    void delete() {
        Mockito.when(categoryRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Assertions.assertDoesNotThrow(() -> categoryService.delete(1L));


        Mockito.verify(categoryRepository, Mockito.times(1))
                .deleteById(Mockito.any(Long.class));
    }

    @Test
    void deleteCategoryNotFoundException() {
        Mockito.when(categoryRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(false);

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(1L));

        Mockito.verify(categoryRepository, Mockito.times(0))
                .deleteById(Mockito.any(Long.class));
    }
}