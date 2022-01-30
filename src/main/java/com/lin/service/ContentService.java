package com.lin.service;

import com.lin.pojo.Goods;
import com.lin.repository.GoodsRepository;
import com.lin.utils.HtmlParseUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class ContentService {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    public RestHighLevelClient restHighLevelClient;

    public void parseContent(String keywords) throws IOException {
        List<Goods> goods = HtmlParseUtil.parseJD(keywords);
        goodsRepository.saveAll(goods);
    }

    public List<Goods> searchDataFromEs(String keywords, int pageNo, int pageSize) throws IOException {
        Page<Goods> goods = goodsRepository.findByTitle(keywords, PageRequest.of(pageNo,pageSize));

        List<Goods> list = new ArrayList<>();

        for (Goods good : goods) {
            list.add(good);
        }
        return list;

    }

    // 高亮
    public List<Map<String, Object>> highlightSearch(String keyword, Integer pageIndex, Integer pageSize) throws IOException {
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //termQuery用作中文失效。name.keyword可以精确匹配，不带keyword就是模糊匹配
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", keyword);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.query(matchQueryBuilder);
        // 精确查询，添加查询条件
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
//        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        searchSourceBuilder.query(termQueryBuilder);
        // 分页
        searchSourceBuilder.from(pageIndex);
        searchSourceBuilder.size(pageSize);
        // 高亮 =========
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 执行查询
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 解析结果 ==========
        SearchHits hits = searchResponse.getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            // 使用新的字段值（高亮），覆盖旧的字段值
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            // 高亮字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField name = highlightFields.get("title");
            // 替换
            if (name != null){
                Text[] fragments = name.fragments();
                StringBuilder new_name = new StringBuilder();
                for (Text text : fragments) {
                    new_name.append(text);
                }
                sourceAsMap.put("title",new_name.toString());
            }
            results.add(sourceAsMap);
        }
        return results;
    }
}
