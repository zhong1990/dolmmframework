package org.dol.framework.data;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.dol.framework.config.ConfigManager;
import org.dol.framework.logging.Logger;
import org.dol.framework.util.StringUtil;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class SqlTimeInterceptor2 implements Interceptor {

    private static final Logger LOGGER = Logger.getLogger(SqlTimeInterceptor2.class);
    private volatile boolean isInit = false;
    private int minTime = 1000;
    private boolean enable;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        long start = System.currentTimeMillis();
        Object returnValue = invocation.proceed();
        long end = System.currentTimeMillis();
        if ((end - start) > minTime) {
            String sqlId = getSql(invocation);
            LOGGER.metric(sqlId, null, start);
        }
        return returnValue;
    }

    private String getSql(Invocation invocation) {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        String sqlId = mappedStatement.getId();
        return sqlId;
    }

    @Override
    public Object plugin(Object target) {
        if (enable) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        if (isInit) {
            return;
        }
        isInit = true;
        String strMinTime = properties.getProperty("minTime");
        if (StringUtil.isNotBlank(strMinTime)) {
            minTime = Integer.valueOf(strMinTime);
        }

        strMinTime = ConfigManager.getProperty("sql.metric.minTime");
        if (StringUtil.isNotBlank(strMinTime)) {
            minTime = Integer.valueOf(strMinTime);
        }

        String enableStr = properties.getProperty("enable");
        if (StringUtil.isNotBlank(enableStr)) {
            enable = Boolean.valueOf(enableStr);
        }
        enableStr = ConfigManager.getProperty("sql.metric.enable");
        if (StringUtil.isNotBlank(enableStr)) {
            enable = Boolean.valueOf(enableStr);
        }
    }
}
