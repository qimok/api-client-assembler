package com.qimok.api.client.config.logger;

import com.qimok.api.client.Constants;
import feign.Request;
import feign.Response;
import feign.Util;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class XrFeignLogger extends feign.Logger {
    public XrFeignLogger() {
        this(feign.Logger.class);
    }

    private Logger log;

    public XrFeignLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    XrFeignLogger(Logger logger) {
        this.log = logger;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        JSONObject object = new JSONObject();
        object.put(Constants.CLIENT_NAME,configKey);
        object.put(Constants.FORMAT,format);
        object.put(Constants.ARGS,args);
        writeLog(object,configKey, Constants.LOG_TYPE_DEFAULT);
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        JSONObject object = new JSONObject();
        object.put(Constants.LOG_TYPE_KEY, Constants.LOG_TYPE_REQUEST);
        object.put(Constants.CLIENT_NAME, configKey);
        object.put(Constants.HEADS, request.headers());
        object.put(Constants.METHOD, request.httpMethod());
        object.put(Constants.URL, request.url());
        try {
            object.put(Constants.BODY, JSONObject.wrap(request.requestBody().asString()));
        } catch (Exception e){
            object.put(Constants.BODY, request.requestBody().asString());
        }
        writeLog(object,configKey, Constants.LOG_TYPE_REQUEST);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel,
                                              Response response, long elapsedTime) throws IOException {
        JSONObject object = new JSONObject();
        object.put(Constants.LOG_TYPE_KEY, Constants.LOG_TYPE_RESPONSE);
        object.put(Constants.URL,response.request().url());
        object.put(Constants.CLIENT_NAME, configKey);
        object.put(Constants.HEADS, response.headers());
        object.put(Constants.STATUS,response.status());
        object.put(Constants.ELAPSED_TIME,elapsedTime);
        byte [] bodyData = Util.toByteArray(response.body().asInputStream());
        try {
            object.put(Constants.BODY, JSONObject.wrap(new String(bodyData)));
        } catch (Exception e) {
            object.put(Constants.BODY, new String(bodyData));
        }
        writeLog(object,configKey, Constants.LOG_TYPE_RESPONSE);
        return response.toBuilder().body(bodyData).build();
    }

    private void writeLog(JSONObject object,String configKey,String type) {
        log.info("\n<--- {} {} begin\n {} \n {} {} end --->",
                configKey,type,object.toString(4),configKey,type);
    }

}
