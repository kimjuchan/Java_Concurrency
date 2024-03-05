package com.example.demo.sychronized.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    public Stock(Long id, Long quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public void decrease(final Long quantity){
        if(this.quantity - quantity < 0) throw new RuntimeException("재고가 부족합니다.");

        this.quantity = this.quantity - quantity;

    }
}
