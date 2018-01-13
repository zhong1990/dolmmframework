/**
 * 
 */
package org.dol.framework.web.common;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.entity.ContentType;
import org.dol.framework.util.FileUtil;
import org.dol.framework.web.model.FormData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * 获取上传文件表单信息.
 * 
 * @author dolphin
 * @date 2017年4月5日 上午10:14:39
 */
public class WebFormUtil {

    /**
     * 
     */
    private static final String DEFAULT_CHARSET = "utf-8";

    public static List<FormData> getUploadFormData(
            HttpServletRequest request,
            HttpServletResponse response) throws FileUploadException, IOException {

        List<FormData> formDatas = new ArrayList<FormData>();
        request.setCharacterEncoding(DEFAULT_CHARSET);
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);
        Iterator<FileItem> itr = items.iterator();
        while (itr.hasNext()) {
            FileItem item = itr.next();
            FormData formData = new FormData();
            if (item.isFormField()) {
                formData.setName(item.getFieldName());
                formData.setValue(item.getString());

            } else {
                formData.setName(item.getFieldName());
                formData.setFileName(item.getName());
                formData.setIsFile(true);
                InputStream inputStream = item.getInputStream();
                formData.setInputStream(inputStream);

                // formData.setData(bytes);
            }
            formDatas.add(formData);
        }
        return formDatas;
    }

    public static Form getForm(
            HttpServletRequest request,
            HttpServletResponse response) throws FileUploadException, IOException {

        ContentType contentType = ContentType.parse(request.getContentType());
        String mineType = contentType.getMimeType();
        String charset = contentType.getCharset() == null ? DEFAULT_CHARSET : contentType.getCharset().name();
        if (ContentType.MULTIPART_FORM_DATA.getMimeType().equals(mineType)) {
            return resolveMultipartFormData(request, charset);
        }
        if (ContentType.APPLICATION_FORM_URLENCODED.getMimeType().equals(mineType)) {
            return resolveFormUrlEncodeData(request, charset);
        }
        if (ContentType.APPLICATION_JSON.getMimeType().equals(mineType)) {
            return resolveJSONData(request, charset);
        }
        return null;
    }

    /**
     * 参照方法名.
     *
     * @param request
     * @param charset
     * @return
     * @throws IOException
     */
    private static Form resolveJSONData(HttpServletRequest request, String charset) throws IOException {
        Form form = new Form();
        byte[] data = FileUtil.input2byte(request.getInputStream());
        try {
            String json = new String(data, charset);
            form.setBody(json);
        } catch (UnsupportedEncodingException e) {
            // never happend
        }
        return form;

    }

    /**
     * 参照方法名.
     *
     * @param request
     * @param charset
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static Form resolveFormUrlEncodeData(HttpServletRequest request, String charset) {
        Enumeration e = request.getParameterNames();
        Form form = new Form();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            FormData formData = new FormData();
            form.put(name, formData);
            formData.setName(name);
            String value = request.getParameter(name);
            if (value != null) {
                try {
                    formData.setValue(URLDecoder.decode(value, charset));
                } catch (UnsupportedEncodingException e1) {
                    // never happened
                }
            }
        }
        return form;
    }

    /**
     * 参照方法名.
     *
     * @param request
     * @param form
     * @throws FileUploadException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private static Form resolveMultipartFormData(HttpServletRequest request, String charset) throws FileUploadException, UnsupportedEncodingException, IOException {
        Form form = new Form();
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);
        Iterator<FileItem> itr = items.iterator();
        while (itr.hasNext()) {
            FileItem item = itr.next();
            FormData formData = new FormData();
            if (item.isFormField()) {
                formData.setName(item.getFieldName());
                formData.setValue(item.getString(charset));

            } else {
                formData.setName(item.getFieldName());
                formData.setFileName(item.getName());
                formData.setIsFile(true);
                InputStream inputStream = item.getInputStream();
                formData.setInputStream(inputStream);
                // byte[] bytes = FileUtil.input2byte(inputStream);
                // formData.setData(bytes);
            }
            form.put(formData.getName(), formData);
        }
        return form;
    }

}
