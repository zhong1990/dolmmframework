package org.dol.framework.search;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.dol.framework.http.RequestResult;
import org.dol.framework.logging.Logger;
import org.dol.framework.util.StringUtil;

/**
 * TODO
 * 
 * @author dolphin
 * @date 2015年12月13日 下午3:52:03
 * @version 1.0
 */
public class SearchService implements InitializingBean, DisposableBean {

	private static final String DEFAULT_KEYWORDS = "*:*";

	private static final String STRING_COLON = ":";

	private static final Logger LOGGER = Logger.getLogger(SearchService.class);

	private SolrClient solrClient;

	public RequestResult query(
	        String keywords,
	        Map<String, Object> filters,
	        String returnFeilds,
	        Integer pageIndex,
	        Integer pageSize,
	        String sortField,
	        String sortDirection) throws Exception {

		SolrQuery solrParams = buildSolrQuery(keywords, filters, returnFeilds, pageIndex, pageSize, sortField, sortDirection);
		String url = serviceUrl + ClientUtils.toQueryString(solrParams, false);
		RequestResult requestResult = HttpRequestUtil.get(url, null);
		return requestResult;
	}

	public QueryResponse search(
	        String keywords,
	        Map<String, Object> filters,
	        String returnFeilds,
	        Integer pageIndex,
	        Integer pageSize,
	        String sortField,
	        String sortDirection,
	        String coreName) throws Exception {
		SolrQuery solrParams = buildSolrQuery(keywords, filters, returnFeilds, pageIndex, pageSize, sortField, sortDirection);
		return solrClient.query(coreName, solrParams, METHOD.POST);
	}

	private SolrQuery buildSolrQuery(
	        String keywords,
	        Map<String, Object> filters,
	        String returnFeilds,
	        Integer pageIndex,
	        Integer pageSize,
	        String sortField,
	        String sortDirection) {
		SolrQuery solrParams = new SolrQuery();
		if (StringUtil.isNotBlank(keywords)) {
			solrParams.add(CommonParams.Q, keywords);
		} else {
			solrParams.add(CommonParams.Q, DEFAULT_KEYWORDS);
		}
		if (filters != null && !filters.isEmpty()) {
			Set<Entry<String, Object>> entries = filters.entrySet();
			for (Entry<String, Object> entry : entries) {
				solrParams.addFilterQuery(entry.getKey() + STRING_COLON + entry.getValue());
			}
		}
		if (StringUtil.isNotBlank(returnFeilds)) {
			solrParams.addFacetField(returnFeilds);
		}
		solrParams.setStart(pageIndex).setRows(pageSize);
		if (StringUtil.isNotBlank(sortField)) {
			ORDER order = StringUtil.isBlank(sortDirection) ? ORDER.asc : ORDER.valueOf(sortDirection);
			solrParams.setSort(sortField, order);
		}
		return solrParams;
	}

	public SearchResult search(
	        String keywords,
	        Map<String, Object> filters,
	        String returnFeilds,
	        Integer pageIndex,
	        Integer pageSize,
	        String sortField,
	        String sortDirection) {
		SearchResult searchResult = null;
		try {
			QueryResponse queryResponse = search(
			        keywords,
			        filters,
			        returnFeilds,
			        pageIndex,
			        pageSize,
			        sortField,
			        sortDirection,
			        null);

			searchResult = new SearchResult();
			if (queryResponse.getStatus() == 0) {
				SearchResponse searchResponse = new SearchResponse();
				searchResponse.setDocs(queryResponse.getResults());
				searchResponse.setNumFound(queryResponse.getResults().getNumFound());
				searchResponse.setStart(queryResponse.getResults().getStart());
				searchResult.setResponse(searchResponse);

			} else {
				SearchError error = new SearchError();
				error.setCode(-1);
				searchResult.setError(error);
			}
		} catch (Exception e) {
			searchResult = new SearchResult();
			SearchError error = new SearchError();
			error.setCode(-1);
			error.setMsg(e.getMessage());
			searchResult.setError(error);
		}
		return searchResult;
	}

	private String serviceUrl;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	public SolrClient getSolrClient() {
		return solrClient;
	}

	public void setSolrClient(SolrClient solrClient) {
		this.solrClient = solrClient;
		if (solrClient instanceof HttpSolrClient) {
			HttpSolrClient httpSolrClient = (HttpSolrClient) solrClient;
			this.serviceUrl = httpSolrClient.getBaseURL();
		}
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String solrUrl) {
		this.serviceUrl = solrUrl;
		if (StringUtil.isNotBlank(serviceUrl)) {
			this.solrClient = new HttpSolrClient(serviceUrl);
		} else {
			close();
		}
	}

	@Override
	public void destroy() throws Exception {

		close();

	}

	public void close() {
		if (this.solrClient != null) {
			try {
				this.solrClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.solrClient = null;
		}
	}
}
