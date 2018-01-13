package org.dol.framework.config;

import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {

	static final ConcurrentHashMap<String, String> ALL_PROPERTIES = new ConcurrentHashMap<String, String>();

	/**
	 * 获取property值
	 * 
	 * @param propertyName
	 * @return String
	 * @throws
	 * @author dolphin
	 * @since JDK 1.7
	 * @date 2016年4月29日 上午10:56:58
	 */
	public static String getProperty(String propertyName) {

		return ALL_PROPERTIES.get(propertyName);
	}

}
