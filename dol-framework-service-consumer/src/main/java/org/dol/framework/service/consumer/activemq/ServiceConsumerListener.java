package org.dol.framework.service.consumer.activemq;

import org.dol.framework.logging.Logger;
import org.dol.framework.queue.JobMessage;
import org.dol.framework.queue.activemq.MessageListenerBase;
import org.dol.framework.service.consumer.common.ExecuteTask;

public class ServiceConsumerListener extends MessageListenerBase {

	public static final Logger LOGGER = Logger.getLogger(ServiceConsumerListener.class);

	private ExecuteTask executeTask;

	public ExecuteTask getExecuteTask() {
		return executeTask;
	}

	public void setExecuteTask(ExecuteTask executeTask) {
		this.executeTask = executeTask;
	}

	@Override
	protected void processMessage(JobMessage jobMessage) {
		executeTask.doWork(jobMessage, jobMessage.getDestinationName());
	}
}
