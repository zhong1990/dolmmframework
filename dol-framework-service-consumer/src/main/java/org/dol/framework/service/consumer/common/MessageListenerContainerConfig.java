/**
 * dol-framework-service-consumer 
 * MessageListenerContainerConfig.java 
 * org.dol.framework.service.consumer.common 
 * TODO  
 * @author dolphin
 * @date   2016年9月14日 下午4:08:51 
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved. 
 * @version   1.0
 */

package org.dol.framework.service.consumer.common;

import java.util.List;

/**
 * ClassName:MessageListenerContainerConfig <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年9月14日 下午4:08:51 <br/>
 * 
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class MessageListenerContainerConfig {

	public String getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(String concurrency) {
		this.concurrency = concurrency;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getFailedQueueName() {
		return failedQueueName;
	}

	public void setFailedQueueName(String failedQueueName) {
		this.failedQueueName = failedQueueName;
	}

	public List<String> getConnectionFactoryNames() {
		return connectionFactoryNames;
	}

	public void setConnectionFactoryNames(List<String> connectionFactoryNames) {
		this.connectionFactoryNames = connectionFactoryNames;
	}

	private List<String> connectionFactoryNames;
	private String concurrency;
	private String failedQueueName;
	private String queueName;

}
