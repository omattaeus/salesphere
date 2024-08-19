package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.product.Category;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.repositories.product.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@SpringJUnitConfig
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setup() {
        Category category = new Category();
        category.setCategoryEnum(CategoryEnum.MALE);

        categoryRepository.save(category);
    }

    @Test
    @DisplayName("Should find category by CategoryEnum")
    public void testFindByCategoryEnum() {

        CategoryEnum categoryEnum = CategoryEnum.MALE;

        Optional<Category> result = categoryRepository.findByCategoryEnum(categoryEnum);

        assertTrue(result.isPresent(), "Category should be present");
        assertEquals(categoryEnum, result.get().getCategoryEnum(), "CategoryEnum should match");
    }

    @Test
    @DisplayName("Should return empty for non-existing CategoryEnum")
    public void testFindByCategoryEnumNotFound() {

        CategoryEnum nonExistentCategoryEnum = CategoryEnum.FEMALE;

        Optional<Category> result = categoryRepository.findByCategoryEnum(nonExistentCategoryEnum);

        assertFalse(result.isPresent(), "Category should not be present");
    }
}
