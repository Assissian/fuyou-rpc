package com.atcwl.common.interfaces;

import com.atcwl.common.annotation.FuyouRpcSPI;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
@FuyouRpcSPI("default")
public interface Compressor {
    /**
     * 压缩
     * @param target
     * @return
     */
    byte[] compress(byte[] target);
    /**
     * 解压缩
     * @param target
     * @return
     */
    byte[] uncompress(byte[] target);
}
