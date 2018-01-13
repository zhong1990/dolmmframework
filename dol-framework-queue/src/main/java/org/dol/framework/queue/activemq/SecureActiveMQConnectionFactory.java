package org.dol.framework.queue.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.InitializingBean;

import org.dol.framework.security.AESCoder;
import org.dol.framework.security.AESCoder.AESKeySize;
import org.dol.framework.util.StringUtil;

/**
 * 安全的 ActiveMQConnectionFactory，当设置key属性时，则表示密码已经加密
 * 
 * @author qiyazhong
 * @date 2015年7月30日 下午5:14:39
 * @version 1.0
 */
public class SecureActiveMQConnectionFactory extends ActiveMQConnectionFactory
		implements InitializingBean {
	private static final AESCoder AESCODER = new AESCoder(AESKeySize.size_128);
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtil.isNotBlank(key)) {
			setPassword(AESCODER.decrypt(getPassword(), key));
		}
	}
}
