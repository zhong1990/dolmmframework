/**
 *
 */
package org.dol.framework.web.support;

import java.io.InputStream;

/**
 * TODO.
 *
 * @author dolphin
 * @date 2017年5月2日 下午4:04:51
 */
public interface Uploader {

    String upload(String fileName, byte[] data) throws Exception;

    String upload(String fileName, byte[] data, String dir) throws Exception;

    String upload(String fileName, InputStream fileSteam, Integer contentLength) throws Exception;

    String upload(String fileName, InputStream fileSteam, Integer contentLength, String dir) throws Exception;

}