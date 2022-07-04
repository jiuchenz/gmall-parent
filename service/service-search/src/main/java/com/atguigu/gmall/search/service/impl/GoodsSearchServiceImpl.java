package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.*;
import com.atguigu.gmall.search.repo.GoodsRepositry;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    GoodsRepositry goodsRepositry;

    @Autowired
    ElasticsearchRestTemplate esTemplate;

    @Override
    public void upGoods(Goods goods) {
        goodsRepositry.save(goods);
    }

    @Override
    public void downGoods(Long skuId) {
        goodsRepositry.deleteById(skuId);
    }

    @Override
    public SearchResponseVo search(SearchParam searchParam) {
        //真正的检索
        //1.根据searchParam封装成真正的query条件
        Query query = buildQuery(searchParam);
        //2.根据查询条件查询esku
        SearchHits<Goods> goods = esTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));
        //3.根据goods列表转换为SearchResponseVo
        SearchResponseVo vo = buildSearchResponseVo(goods, searchParam);
        return vo;
    }

    private SearchResponseVo buildSearchResponseVo(SearchHits<Goods> result, SearchParam param) {
        SearchResponseVo vo = new SearchResponseVo();
        //private List<Goods> goodsList;
        List<Goods> list =result.getSearchHits().stream().map(hit -> {
            Goods good = hit.getContent();
            return good;
        }).collect(Collectors.toList());
        vo.setGoodsList(list);

        //private Long pageNo;
        vo.setPageNo(param.getPageNo());

        //private Integer totalPages;
        long totalHits = result.getTotalHits();
        long l = totalHits % 10 == 0 ? totalHits / 10 : totalHits / 10 + 1;
        vo.setTotalPages(new Integer(l + ""));

        //private SearchParam searchParam;
        vo.setSearchParam(param);

        //private List<TrademarkSearchVo> trademarkList;
        //拿到所有的查询结果
        ParsedLongTerms tmIdAgg = result.getAggregations().get("tmIdAgg");
        List<TrademarkSearchVo> tmList =tmIdAgg.getBuckets().stream().map(bucket -> {
            //拿到品牌id
            long tmId = bucket.getKeyAsNumber().longValue();
            //拿到品牌属性
            ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            //拿到品牌logo
            ParsedStringTerms tmLogoAgg = bucket.getAggregations().get("tmLogoAgg");
            String tmLogo = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            //封装tmvo
            TrademarkSearchVo tmVo = new TrademarkSearchVo();
            tmVo.setTmId(tmId);
            tmVo.setTmName(tmName);
            tmVo.setTmLogoUrl(tmLogo);
            return tmVo;
        }).collect(Collectors.toList());
        vo.setTrademarkList(tmList);

        //private List<AttrSearchVo> attrsList;
        //拿到所有的查询结果
        ParsedNested attrAgg = result.getAggregations().get("attrAgg");
        //拿到attrId的值分布
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<AttrSearchVo> attrList =  attrIdAgg.getBuckets().stream().map(bucket -> {
            //属性id
            long attId = bucket.getKeyAsNumber().longValue();
            //属性名
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            //属性值
             new ArrayList<>();
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            List<String> vals = attrValueAgg.getBuckets().stream().map(val -> {
                String attrValue = val.getKeyAsString();
                return attrValue;
            }).collect(Collectors.toList());
            AttrSearchVo attrSearchVo = new AttrSearchVo();
            attrSearchVo.setAttrId(attId);
            attrSearchVo.setAttrName(attrName);
            attrSearchVo.setAttrValueList(vals);
            return attrSearchVo;
        }).collect(Collectors.toList());
        vo.setAttrsList(attrList);

        //前端的当时检索时的完整请求路径
        String urlParam = makeUrlParam(param);
        vo.setUrlParam(urlParam);

        //private String trademarkParam;//1:小米；制作品牌面包屑
        String trademark = param.getTrademark();
        if (!StringUtils.isEmpty(trademark)) {
            vo.setTrademarkParam("品牌：" + trademark.split(":")[1]);
        }

        //属性的面包屑private List<AttrBread> propsParamList;
        if (param.getProps() != null && param.getProps().length > 0) {
            //3:6GB:运行内存   4:64GB:机身存储
            //制作每个检索属性的面包屑
            List<AttrBread> attrBreadList =Arrays.stream(param.getProps()).map(str -> {
                String[] split = str.split(":");
                AttrBread bread = new AttrBread();
                bread.setAttrId(Long.parseLong(split[0]));
                bread.setAttrName(split[2]);//name：运行内存
                bread.setAttrValue(split[1]);//value： 6GB
                return bread;
            }).collect(Collectors.toList());
            vo.setPropsParamList(attrBreadList);
        }

        // order=2:desc
        //回显orderMap
        String order = param.getOrder(); //order=2:asc  order=1:desc
        OrderMap orderMap = new OrderMap();
        if(!StringUtils.isEmpty(order) && !"null".equals(order)){
            orderMap.setType(order.split(":")[0]);
            orderMap.setSort(order.split(":")[1]);
        }
        vo.setOrderMap(orderMap);

        return vo;
    }

    private Query buildQuery(SearchParam param) {
        //条件检索
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //最外层的查询条件
        NativeSearchQuery dsl = new NativeSearchQuery(boolQuery);
        //==========查询条件==========
        //先判断查询条件
        //查询三级分类bool-must-term
        if (param.getCategory1Id() != null) {
            //按照三级分类查询
            boolQuery.must(QueryBuilders.termQuery("category1Id", param.getCategory1Id()));
        }
        if (param.getCategory2Id() != null) {
            //按照三级分类查询
            boolQuery.must(QueryBuilders.termQuery("category2Id", param.getCategory2Id()));
        }
        if (param.getCategory3Id() != null) {
            //按照三级分类查询
            boolQuery.must(QueryBuilders.termQuery("category3Id", param.getCategory3Id()));
        }
        //查询商品名称bool-must-match
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("title", param.getKeyword()));
        }
        //查询品牌id bool-must-term   1:小米
        if (!StringUtils.isEmpty(param.getTrademark())) {
            String trademark = param.getTrademark();
            String[] split = trademark.split(":");
            boolQuery.must(QueryBuilders.termQuery("tmId", split[0]));
        }
        //查询属性 bool-must-nested attrs id-term value-term
        //["3:6GB:运行内存","4:64GB:机身存储"]
        if (param.getProps() != null && param.getProps().length > 0) {
            for (String prop : param.getProps()) {
                //["3:6GB:运行内存","4:64GB:机身存储"]
                String[] split = prop.split(":");
                //nested里面的boolQuery
                BoolQueryBuilder propBool = QueryBuilders.boolQuery();
                propBool.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                propBool.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                boolQuery.must(QueryBuilders.nestedQuery("attrs", propBool, ScoreMode.None));
            }
        }
//            Arrays.stream(param.getProps()).foreach(prop -> {
//
//
//            });
        //===========排序条件===========
        //1:asc热度升序     2:desc价格降序
        if (!StringUtils.isEmpty(param.getOrder())) {
            Sort sort = null;
            String[] split = param.getOrder().split(":");
            switch (split[0]) {
                case "1":
                    sort = split[1].equalsIgnoreCase("asc") ? Sort.by("hotScore").ascending() :
                            Sort.by("hotScore").descending();
                    break;
                case "2":
                    sort = split[1].equalsIgnoreCase("desc") ? Sort.by("price").ascending() :
                            Sort.by("price").descending();
                    break;
            }
            dsl.addSort(sort);
        }
        //===========聚合分析==========
        //1.品牌id-聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId").size(100);
        //2.品牌id-聚合-品牌名字聚合
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName").size(1));
        //3.品牌id-聚合-品牌图片聚合
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmLogoAgg").field("tmLogoUrl").size(1));
        dsl.addAggregation(tmIdAgg);
        //==聚合平台属性==
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(100);
        //attrNameAgg
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1));
        //attrValueAgg
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(100));
        attrAgg.subAggregation(attrIdAgg);
        dsl.addAggregation(attrAgg);

        return dsl;
    }

    private String makeUrlParam(SearchParam param) {
        StringBuilder urlBuilder = new StringBuilder("/list.html?");
        if (param.getPageNo() != null) {
            // /list.html?pageNo=1
            urlBuilder.append("&pageNo=" + param.getPageNo());
        }

        if (param.getCategory1Id() != null) {
            urlBuilder.append("&category1Id=" + param.getCategory1Id());
        }
        if (param.getCategory2Id() != null) {
            urlBuilder.append("&category2Id=" + param.getCategory2Id());
        }
        if (param.getCategory3Id() != null) {
            urlBuilder.append("&category3Id=" + param.getCategory3Id());
        }
        if (!StringUtils.isEmpty(param.getKeyword())) {
            urlBuilder.append("&keyword=" + param.getKeyword());
        }
        if (!StringUtils.isEmpty(param.getTrademark())) {
            urlBuilder.append("&trademark=" + param.getTrademark());
        }
        if (param.getProps() != null && param.getProps().length > 0) {
            Arrays.stream(param.getProps()).forEach(prop -> {
                urlBuilder.append("&props=" + prop);
            });
        }
//        if(!StringUtils.isEmpty(param.getOrder())){
//            urlBuilder.append("&order="+param.getOrder());
//        }
        return urlBuilder.toString();
    }

}