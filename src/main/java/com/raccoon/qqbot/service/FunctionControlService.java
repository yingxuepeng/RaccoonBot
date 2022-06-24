package com.raccoon.qqbot.service;

import com.raccoon.qqbot.config.GroupFunctionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.raccoon.qqbot.data.action.UserAction.*;

@Service
public class FunctionControlService {

    @Autowired
    GroupFunctionConfig groupFunctionConfig;

    Map<Long, String> groupToConfigMap;
    final Map<String, Set<Type>> configToFunctionMap = new HashMap<>();

    @PostConstruct
    public void init(){
        groupToConfigMap = groupFunctionConfig.configs;
        Map<String, List<Integer>> map = groupFunctionConfig.functions;
        for(Map.Entry<String, List<Integer>> e:map.entrySet()){
            String configId = e.getKey();
            List<Integer> v = e.getValue();
            Set<Type> types = new HashSet<>();
            v.forEach(o -> types.add(Type.valueOf(o)));
            configToFunctionMap.put(configId, types);
        }
    }

    private final static String DEFAULT_CONFIG_NAME = "config0";
    public boolean checkGroupSupportThisFunction(Long groupId, Type type){
        String config = groupToConfigMap.get(groupId);
        if (config == null){
            config = DEFAULT_CONFIG_NAME;
        }
        Collection<Type> types = configToFunctionMap.get(config);
        if (types == null){
            // 没有定义该配置
            return false;
        }else{
            return types.contains(type);
        }
    }
}
