package com.qimok.api.client.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.TimeZone;

/**
 * @author qimok
 * @since 2019-05-12
 */
public class JsonObjectMapper extends ObjectMapper {

    public static JsonObjectMapper create() {
        return new JsonObjectMapper();
    }

    public JsonObjectMapper() {
        this(null, null, null);
    }

    public JsonObjectMapper(JsonFactory jsonFactory,
                            DefaultSerializerProvider serializerProvider,
                            DefaultDeserializationContext deserializationContext) {
        super(jsonFactory, serializerProvider, deserializationContext);
        init();
    }

    private void init() {
        setTimeZone(TimeZone.getDefault());
        setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

}
