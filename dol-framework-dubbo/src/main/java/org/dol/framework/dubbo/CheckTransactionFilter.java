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

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import org.dol.framework.data.MapperFactory;

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
public class CheckTransactionFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		invocation.getAttachments().put("uniqeInvokeId", String.valueOf(System.currentTimeMillis()));
		Result result = invoker.invoke(invocation);
		if (MapperFactory.checkAndRollbackTransaction()) {
			throw new RpcException("found un complete transaction");
		}
		return result;
	}
}
