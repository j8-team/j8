package cn.j8.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public abstract class Json{
    public static Json object(){
        return object(null);
    }

    public static Json object(Object... kvs){
        if(kvs == null)
            return new JsonObj();
        if(kvs.length % 2 != 0)
            throw new JSONException("传入了不完整的键值对");

        JsonObj obj = new JsonObj();
        for(int i=0; i<kvs.length; ++i){
            obj.put(String.valueOf(kvs[i]), kvs[++i]);
        }
        return obj;
    }

    public static Json array(){
        return array(null);
    }

    public static Json array(Object... vals){
        if(vals == null)
            return new JsonArray();
        JsonArray arr = new JsonArray();
        for(int i=0; i<vals.length; ++i){
            arr.add(vals[i]);
        }
        return arr;
    }

    public static Json parse(String text){
        if(text == null || text.trim().length() == 0)
            return null;
        if(text.startsWith("{"))
            return new JsonObj(text);
        if(text.startsWith("["))
            return new JsonArray(text);
        return null;
    }

    public JsonObj asObj(){
        return (JsonObj)this;
    }

    public JsonArray asArray(){
        return (JsonArray)this;
    }

    public abstract boolean isObject();
    public abstract boolean isArray();
    public abstract int size();

}
