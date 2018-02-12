/**
 *
 */
package org.dol.framework.web.support;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * TODO.
 *
 * @author dolphin
 * @date 2017年4月15日 下午2:27:19
 */
public class JsonHttpMessageConverter extends FastJsonHttpMessageConverter {
    private static final SerializerFeature[] QUERY_DEFAULT_SERIALIZER_FEATURES = new SerializerFeature[]{
            SerializerFeature.BrowserSecure
    };

    public JsonHttpMessageConverter() {
        super();
        getFastJsonConfig().setSerializerFeatures(QUERY_DEFAULT_SERIALIZER_FEATURES);
    }


    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        super.writeInternal(obj, outputMessage);
    }


}
