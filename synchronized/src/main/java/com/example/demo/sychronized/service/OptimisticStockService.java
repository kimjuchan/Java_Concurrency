package com.example.demo.sychronized.service;


import com.example.demo.sychronized.domain.Stock;
import com.example.demo.sychronized.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticStockService {
    private StockRepository stockRepository;

    public OptimisticStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
    @Transactional
    public void decrease(final Long id, final Long quantity) {
        Stock stock = stockRepository.findByWithOptimisticLock(id);
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }

}
