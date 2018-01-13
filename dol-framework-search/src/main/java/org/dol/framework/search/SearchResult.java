/**
 * dol-framework-search 
 * SearchResult.java 
 * org.dol.framework.search 
 * TODO  
 * @author dolphin
 * @date   2015年12月15日 下午3:00:42 
 * @Copyright 2015, 唯创国际 幸福9号 All Rights Reserved. 
 * @version   1.0
 */

package org.dol.framework.search;


/**
 * ClassName:SearchResult <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年12月15日 下午3:00:42 <br/>
 * 
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class SearchResult {

	private SearchError error;
	private SearchResponse response;

	/**
	 * success.
	 *
	 * @return the success
	 */
	public boolean isSuccess() {
		return getError() == null;
	}

	/**
	 * error.
	 *
	 * @return the error
	 */
	public SearchError getError() {
		return error;
	}

	/**
	 * error.
	 *
	 * @param error
	 *            the error to set
	 */
	public void setError(SearchError error) {
		this.error = error;
	}

	/**
	 * response.
	 *
	 * @return the response
	 */
	public SearchResponse getResponse() {
		return response;
	}

	/**
	 * response.
	 *
	 * @param response
	 *            the response to set
	 */
	public void setResponse(SearchResponse response) {
		this.response = response;
	}
}
