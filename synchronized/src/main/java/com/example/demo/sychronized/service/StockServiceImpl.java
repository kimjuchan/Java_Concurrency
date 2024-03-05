package com.example.demo.sychronized.service;

import com.example.demo.sychronized.domain.Stock;
import com.example.demo.sychronized.repository.StockRepository;
import lombok.Synchronized;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockServiceImpl {

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(final Long id, final Long quantity){
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 ID 정보를 가지고 있지 않아요."));
        stock.decrease(quantity);
        //
        stockRepository.saveAndFlush(stock);
    }
}
