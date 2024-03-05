package com.example.demo.sychronized.service;

import com.example.demo.sychronized.domain.Stock;
import com.example.demo.sychronized.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@ActiveProfiles("prod") //profile.active  설정.
class StockServiceImplTest {

    @Autowired
    private StockServiceImpl stockService;
    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @Test
    public void stock_decrease() {
        stockService.decrease(1L, 1L);
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(99L);
    }


    @Test
    @DisplayName("동시에_100개의_요청")
    public void requests_100_AtTheSameTime() throws InterruptedException{
        int threadCnt = 50;

        //멀티스레드 이용 ExecutorService : 비동기를 단순하게 처리할 수 있도록 해주는 java api
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //다른 스레드에서 수행이 완료될 때 까지 대기할 수 있도록 도와주는 API - 요청이 끝날때 까지 기다림
        CountDownLatch latch = new CountDownLatch(threadCnt);
        for(int i=0; i<threadCnt; i++){
            executorService.submit(()->{
                try {
                    stockService.decrease(1L,1L);
                    Stock remain_quantity = stockRepository.findById(1L).orElseThrow();
                    System.out.println("1) 현재 실행중인 thread 정보 : " +Thread.currentThread().getName());
                    System.out.println("2) 남은 quantity : " + remain_quantity.getQuantity());
                }finally {
                    latch.countDown();
                    System.out.println("3) 남은 latch : " +latch.getCount());
                }
            });
        }
        latch.await();
        Stock stock = stockRepository.findById(1L).orElseThrow();
        //실제로 남은 재고 수가 0이 아님을 확인 -> 레이스 컨디션(2개 이상의 스레드가 공유 데이터에 접근할 수 있고, 동시에 변경하려할 때 발생하는 문제.
        assertThat(stock.getQuantity()).isEqualTo(0L);
        System.out.println("## TEST END ##");

    }

    /**
     * --> 이런식으로 순차적으로 스레드가 접근하여 감소시킨다는 예상과는 달리 동시에 같은 quantity를 접근 및 변경하게 되면서 문제 발생.
     * ->문제해결
     * [1] -  Synchronized 사용
     *  method에  Synchronized  선언을 통해서 스레드간 동기화 진행.
     *  공유데이터 Thread-safe 보장
     *  ->문제 하나의 프로세스내에서만 동기화 지원    (서버가 2대 이상일 경우의 문제 동일한 문제 발생)
     *
     * [2] Pesimistic lock ->  사용중인 데이터에  Lock 걸어 다른 서버에서 데이터 접근 시 대기
     * 서로 자원이 필요한 경우 데드 락 발생
     * 동시성이 떨어져 그만큼 성능 이슈 발생.
     *
     * [3] Optimistic lock -> Lock 대신 버전을 이용함으로 정합성 맞추는 방법.
     * Stock 도메인에 version 컬럼 추가하여,   version 값 관리
     *
     * 1) 현재 실행중인 thread 정보 : pool-2-thread-3
     * 1) 현재 실행중인 thread 정보 : pool-2-thread-7
     * 2) 남은 quantity : 98
     * 2) 남은 quantity : 98
     * 3) 남은 latch : 48
     * 3) 남은 latch : 49
     * 1) 현재 실행중인 thread 정보 : pool-2-thread-10
     * 2) 남은 quantity : 96
     * 3) 남은 latch : 47
     * 1) 현재 실행중인 thread 정보 : pool-2-thread-4
     * 2) 남은 quantity : 96
     * 3) 남은 latch : 46
     * 1) 현재 실행중인 thread 정보 : pool-2-thread-9
     *.... 생략
     *
     * Expected :0L
     * Actual   :55L
     */


}