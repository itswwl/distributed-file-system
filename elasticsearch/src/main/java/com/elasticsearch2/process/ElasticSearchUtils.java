package com.elasticsearch2.process;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortBuilders;

/**
 * @author Administrator
 *
 * ElasticSearch 安装 和java api
 * http://blog.csdn.net/hfmbook/article/details/51881678
 * 
 * 本例子是集群部署，没有测试
 * 
 */
public class ElasticSearchUtils {

    protected TransportClient client ; 

    protected String index ; 

    protected String type ; 

    protected String idKey ;
    /**
     * 创建TransportClient
     * @param clusterName 集群名称
     * @param host 地址
     * @param port es端口 
     * */
    TransportClient getTransportClient(String clusterName , String host , Integer port) throws  Exception {
        Builder builder = Settings.builder().put("cluster.name",  clusterName ) ;
        TransportClient client = TransportClient
                .builder()
                .settings(builder)
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress
                                .getByName( host ), port));
        return client ; 
    }
    /**
     * 创建TransportClient
     * @param clusterName 集群名称
     * @param host 地址
     * @param port es端口 , 末日�?300
     * @param index 索引
     * @param type 类型
     * @param idKey 主名
     * */
    public static ElasticSearchUtils getElasticSearch(String clusterName
             , String host , Integer port , String index , String type , String idKey){
        try {
            ElasticSearchUtils search = new ElasticSearchUtils();
            search.client = search.getTransportClient(clusterName, host, port) ;
            search.idKey = idKey ;
            search.index = index ; 
            search.type = type ; 
            return search ; 
        } catch(Exception e ) {
            throw new RuntimeException( e.getMessage() , e )  ;
        }
    }

    public static void main(String[] args) {
        ElasticSearchUtils searchUtils = ElasticSearchUtils.getElasticSearch("hks"
                , "127.0.0.1", 9300, "hksesdata", "part", "id") ;  
        /*创建搜索引擎*/
        SearchRequestBuilder requestBuilder = searchUtils.client.prepareSearch( searchUtils.index )
                .setTypes( searchUtils.type ) .setSearchType(SearchType.DEFAULT
                        ).setFrom(0)
                .setSize( 20 ) ; 
        /*指定排序字段,可以指定多个*/
        requestBuilder.addSort( SortBuilders.fieldSort("p1001_customertype2") );
        requestBuilder.addSort( SortBuilders.fieldSort("p1001_customertype2") );
        //requestBuilder.setQuery(QueryBuilders.matchPhraseQuery( "keyword" , "FDB" ));
        /*使用bool查询*/
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        /*设置查询*/
        requestBuilder.setQuery(
                //QueryBuilders.termsQuery("keyword", "FDB" , "鲁达" )
                boolQueryBuilder
                .must(QueryBuilders.matchQuery("keyword", "奥迪 Q5"))
        );
        SearchResponse actionGet = requestBuilder.setExplain(true).execute().actionGet();
        SearchHits searchHits =  actionGet.getHits() ;
        SearchHit[] hits = searchHits.getHits();
        System.out.println( "totalHits: " + searchHits.getTotalHits() ); 
        for (SearchHit hit : hits) {
            Map<String, Object> source = hit.getSource() ; 
            if(null != source){
                System.out.println( source.get("materialcode") + "-->" + source.get("materialname"));
            }
        }
        /*---------------------分组 搜索------------------------*/
        SearchRequestBuilder searchRequestBuilder = searchUtils.client.prepareSearch( searchUtils.index )
        .setTypes( searchUtils.type ) .setSearchType(SearchType.DEFAULT
                ).setFrom(0)
        .setSize( 100 ) ; 

        searchRequestBuilder.setQuery(
                //QueryBuilders.termsQuery("keyword", "FDB" , "鲁达" )
                boolQueryBuilder
                .must(QueryBuilders.matchQuery("keyword", "奥迪"))
        );
        TermsBuilder  partidTermsBuilder = AggregationBuilders.terms("postion").field("relation_postion") ;
        partidTermsBuilder.size( 100 );
        searchRequestBuilder.addAggregation( partidTermsBuilder );
        SearchResponse sr = searchRequestBuilder.execute().actionGet();
        Map<String, Aggregation> aggMap = sr.getAggregations().asMap() ; 
        StringTerms postionTerms = (StringTerms) aggMap.get("postion") ; 
        Iterator<Terms.Bucket> iterator = postionTerms.getBuckets().iterator();
        while(iterator.hasNext()){
            Bucket next = iterator.next();
            System.out.println( next.getKey() + "--->" + next.getDocCount());
        }
    }
}
