package cn.j8.es;

import cn.j8.json.Json;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.lang.reflect.Field;

public class MappingBuilder {
    public static Json properties(Class cls){
        Json json = Json.object();
        Field[] fields = cls.getDeclaredFields();
        for(Field f : fields){
            MappingField annotation = f.getAnnotation(MappingField.class);
            if(annotation == null)
                continue;
            if(StringUtils.isBlank(annotation.type()))
                continue;

            Json props = Json.object("type", annotation.type());
            if(StringUtils.isNotBlank(annotation.format())){
                props.asObj().put("format", annotation.format());
            }
            if(StringUtils.isNotBlank(annotation.analyzer())){
                props.asObj().put("analyzer", annotation.analyzer());
            }
            if(StringUtils.isNotBlank(annotation.search_analyzer())){
                props.asObj().put("search_analyzer", annotation.search_analyzer());
            }

            json.asObj().put(f.getName(), props);
        }
        return json;
    }

    public static XContentBuilder data(Object obj){
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            for (Field f : fields) {
                boolean isAcc = f.isAccessible();
                if (!isAcc)
                    f.setAccessible(true);

                try {
                    builder.field(f.getName(), f.get(obj));
                }catch (IllegalAccessException e){
                    //ignore this field
                }finally {
                    f.setAccessible(isAcc);
                }
            }
            builder.endObject();
            return builder;
        }catch(IOException e){
            return null;
        }
    }
}
