package org.demo.springretry.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "app.retry")
public class ExceptionProperties {

    private Map<String , Integer> exceptions=new HashMap<>();

    public Map<String, Integer> getExceptions() {
        return exceptions;
    }

    public void setExceptions(Map<String, Integer> exceptions) {
        this.exceptions = exceptions;
    }
}
