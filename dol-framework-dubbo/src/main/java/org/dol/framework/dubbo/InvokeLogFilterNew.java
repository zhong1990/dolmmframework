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

import java.net.InetSocketAddress;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import org.dol.framework.logging.Logger;
import org.dol.framework.util.StringUtil;
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
public class InvokeLogFilterNew implements Filter {

	private static final String DOT_SP = ".";
	private static final Logger LOGGER = Logger.getLogger(InvokeLogger.class);

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		Result result = null;
		RpcException rpcException = null;
		String invokerName = StringUtil.EMPTY_STRING;
		String invokerIP = StringUtil.EMPTY_STRING;
		final String api = invoker.getInterface().getName() + DOT_SP + invocation.getMethodName();
		RpcContext ssContext = RpcContext.getContext();
		if (ssContext != null) {
			InetSocketAddress socketAddress = ssContext.getRemoteAddress();
			if (socketAddress != null && socketAddress.getAddress() != null) {
				invokerIP = socketAddress.getAddress().getHostAddress();
			}
		}
		final Object[] params = invocation.getArguments();
		String rid = invocation.getAttachment(Logger.REQUEST_ID_KEY);
		long startTime = LOGGER.logIn(rid, invokerName, invokerIP, api, params);
		try {
			result = invoker.invoke(invocation);
			return result;

		} catch (RpcException ex) {
			rpcException = ex;
			throw rpcException;

		} finally {
			logOut(invokerName, invokerIP, api, startTime, result, rpcException);
		}

	}

	private void logOut(String invokerName, String invokerIP, final String api, long startTime, Result result, RpcException rpcException) {

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
		LOGGER.logOut(invokerName, invokerIP, api, status, returnMessage, startTime, null);
	}
}
