package com.atcwl.common.config.loader;

import com.atcwl.common.interfaces.ConfigLoader;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 默认的spi加载器
 *
 * @author: WuChengXing
 * @create: 2022-06-19 01:05
 **/
public class SpiPropertiesConfigLoader implements ConfigLoader {

    @Override
    public String loadConfigItem(String key) {
        return key;
    }
}
