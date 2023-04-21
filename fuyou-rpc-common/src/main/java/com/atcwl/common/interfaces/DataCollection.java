package com.atcwl.common.interfaces;

import com.atcwl.common.annotation.FuyouRpcSPI;
import com.atcwl.common.interfaces.impl.CollectData;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 数据收集接口
 *
 * @author: WuChengXing
 * @create: 2022-06-13 23:56
 **/
@FuyouRpcSPI
public interface DataCollection {

    /**
     * 数据收集
     *
     * @param collectData
     * @return
     */
    Boolean collect(CollectData collectData);
}
