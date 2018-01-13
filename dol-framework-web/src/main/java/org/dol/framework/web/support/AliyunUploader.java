/**
 *
 */
package org.dol.framework.web.support;

import java.io.InputStream;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

/**
 * TODO.
 *
 * @author dolphin
 * @date 2017年5月2日 下午3:15:35
 */
public class AliyunUploader extends AbstractUploader {

    private String endpoint;
    private String keyid;
    private String keysecret;

    private OSSClient client;
    private boolean isInit;

    /*
     * (non-Javadoc)
     * @see org.dol.framework.web.support.Uploader#upload(java.lang.String,
     * java.io.InputStream, java.lang.Integer, java.lang.String)
     */
    @Override
    public String upload(String fileName, InputStream fileSteam, Integer contentLength, String subDir) {
        sureInit();
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(contentLength);
        client.putObject(subDir, fileName, fileSteam, meta);
        return getBaseDir() + fileName;
    }

    /**
     * 参照方法名.
     */
    private void sureInit() {
        if (isInit) {
            return;
        }
        client = new OSSClient(endpoint, keyid, keysecret);
        isInit = true;
    }

}
