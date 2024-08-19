package com.salesphere.salesphere.repositories.product;

import com.salesphere.salesphere.models.product.Availability;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    Optional<Availability> findByAvailability(AvailabilityEnum availabilityEnum);
}