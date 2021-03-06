package com.mazdah.eeimporter.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;

import org.slf4j.Logger;

public class ElasticHelper {
	
	private final static Logger logger = LoggerFactory.getLogger(ElasticHelper.class);
	
	public static void makeIndex (Map<String, String> indexMap) {
		HashMap<String, String> idxMap;
		
		if (indexMap != null) {
			idxMap = (HashMap<String, String>) indexMap;
		}

		XContentBuilder indexSettings = null;
		  try {
		   indexSettings = XContentFactory.jsonBuilder();
		   indexSettings.startObject()
		       .field("index.number_of_shards", 3)   // 샤드 갯수 지정
		       .field("index.number_of_replicas", 1)  // 레플리카 갯수 지정
		    // index
		       .startObject("index")
		       	// analysis
		       		.startObject("analysis")
		       		// analyzer
		       			.startObject("analyzer")
		       				.startObject("common_analyzer")
		       					.field("type", "cjk")
		       					.field("filter", Arrays.asList("lowercase", "trim"))
		       				.endObject()
		       				.startObject("pattern_analyzer")
		       					.field("type", "custom")
		       					.field("tokenizer", "pattern_tokenizer")
		       					.field("filter", Arrays.asList("lowercase", "trim"))
		       				.endObject()
		       				.startObject("ngram_analyzer")
		       					.field("type", "custom")
		       					.field("tokenizer", "ngram_tokenizer")
		       					.field("filter", Arrays.asList("lowercase", "trim"))
		       				.endObject()
		       			.endObject()
		       		// analyzer
		       		// tokenizer
		       			.startObject("tokenizer")
		       				.startObject("ngram_tokenizer")
		       					.field("type", "nGram")
		       					.field("min_gram", "2")
		       					.field("max_gram", "10")
		       					.field("token_char", Arrays.asList("letter", "digit"))
		       				.endObject()
		       				.startObject("pattern_tokenizer")
		       					.field("type", "pattern")
		       					.field("pattern", ",")
		       				.endObject()
		       			.endObject()
		       		// tokenizer
		       		.endObject()
		       	// analysis
		       	// store
		       		.startObject("store")
		       			.field("type", "mmapfs")
		       			.startObject("compress")
		       				.field("stored", true)
		       				.field("tv", true)
		       			.endObject()
		       		.endObject()
		       	// store
		     // index
		       	.endObject()
		      .endObject()
		      .prettyPrint();
		   
		   	logger.info("##### setting info : " + indexSettings.string());
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
	}
	
	public static Map<String, Object> makeSimpleIndex (Map<String, Object> indexMap) {
		Client client = ElasticClientHelper.newTransportClient();
//		IndicesAdminClient indicesClient = client.admin().indices();
		
		String indexName = (String)indexMap.get("indexName");
		Integer shardNum = (Integer)indexMap.get("shardNum");
		Integer replicaNum = (Integer)indexMap.get("replicaNum");
		
		
		CreateIndexResponse response = client.admin().indices().prepareCreate(indexName)
        .setSettings(Settings.builder()             
                .put("index.number_of_shards", shardNum)
                .put("index.number_of_replicas", replicaNum)
        )
        .get();
		
		String index = response.index();
		boolean ack = response.isAcknowledged();
		TransportAddress address = response.remoteAddress();
		
		Map <String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("indexName", index);
		resultMap.put("ack", ack);
		resultMap.put("address", address);
		
		ElasticClientHelper.connectDisconnect(client);
		
		return resultMap;
	}
	
	public static IndexResponse importData (String id, String index, String type, String keyVals) {
		Client client = ElasticClientHelper.newTransportClient();
		IndexResponse response = client.prepareIndex(index, type, id)
				.setSource(keyVals, XContentType.JSON)
		        .get();

		ElasticClientHelper.connectDisconnect(client);
		return response;
	}
	
	public static BulkResponse importBulkData (List<String> idList, String index, String type, List<String> keyVals) {
		Client client = ElasticClientHelper.newTransportClient();
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		
		int cnt = keyVals.size();
		for (int i = 0; i < cnt; i++) {
			String keyVal = keyVals.get(i);
			
			bulkRequest.add(client.prepareIndex(index, type, idList.get(i))
					.setSource(keyVal, XContentType.JSON)
			        );
		}
		
		BulkResponse response = bulkRequest.get();
		
		ElasticClientHelper.connectDisconnect(client);
		
		return response;
	}
	
	public static String[] getIndexList() {

		Client client = ElasticClientHelper.newTransportClient();
//		IndicesAdminClient indicesClient = client.admin().indices();
		
		String[] indices = client.admin()
			    .indices()
			    .getIndex(new GetIndexRequest())
			    .actionGet()
			    .getIndices();

		ElasticClientHelper.connectDisconnect(client);
		
		return indices;
	}
	
	public static List<String> getTypeList(String index) {
		List<String> typeList = new ArrayList<String>();
		Client client = ElasticClientHelper.newTransportClient();
		
		GetMappingsResponse res;
		try {
			res = client.admin().indices().getMappings(new GetMappingsRequest().indices(index)).get();
			ImmutableOpenMap<String, MappingMetaData> mapping = res.mappings().get(index);
            
	        for (ObjectObjectCursor<String, MappingMetaData> c : mapping) {
	            typeList.add(c.key);
	        }
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			logger.debug(e.getLocalizedMessage());
		}
        
		ElasticClientHelper.connectDisconnect(client);
		
		return typeList;
	}
}
