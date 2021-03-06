/**
 * dol-gc-service 
 * InvokeLogFilter.java 
 * org.dol.gc.service.dubbo 
 * TODO  
 * @author dolphin
 * @date   2015年10月22日 下午6:20:15 
 * @Copyright 2015, 唯创国际 幸福9号 All Rights Reserved. 
 * @version   1.0
 */

package org.dol.framework.dubbo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import org.dol.framework.bizlogging.BizLogger;
import org.dol.framework.logging.Logger;
import org.dol.framework.util.ApplicationContextUtil;

/**
 * ClassName:InvokeLogFilter <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年10月22日 下午6:20:15 <br/>
 * 
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class InvokeLogger implements InitializingBean, DisposableBean {
	private static final Logger LOGGER = Logger.getLogger(InvokeLogger.class);
	private static BizLogger bizLogger;

	private final static ExecutorService EXECUTOR_SERVICE;
	static {
		EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
	}

	public static void logInvoke(
			Invoker<?> invoker,
			Invocation invocation,
			final long requestTime,
			final Result result,
			final String invokerHostName,
			final String invokerIP,
			final RpcException rpcException
			) {

		final Object[] arguments = invocation.getArguments();
		final String api = invoker.getInterface().getName() + "." + invocation.getMethodName();
		URL node = invoker.getUrl();
		final String serviceName = invoker.getUrl().getParameter("application");
		final String serverIP = node.getHost();
		final long returnTime = System.currentTimeMillis();

		Throwable lastException = rpcException == null ? LOGGER.getLastException() : rpcException;
		InvokeLogWoker invokeLogWoker = new InvokeLogWoker(
				arguments,
				api,
				serviceName,
				serverIP,
				invokerHostName,
				invokerIP,
				requestTime,
				returnTime,
				result,
				lastException,
				bizLogger);

		EXECUTOR_SERVICE.execute(invokeLogWoker);

	}

	@Override
	public void destroy() throws Exception {
		EXECUTOR_SERVICE.shutdown();
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		bizLogger = ApplicationContextUtil.getBean(BizLogger.class);

	}

}
