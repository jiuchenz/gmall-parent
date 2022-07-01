package com.atguigu.gmall.search.repo;


import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepositry extends PagingAndSortingRepository<Goods,Long> {
}
