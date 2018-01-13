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

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import org.dol.framework.logging.Logger;
import org.dol.message.MessageInfo;

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
public class ClientLogFilter implements Filter {

	private static final String DOT_SP = ".";
	private static final Logger LOGGER = Logger.getLogger(ClientLogFilter.class);

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		Result result = null;
		RpcException rpcException = null;
		final String api = invoker.getInterface().getName() + DOT_SP + invocation.getMethodName();
		String rid = LOGGER.getRid();
		invocation.getAttachments().put(Logger.REQUEST_ID_KEY, rid);
		final Object[] params = invocation.getArguments();
		long startTime = LOGGER.logApiRequest(api, params);
		try {

			result = invoker.invoke(invocation);
			return result;
		} catch (RpcException ex) {
			rpcException = ex;
			throw rpcException;
		} finally {
			logApiResponse(api, params, startTime, result, rpcException);
		}
	}

	private void logApiResponse(final String api, Object[] params, long startTime, Result result, RpcException rpcException) {
		int status = -1;
		String returnMessage = null;
		if (result != null) {
			if (result.getValue() instanceof MessageInfo) {
				@SuppressWarnings("rawtypes")
				MessageInfo messageInfo = (MessageInfo) result.getValue();
				status = messageInfo.getStatus();
				returnMessage = messageInfo.getMessage();
			}
		}
		if (rpcException != null) {
			returnMessage = LOGGER.getDetailMessage(rpcException);
		}
		LOGGER.logApiResponse(api, params, status, returnMessage, startTime, null);
	}
}
