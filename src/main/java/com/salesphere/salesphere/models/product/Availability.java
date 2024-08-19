package com.salesphere.salesphere.models.product;

import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_availability")
@Getter
@Setter
@NoArgsConstructor
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_enum")
    private AvailabilityEnum availability;

    public Availability(AvailabilityEnum availability) {
        this.availability = availability;
    }
}