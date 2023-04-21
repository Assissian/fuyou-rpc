package com.atcwl.core.compressor;

import cn.hutool.core.lang.Assert;
import com.atcwl.common.interfaces.Compressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip 方式压缩
 * @Author cwl
 * @date
 * @apiNote
 */
public class GzipCompressor implements Compressor {
    private static final int BUFFER_SIZE = 4096;

    @Override
    public byte[] compress(byte[] target) {
        Assert.notNull("bytes should not be null");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStream(out);) {
            gzipOut.write(target);
            gzipOut.flush();
            gzipOut.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip压缩失败");
        }
    }

    @Override
    public byte[] uncompress(byte[] target) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(target))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gzip.read(buffer)) > -1) {
                out.write(buffer);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip解压缩失败");
        }
    }
}
