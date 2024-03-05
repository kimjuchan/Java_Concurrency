package com.example.demo.sychronized.service;

import com.example.demo.sychronized.domain.Stock;
import com.example.demo.sychronized.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PesimisticLockStockServiceImpl {
    private StockRepository stockRepository;

    public PesimisticLockStockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(final Long id, final Long quantity) {
        Stock stock = stockRepository.findByWithPessimisticLock(id);
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
