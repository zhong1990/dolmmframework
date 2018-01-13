/**
 *
 */
package org.dol.framework.web.support;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * TODO.
 *
 * @author dolphin
 * @date 2017年5月2日 下午3:15:35
 */
public abstract class AbstractUploader implements Uploader {

    private String defaultSubDir;
    private String baseDir;

    /**
     * @return the defaultSubDir
     */
    public String getDefaultSubDir() {
        return defaultSubDir;
    }

    /**
     * @param defaultSubDir the defaultSubDir to set
     */
    public void setDefaultSubDir(String defaultSubDir) {
        this.defaultSubDir = defaultSubDir;
    }

    /**
     * @return the baseDir
     */
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * @param baseDir the baseDir to set
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    /*
     * (non-Javadoc)
     * @see org.dol.framework.web.support.Uploader#upload(java.lang.String,
     * byte[])
     */
    @Override
    public String upload(String fileName, byte[] data) throws Exception {
        return upload(fileName, new ByteArrayInputStream(data), data.length, defaultSubDir);
    }

    /*
     * (non-Javadoc)
     * @see org.dol.framework.web.support.Uploader#upload(java.lang.String,
     * byte[], java.lang.String)
     */
    @Override
    public String upload(String fileName, byte[] data, String subDir) throws Exception {
        return upload(fileName, new ByteArrayInputStream(data), data.length, subDir);
    }

    /*
     * (non-Javadoc)
     * @see org.dol.framework.web.support.Uploader#upload(java.lang.String,
     * java.io.InputStream, java.lang.Integer)
     */
    @Override
    public String upload(String fileName, InputStream fileSteam, Integer contentLength) throws Exception {
        return upload(fileName, fileSteam, contentLength, defaultSubDir);
    }

    /*
     * (non-Javadoc)
     * @see org.dol.framework.web.support.Uploader#upload(java.lang.String,
     * java.io.InputStream, java.lang.Integer, java.lang.String)
     */
    @Override
    public abstract String upload(String fileName, InputStream fileSteam, Integer contentLength, String subDir) throws Exception;

}
