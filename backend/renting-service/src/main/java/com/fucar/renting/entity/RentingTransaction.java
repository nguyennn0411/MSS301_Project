package com.fucar.renting.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "renting_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentingTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentingTransactionId;

    private LocalDate rentingDate;
    @Builder.Default
    private BigDecimal basePrice = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal lateFee = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal damageFee = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal otherFee = BigDecimal.ZERO;
    private BigDecimal totalPrice;
    private Long customerId;
    private String rentingStatus;
    private String sagaId;
    private String adminNote;

    @Builder.Default
    @OneToMany(mappedBy = "rentingTransaction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RentingDetail> rentingDetails = new ArrayList<>();
}
