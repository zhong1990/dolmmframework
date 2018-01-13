/**
 * dol-framework-search 
 * SearchData.java 
 * org.dol.framework.search 
 * TODO  
 * @author dolphin
 * @date   2015年12月15日 下午3:12:50 
 * @Copyright 2015, 唯创国际 幸福9号 All Rights Reserved. 
 * @version   1.0
 */

package org.dol.framework.search;

import java.util.List;
import java.util.Map;

/**
 * ClassName:SearchData <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年12月15日 下午3:12:50 <br/>
 * 
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class SearchResponse {
	private long numFound;
	private long start;
	private List<? extends Map<String, Object>> docs;

	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public List<? extends Map<String, Object>> getDocs() {
		return docs;
	}

	public void setDocs(List<? extends Map<String, Object>> docs) {
		this.docs = docs;
	}
}
