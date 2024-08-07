package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.Availability;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    Optional<Availability> findByAvailability(AvailabilityEnum availabilityEnum);
}