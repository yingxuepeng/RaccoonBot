package com.raccoon.qqbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 千里夜雨
 */
@Component
@ConfigurationProperties("group.function")
public class GroupFunctionConfig {
    /**
     * 指定从id到config选择之间的关系
     */
    public Map<Long, String> configs;

    /**
     * 指定各个config支持多少的方法
     */
    public Map<String, List<Integer>> functions;

    public Map<Long, String> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<Long, String> configs) {
        this.configs = configs;
    }

    public Map<String, List<Integer>> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, List<Integer>> functions) {
        this.functions = functions;
    }
}
