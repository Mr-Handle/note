package com.handle.jackson.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author handle
 * @date 2024-09-29
 */
@Configuration
public class JacksonConfiguration {
    private static final Pattern FORWARD_SLASH_YYYY_MM_DD_PATTERN = Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}$");

    private static final Pattern HYPHEN_YYYY_MM_DD_PATTERN = Pattern.compile("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");

    private static final DateTimeFormatter FORWARD_SLASH_YYYY_MM_DD_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final DateTimeFormatter HYPHEN_YYYY_MM_DD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * 定义Date转换器，用于转换@RequestParam和@PathVariable参数
     * 不能用lambda表达式取代Converter内部类的写法，不然会报类型匹配问题
     */
    @Bean
    public Converter<String, Date> DateConverter() {
        return new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                Instant instant = Instant.ofEpochMilli(Long.parseLong(source));
                return Date.from(instant);
            }
        };
    }

    /**
     * 定义LocalDate转换器，用于转换@RequestParam和@PathVariable参数
     * 不能用lambda表达式取代Converter内部类的写法，不然会报类型匹配问题
     */
    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                Instant instant = Instant.ofEpochMilli(Long.parseLong(source));
                return LocalDate.ofInstant(instant, ZoneId.systemDefault());
            }
        };
    }

    /**
     * 定义OffsetDateTime转换器，用于转换@RequestParam和@PathVariable参数
     * 不能用lambda表达式取代Converter内部类的写法，不然会报类型匹配问题
     */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                Instant instant = Instant.ofEpochMilli(Long.parseLong(source));
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
        };
    }

    /**
     * 定义OffsetDateTime转换器，用于转换@RequestParam和@PathVariable参数
     * 不能用lambda表达式取代Converter内部类的写法，不然会报类型匹配问题
     */
    @Bean
    public Converter<String, OffsetDateTime> offsetDateTimeConverter() {
        return new Converter<String, OffsetDateTime>() {
            @Override
            public OffsetDateTime convert(String source) {
                Instant instant = Instant.ofEpochMilli(Long.parseLong(source));
                return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
        };
    }

    /**
     * 自定义一些类型的序列化器和反序列化器，其它的还是用系统默认的配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // js的最大值为2^53-1，当返回前端的Long类型的数值位数超过这个值时，会丢失精度
            // 因此对Long类型序列化为字符串，保证数据准确性
            builder.serializerByType(Long.class, new LongSerializer());
            builder.deserializerByType(Long.class, new LongDeserializer());

            builder.serializerByType(LocalDate.class, new LocalDateSerializer());
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer());

            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer());
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer());

            builder.serializerByType(OffsetDateTime.class, new OffsetDateTimeSerializer());
            builder.deserializerByType(OffsetDateTime.class, new OffsetDateTimeDeserializer());
        };
    }

    private static class LongSerializer extends JsonSerializer<Long> {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (Objects.nonNull(value)) {
                gen.writeString(value.toString());
            }
        }
    }

    private static class LongDeserializer extends JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return p.getValueAsLong();
        }
    }

    private static class LocalDateSerializer extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (Objects.nonNull(value)) {
                gen.writeString(HYPHEN_YYYY_MM_DD_FORMATTER.format(value));
            }
        }
    }

    private static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            if (StringUtils.isNotBlank(value)) {
                if (FORWARD_SLASH_YYYY_MM_DD_PATTERN.matcher(value.trim()).matches()) {
                    return LocalDate.parse(value, FORWARD_SLASH_YYYY_MM_DD_FORMATTER);
                } else if (HYPHEN_YYYY_MM_DD_PATTERN.matcher(value.trim()).matches()) {
                    return LocalDate.parse(value, HYPHEN_YYYY_MM_DD_FORMATTER);
                }
            }
            return null;
        }
    }

    private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (Objects.nonNull(value)) {
                long timestamp = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                gen.writeNumber(timestamp);
            }
        }
    }

    private static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            long timestamp = p.getValueAsLong();
            if (timestamp > 0) {
                Instant instant = Instant.ofEpochMilli(timestamp);
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            return null;
        }
    }

    private static class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
        @Override
        public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (Objects.nonNull(value)) {
                long timestamp = value.toInstant().toEpochMilli();
                gen.writeNumber(timestamp);
            }
        }
    }

    private static class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
        @Override
        public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            long timestamp = p.getValueAsLong();
            if (timestamp > 0) {
                Instant instant = Instant.ofEpochMilli(timestamp);
                return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            return null;
        }
    }
}