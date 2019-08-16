package cn.j8.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonObj extends Json {
    JSONObject obj;

    public JsonObj() {
        obj = new JSONObject();
    }

    public JsonObj(String json) {
        obj = JSONObject.parseObject(json);
    }

    JsonObj(JSONObject json) {
        obj = json;
    }

    public JsonObj put(String key, Object value) {
        if (value instanceof JsonObj) {
            obj.put(key, ((JsonObj) value).obj);
        } else if (value instanceof JsonArray) {
            obj.put(key, ((JsonArray) value).arr);
        } else {
            obj.put(key, value);
        }
        return this;
    }

    @Override
    public String toString() {
        return obj.toString();
    }

    public int intVal(String key) {
        return obj.getIntValue(key);
    }

    public int intVal(String key, int defaultValue) {
        return obj.containsKey(key) ? obj.getIntValue(key) : defaultValue;
    }

    public long longVal(String key) {
        return obj.getLongValue(key);
    }

    public long longVal(String key, long defaultValue) {
        return obj.containsKey(key) ? obj.getLongValue(key) : defaultValue;
    }

    public String strVal(String key) {
        return obj.getString(key);
    }

    public float floatVal(String key) {
        return obj.getFloatValue(key);
    }

    public float floatVal(String key, float defaultValue) {
        return obj.containsKey(key) ? obj.getFloatValue(key) : defaultValue;
    }

    public double doubleVal(String key) {
        return obj.getDoubleValue(key);
    }

    public double doubleVal(String key, double defaultValue) {
        return obj.containsKey(key) ? obj.getDoubleValue(key) : defaultValue;
    }

    public <T> T val(String key) {
        Object t = obj.get(key);
        if (t instanceof JSONObject)
            return (T) (new JsonObj((JSONObject) t));
        if (t instanceof JSONArray)
            return (T) (new JsonArray((JSONArray) t));
        return (T) t;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public boolean isArray() {
        return false;
    }
}
