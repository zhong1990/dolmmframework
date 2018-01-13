/**
 * dol-framework-service-consumer 
 * MessageListenerContainerManager.java 
 * org.dol.framework.service.consumer.common 
 * TODO  
 * @author dolphin
 * @date   2016年9月14日 下午4:07:21 
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved. 
 * @version   1.0
 */

package org.dol.framework.service.consumer.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.MessageListenerContainer;

import org.dol.framework.logging.Logger;
import org.dol.framework.queue.Producer;
import org.dol.framework.service.consumer.activemq.ServiceConsumerListener;

/**
 * ClassName:MessageListenerContainerManager <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年9月14日 下午4:07:21 <br/>
 * 
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class MessageListenerContainerManager implements InitializingBean, DisposableBean {

	private static final Logger LOGGER = Logger.getLogger(MessageListenerContainerManager.class);
	private List<MessageListenerContainer> messageListenerContainers = new ArrayList<MessageListenerContainer>();

	@Autowired
	private Producer producer;

	private Map<String, ConnectionFactory> connectionFactories;

	@Override
	public void afterPropertiesSet() throws Exception {
		startAll();
	}

	@Override
	public void destroy() throws Exception {
		stopAll();
	}

	public synchronized void startAll() {
		doStartAll();
	}

	private void doStartAll() {
		List<MessageListenerContainerConfig> configs = getConfigs();
		List<MessageListenerContainer> newMessageListenerContainers = new ArrayList<MessageListenerContainer>();
		for (MessageListenerContainerConfig config : configs) {
			List<MessageListenerContainer> messageListenerContainers = buildMessageContainers(config);
			newMessageListenerContainers.addAll(messageListenerContainers);
		}
		messageListenerContainers = newMessageListenerContainers;
	}

	private List<MessageListenerContainerConfig> getConfigs() {

		List<MessageListenerContainerConfig> configs = new ArrayList<MessageListenerContainerConfig>();
		return configs;

	}

	/**
	 * 根据MessageListenerConfig构建一个MessageListener
	 * 
	 * @param config
	 * @return MessageListenerContainerEx
	 * @author dolphin
	 * @since JDK 1.7
	 * @date 2016年9月17日 下午10:07:37
	 */
	private List<MessageListenerContainer> buildMessageContainers(MessageListenerContainerConfig config) {

		List<MessageListenerContainer> list = new ArrayList<MessageListenerContainer>(config.getConnectionFactoryNames().size());
		for (String connectionFactoryName : config.getConnectionFactoryNames()) {
			MessageListenerContainerEx messageListenerContainer = new MessageListenerContainerEx();
			messageListenerContainer.setConnectionFactory(getConnectionFactory(connectionFactoryName));
			messageListenerContainer.setConcurrency(config.getConcurrency());
			messageListenerContainer.setDestinationName(config.getQueueName());
			messageListenerContainer.setMessageListener(getDefaultMessageListener(config));
			messageListenerContainer.setAutoStartup(false);
			messageListenerContainer.initialize();
			list.add(messageListenerContainer);
		}
		return list;
	}

	private ConnectionFactory getConnectionFactory(String connectionFactoryName) {

		return this.connectionFactories.get(connectionFactoryName);
	}

	private Object getDefaultMessageListener(MessageListenerContainerConfig config) {
		ServiceConsumerListener consumerListener = new ServiceConsumerListener();
		ExecuteTask executeTask = new ExecuteTask();
		executeTask.setDefaultFailedQueue(config.getFailedQueueName());
		executeTask.setServiceProducer(producer);
		consumerListener.setExecuteTask(executeTask);
		return consumerListener;
	}

	/**
	 * 停止所有正在运行的MessageListener
	 * 
	 * @author dolphin
	 * @since JDK 1.7
	 * @date 2016年9月17日 下午10:05:16
	 */
	private synchronized void stopAll() {
		doStopAll();
	}

	/**
	 * 实际执行停止所有正在运行的MessageListener
	 * 
	 * @author dolphin
	 * @since JDK 1.7
	 * @date 2016年9月17日 下午10:05:46
	 */
	private void doStopAll() {
		for (MessageListenerContainer messageListenerContainerEx : messageListenerContainers) {
			forceStop(messageListenerContainerEx);
		}
	}

	/**
	 * 停止某一MessageListener,停止发生错误不抛出，记录日志
	 * 
	 * @param messageListenerContainer
	 * 
	 * @author dolphin
	 * @since JDK 1.7
	 * @date 2016年9月17日 下午10:06:18
	 */
	private void forceStop(MessageListenerContainer messageListenerContainer) {
		try {
			messageListenerContainer.stop();
		} catch (Exception e) {
			LOGGER.error("forceStop", e);
		}
	}

}
