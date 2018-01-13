/**
 *
 */
package org.dol.framework.web.support;

import java.io.IOException;
import java.io.InputStream;

import org.dol.framework.util.FileUtil;

/**
 * TODO.
 *
 * @author dolphin
 * @date 2017年5月2日 下午4:04:51
 */
public class FileUploader extends AbstractUploader {

    /*
     * (non-Javadoc)
     * @see
     * org.dol.framework.web.support.AbstractUploader#upload(java.lang.String,
     * java.io.InputStream, java.lang.Integer, java.lang.String)
     */
    @Override
    public String upload(String fileName, InputStream fileSteam, Integer contentLength, String subDir) throws Exception {

        String fullFileName = FileUtil.buildFullFileName(getBaseDir(), subDir, fileName);
        FileUtil.write(fileSteam, fullFileName);
        return fullFileName;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.dol.framework.web.support.AbstractUploader#upload(java.lang.String,
     * byte[], java.lang.String)
     */
    @Override
    public String upload(String fileName, byte[] data, String subDir) throws IOException {
        String fullFileName = FileUtil.buildFullFileName(getBaseDir(), subDir, fileName);
        FileUtil.writeUseNio(fullFileName, data);
        return fullFileName;
    }

}