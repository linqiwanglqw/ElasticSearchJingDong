package com.lin.repository;

import com.lin.pojo.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends ElasticsearchRepository<Goods,String> {

    Page<Goods> findByTitle(String name, Pageable pageable);


}
