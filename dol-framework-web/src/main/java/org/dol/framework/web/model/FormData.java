/**
 * dol-gc-web
 * FormData.java
 * org.dol.gc.web.common
 * TODO
 *
 * @author dolphin
 * @date 2016年1月20日 下午4:07:00
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.web.model;

import java.io.IOException;
import java.io.InputStream;

import org.dol.framework.util.FileUtil;

/**
 * ClassName:FormData <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年1月20日 下午4:07:00 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class FormData {

    private String name;
    private String value;
    private byte[] data;
    private Boolean isFile;
    private String fileName;
    private InputStream inputStream;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {

        if (data == null && inputStream != null) {
            try {
                data = FileUtil.input2byte(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Boolean getIsFile() {
        return isFile != null && isFile.booleanValue();
    }

    public void setIsFile(Boolean isFile) {
        this.isFile = isFile;
    }

    /**
     * fileName.
     *
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * fileName.
     *
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * inputStream.
     *
     * @return the inputStream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * inputStream.
     *
     * @param inputStream
     *            the inputStream to set
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * value.
     *
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
