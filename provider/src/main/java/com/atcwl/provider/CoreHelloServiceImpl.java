package com.atcwl.provider;

import com.atcwl.api.common.service.CoreHelloService;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public class CoreHelloServiceImpl implements CoreHelloService {
    @Override
    public String sayHello(String name) {
        return "Hello: " + name;
    }
}
