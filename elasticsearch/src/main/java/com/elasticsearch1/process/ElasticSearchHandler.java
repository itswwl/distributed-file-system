package com.elasticsearch1.process;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.elasticsearch1.model.DataFactory;
import com.elasticsearch1.model.Medicine;
/**
 * Elasticsearch笔记
 * https://my.oschina.net/jhao104/blog?search=Elasticsearch
 * 
 * 
 * Elasticsearch
 * http://www.cnblogs.com/wxw16/tag/Elasticsearch/
 * 
 * ElasticSearch搜索实例
 * http://blog.csdn.net/liyantianmin/article/details/45134423
 * 
 * ElasticSearch
 * http://www.cnblogs.com/paulwinflo/p/4469348.html
 * http://www.cnblogs.com/paulwinflo/p/4469480.html
 * http://www.cnblogs.com/paulwinflo/p/4503933.html
 * 
 * 
 * */

/*
 * 
 *	原程序中的方法和本次使用jar不符合，在本次示例中已被修正
 * 
 *  为什么InetAddress类不能new初始化但可以定义对象：The constructor InetAddress() is not visible
 *  http://blog.csdn.net/pomelotea/article/details/37739673
 *  
 *  
 * */

/**
 * @author Administrator
 * 
 * 使用Java调用ElasticSearch提供的相关API进行数据搜索完整实例演示
 * http://www.tuicool.com/articles/R7RVJb
 *
 */
public class ElasticSearchHandler {

	private Client client;

	public ElasticSearchHandler() {
		// 使用本机做为节点
		this("127.0.0.1");
	}

	public ElasticSearchHandler(String ipAddress) {
		// 集群连接超时设置

		/*
		 * Settings settings = ImmutableSettings.settingsBuilder().put(
		 * "client.transport.ping_timeout", "10s").build(); client = new
		 * TransportClient(settings);
		 */

//		client = new TransportClient(null).addTransportAddress(new InetSocketTransportAddress(ipAddress, 9300));
		try {
			client = new TransportClient.Builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipAddress), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 建立索引,索引建立好之后,会在elasticsearch-0.20.6\data\elasticsearch\nodes\0创建所以你看
	 * 
	 * @param indexName
	 *            为索引库名，一个es集群中可以有多个索引库。 名称必须为小写
	 * @param indexType
	 *            Type为索引类型，是用来区分同索引库下不同类型的数据的，一个索引库下可以有多个索引类型。
	 * @param jsondata
	 *            json格式的数据集合
	 * 
	 * @return
	 */
	public void createIndexResponse(String indexname, String type, List<String> jsondata) {
		// 创建索引库 需要注意的是.setRefresh(true)这里一定要设置,否则第一次建立索引查找不到数据
		IndexRequestBuilder requestBuilder = client.prepareIndex(indexname, type).setRefresh(true);
		for (int i = 0; i < jsondata.size(); i++) {
			requestBuilder.setSource(jsondata.get(i)).execute().actionGet();
		}

	}

	/**
	 * 创建索引
	 * 
	 * @param client
	 * @param jsondata
	 * @return
	 */
	public IndexResponse createIndexResponse(String indexname, String type, String jsondata) {
		IndexResponse response = client.prepareIndex(indexname, type).setSource(jsondata).execute().actionGet();
		return response;
	}

	/**
	 * 执行搜索
	 * 
	 * @param queryBuilder
	 * @param indexname
	 * @param type
	 * @return
	 */
	public List<Medicine> searcher(QueryBuilder queryBuilder, String indexname, String type) {
		List<Medicine> list = new ArrayList<Medicine>();
		SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type).setQuery(queryBuilder).execute()
				.actionGet();
		SearchHits hits = searchResponse.getHits();
		System.out.println("查询到记录数=" + hits.getTotalHits());
		SearchHit[] searchHists = hits.getHits();
		if (searchHists.length > 0) {
			for (SearchHit hit : searchHists) {
				Integer id = (Integer) hit.getSource().get("id");
				String name = (String) hit.getSource().get("name");
				String function = (String) hit.getSource().get("funciton");
				list.add(new Medicine(id, name, function));
			}
		}
		return list;
	}

	public static void main(String[] args) {
		ElasticSearchHandler esHandler = new ElasticSearchHandler();
		List<String> jsondata = DataFactory.getInitJsonData();
		String indexname = "indexdemo";
		String type = "typedemo";
		esHandler.createIndexResponse(indexname, type, jsondata);
		// 查询条件
		QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "感冒");
		/*
		 * QueryBuilder queryBuilder = QueryBuilders.boolQuery()
		 * .must(QueryBuilders.termQuery("id", 1));
		 */
		List<Medicine> result = esHandler.searcher(queryBuilder, indexname, type);
		for (int i = 0; i < result.size(); i++) {
			Medicine medicine = result.get(i);
			System.out
					.println("(" + medicine.getId() + ")药品名称:" + medicine.getName() + "\t\t" + medicine.getFunction());
		}
	}
}
