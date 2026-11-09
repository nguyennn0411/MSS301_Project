package com.fucar.renting.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "renting_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentingDetail {
    @EmbeddedId
    private RentingDetailId id;

    @ManyToOne
    @MapsId("rentingTransactionId")
    @JoinColumn(name = "renting_transaction_id")
    private RentingTransaction rentingTransaction;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
}
