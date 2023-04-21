package com.atcwl.common.interfaces;

import com.atcwl.common.annotation.FuyouRpcSPI;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
@FuyouRpcSPI(value = "simple-rpc-property")
public interface ConfigLoader {
    /**
     * 加载配置项
     *
     * @param key 配置的 key
     * @return 配置项的值，如果不存在，返回 null
     */
    String loadConfigItem(String key);
}
