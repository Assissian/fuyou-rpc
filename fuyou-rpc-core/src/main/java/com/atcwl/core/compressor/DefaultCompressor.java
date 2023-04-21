package com.atcwl.core.compressor;

import com.atcwl.common.interfaces.Compressor;

/**
 * 默认压缩方式，就是不压缩
 * 可以不写该实现类，不过需要额外的判断，对于无需压缩的数据不进行压缩
 * @Author cwl
 * @date
 * @apiNote
 */
public class DefaultCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] target) {
        return new byte[0];
    }

    @Override
    public byte[] uncompress(byte[] target) {
        return new byte[0];
    }
}
