package eu.h2020.sc.protocol;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {
    private Map<String, String> parameters = null;

    private QueryParameter(String name, String value) {
        this.parameters = new HashMap<String, String>();
        this.parameters.put(name, value);
    }

    public static QueryParameter with(String name, String value) {
        return new QueryParameter(name, value);
    }

    public QueryParameter and(String name, String value) {
        this.parameters.put(name, value);
        return this;
    }

    public Map<String, String> parameters() {
        return this.parameters;
    }
}
