package com.atcwl.common.interfaces.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 服务权重
 *
 * @author: WuChengXing
 * @create: 2022-05-12 19:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceWeight {
    public String url;
    public Integer weight;
    public Integer curWeight;
}
