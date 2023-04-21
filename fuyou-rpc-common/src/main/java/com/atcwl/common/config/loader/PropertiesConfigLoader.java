package com.atcwl.common.config.loader;

import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import com.atcwl.common.interfaces.ConfigLoader;
import com.atcwl.common.interfaces.ConfigLoader;
//import com.atcwl.common.util.SimpleRpcLog;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-04-30 10:36
 **/
public class PropertiesConfigLoader implements ConfigLoader {

    private Setting setting = null;

    public PropertiesConfigLoader() {
        try {
            setting = SettingUtil.get("simple-rpc.properties");
        } catch (NoResourceException ex) {
            //SimpleRpcLog.info("Config file 'simple-rpc.properties' not exist!");
        }
    }


    @Override
    public String loadConfigItem(String key) {
        if (setting == null) {
            return null;
        }
        return setting.getStr(key);
    }
}
