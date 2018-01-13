/**
 * dol-framework-security 
 * HexUtil.java 
 * org.dol.framework.security 
 * TODO  
 * @author dolphin
 * @date   2016年5月13日 上午11:03:47 
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved. 
 * @version   1.0
 */

package org.dol.framework.security;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * ClassName:HexUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年5月13日 上午11:03:47 <br/>
 * 
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public abstract class HexUtil {

	public static String hex2Base64(String hex) throws DecoderException {
		byte[] data = hex2Bytes(hex);
		return Base64Util.encodeBase64String(data);

	}

	public static byte[] hex2Bytes(String hex) throws DecoderException {
		char[] data = hex.toCharArray();
		return Hex.decodeHex(data);
	}
}
