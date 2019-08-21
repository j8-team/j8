package cn.j8.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonArray extends Json {
    JSONArray arr;

    public JsonArray() {
        arr = new JSONArray();
    }

    public JsonArray(String json) {
        arr = JSONArray.parseArray(json);
    }

    JsonArray(JSONArray json) {
        this.arr = json;
    }

    public JsonArray add(Object value) {
        if (value instanceof JsonObj) {
            arr.add(((JsonObj) value).obj);
        } else if (value instanceof JsonArray) {
            arr.add(((JsonArray) value).arr);
        } else {
            arr.add(value);
        }
        return this;
    }

    @Override
    public String toString() {
        return arr.toString();
    }

    public int intVal(int idx) {
        return arr.getIntValue(idx);
    }

    public long longVal(int idx) {
        return arr.getLongValue(idx);
    }

    public String strVal(int idx) {
        return arr.getString(idx);
    }

    public float floatVal(int idx) {
        return arr.getFloatValue(idx);
    }

    public double doubleVal(int idx) {
        return arr.getDoubleValue(idx);
    }

    public <T> T val(int idx, boolean raw) {
        Object t = arr.get(idx);
        if (t instanceof JSONObject && !raw)
            return (T) (new JsonObj((JSONObject) t));
        if (t instanceof JSONArray && !raw)
            return (T) (new JsonArray((JSONArray) t));
        return (T) t;
    }

    public <T> T val(int idx) {
        return val(idx, false);
    }

    @Override
    public int size() {
        return arr.size();
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }
}
