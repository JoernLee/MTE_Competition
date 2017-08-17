package com.handsome.robot.Activity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Joern on 2017/08/16.
 */

public class DrawUtils {

    /**
     * @param vertexs float 数组
     * @return 获取浮点形缓冲数据
     */
    public static FloatBuffer getFloatBuffer(float[] vertexs) {
        FloatBuffer buffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(vertexs.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        buffer = qbb.asFloatBuffer();
        //写入数组
        buffer.put(vertexs);
        //设置默认的读取位置
        buffer.position(0);
        return buffer;
    }

    public static ShortBuffer getShortBuffer(short[] vertexs) {
        ShortBuffer buffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(vertexs.length * 2);
        qbb.order(ByteOrder.nativeOrder());
        buffer = qbb.asShortBuffer();
        //写入数组
        buffer.put(vertexs);
        //设置默认的读取位置
        buffer.position(0);
        return buffer;
    }



}
