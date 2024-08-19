package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.product.Availability;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.repositories.product.AvailabilityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AvailabilityRepositoryTest {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Test
    @DisplayName("Should find availability by AvailabilityEnum")
    void shouldFindAvailabilityByAvailabilityEnum() {
        // Given
        Availability availability = new Availability();
        availability.setAvailability(AvailabilityEnum.AVAILABLE);
        availabilityRepository.save(availability);

        // When
        Optional<Availability> foundAvailability = availabilityRepository.findByAvailability(AvailabilityEnum.AVAILABLE);

        // Then
        assertThat(foundAvailability).isPresent();
        assertThat(foundAvailability.get().getAvailability()).isEqualTo(AvailabilityEnum.AVAILABLE);
    }

    @Test
    @DisplayName("Should not find non-existing availability by AvailabilityEnum")
    void shouldNotFindAvailabilityByNonExistingAvailabilityEnum() {
        // When
        Optional<Availability> foundAvailability = availabilityRepository.findByAvailability(AvailabilityEnum.OUT_OF_STOCK);

        // Then
        assertThat(foundAvailability).isNotPresent();
    }
}