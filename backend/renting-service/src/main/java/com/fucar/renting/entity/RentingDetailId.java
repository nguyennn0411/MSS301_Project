package com.fucar.renting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RentingDetailId implements Serializable {
    @Column(name = "renting_transaction_id")
    private Long rentingTransactionId;

    @Column(name = "car_id")
    private Long carId;
}
