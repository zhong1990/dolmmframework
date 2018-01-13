/**
 * dol-framework-search 
 * IndexService.java 
 * org.dol.framework.search 
 * TODO  
 * @author dolphin
 * @date   2015年12月15日 下午3:38:05 
 * @Copyright 2015, 唯创国际 幸福9号 All Rights Reserved. 
 * @version   1.0
 */

package org.dol.framework.search;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.DisposableBean;

import org.dol.framework.util.ListUtil;
import org.dol.framework.util.MapUtil;

/**
 * ClassName:IndexService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年12月15日 下午3:38:05 <br/>
 * 
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class IndexService implements DisposableBean {

	@Override
	public void destroy() throws Exception {

		if (querySolrClient != null) {
			querySolrClient.close();
		}
		if (updateSolrClient != null) {
			updateSolrClient.close();
		}
	}

	private static final Logger LOGGER = Logger.getLogger(IndexService.class);

	private SolrClient querySolrClient;
	private SolrClient updateSolrClient;

	public void addIndex(List<Map<String, Object>> dataList) throws IOException, SolrServerException {
		if (ListUtil.isNullOrEmpty(dataList)) {
			return;
		}
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (Map<String, Object> map : dataList) {
			Set<Entry<String, Object>> entries = map.entrySet();
			SolrInputDocument doc = new SolrInputDocument();
			for (Entry<String, Object> entry : entries) {
				Object valueObject = entry.getValue();
				if (valueObject == null) {
					continue;
				}
				doc.addField(entry.getKey(), prepareValue(valueObject));
			}
			docs.add(doc);
		}
		updateSolrClient.add(docs);
		updateSolrClient.commit();
	}

	public void deleteByQuery(String query) throws IOException, SolrServerException {
		updateSolrClient.deleteByQuery(query);
		updateSolrClient.commit();
	}

	public void updateIndex(List<Map<String, Object>> dataList, List<String> deleteIdList) throws IOException, SolrServerException {

		if (ListUtil.isNullOrEmpty(dataList)
		        && ListUtil.isNullOrEmpty(deleteIdList)) {
			return;
		}
		if (ListUtil.isNotNullAndEmpty(dataList)) {
			Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>(dataList.size());
			for (Map<String, Object> map : dataList) {
				Set<Entry<String, Object>> entries = map.entrySet();
				SolrInputDocument doc = new SolrInputDocument();
				for (Entry<String, Object> entry : entries) {
					Object valueObject = entry.getValue();
					if (valueObject == null) {
						continue;
					}
					doc.addField(entry.getKey(), prepareValue(valueObject));
				}
				docs.add(doc);
			}
			updateSolrClient.add(docs);
			LOGGER.info("更新/添加索引" + dataList.size() + "条");
		}
		if (ListUtil.isNotNullAndEmpty(deleteIdList)) {
			updateSolrClient.deleteById(deleteIdList);
			LOGGER.info("删除索引" + deleteIdList.size() + "条");
		}
		updateSolrClient.commit();
	}

	public Object prepareValue(Object valueObject) {
		if (valueObject == null) {
			return null;
		}
		if (valueObject instanceof BigDecimal) {
			return ((BigDecimal) valueObject).floatValue();
		}
		if (valueObject instanceof BigInteger) {
			return ((BigInteger) valueObject).longValue();
		}
		return valueObject;
	}

	public void updateIndex(
	        List<Map<String, Object>> dataList,
	        String uniqFieldName,
	        Boolean addIfNotFound) throws IOException, SolrServerException {

		if (ListUtil.isNullOrEmpty(dataList)) {
			return;
		}

		List<Object> list = getExistKeyList(dataList, uniqFieldName);
		if (list.isEmpty()) {
			return;
		}
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>(dataList.size());
		for (Map<String, Object> map : dataList) {
			Set<Entry<String, Object>> entries = map.entrySet();
			Object uniqValue = map.get(uniqFieldName);
			if (uniqValue == null) {
				continue;
			}
			if (list.contains(uniqValue)) {
				SolrInputDocument doc = new SolrInputDocument();
				doc.addField(uniqFieldName, prepareValue(uniqValue));
				for (Entry<String, Object> entry : entries) {
					if (entry.getKey().equals(uniqFieldName)) {
						continue;
					}

					doc.addField(entry.getKey(), MapUtil.buildMap(
					        "set",
					        prepareValue(entry.getValue())));

				}
				docs.add(doc);
			} else if (addIfNotFound) {
				SolrInputDocument doc = new SolrInputDocument();
				for (Entry<String, Object> entry : entries) {

					Object val = entry.getValue();
					if (val == null) {
						continue;
					}
					doc.addField(entry.getKey(), prepareValue(val));
				}
				docs.add(doc);
			}
		}
		updateSolrClient.add(docs);
		updateSolrClient.commit();

	}

	public void updateIndex(
	        List<Map<String, Object>> dataList,
	        String uniqFieldName) throws IOException, SolrServerException {
		updateIndex(dataList, uniqFieldName, false);
	}

	private List<Object> getExistKeyList(
	        List<Map<String, Object>> dataList,
	        String uniqFieldName)
	        throws SolrServerException, IOException {
		List<String> uniquNameList = MapUtil.getValueList(dataList, uniqFieldName, true);
		int size = 100;
		int startIndex = 0;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setStart(0);
		solrQuery.setFields(uniqFieldName);
		List<Object> list = new ArrayList<Object>(uniquNameList.size());
		while (startIndex < uniquNameList.size()) {
			int endIndex = Math.min(startIndex + size, uniquNameList.size());
			List<String> subList = uniquNameList.subList(startIndex, endIndex);
			solrQuery.setQuery(uniqFieldName + ":(" + ListUtil.connectToString(subList, ' ') + ")");
			solrQuery.setRows(subList.size());
			QueryResponse queryResponse = querySolrClient.query(solrQuery);
			if (queryResponse.getStatus() == 0) {
				SolrDocumentList solrDocumentList = queryResponse.getResults();
				for (SolrDocument solrDocument : solrDocumentList) {
					list.add(solrDocument.getFieldValue(uniqFieldName));
				}
			}
			startIndex = endIndex;
		}
		return list;
	}

	public void deleteIndex(List<String> ids) throws IOException, SolrServerException {
		if (ListUtil.isNullOrEmpty(ids)) {
			return;
		}
		updateSolrClient.deleteById(ids);
		updateSolrClient.commit();
	}

	public SolrClient getQuerySolrClient() {
		return querySolrClient;
	}

	public void setQuerySolrClient(SolrClient querySolrClient) {
		this.querySolrClient = querySolrClient;
	}

	public void setUpdateSolrClient(SolrClient updateSolrClient) {
		this.updateSolrClient = updateSolrClient;
	}
}
