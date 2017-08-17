package com.handsome.robot.Activity.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Joern on 2017/08/16.
 */

public class Triangle {

    private FloatBuffer vertexBuffer;

    // 数组中每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // 按逆时针方向顺序:
            0.0f,  0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };

    // 设置颜色，分别为red, green, blue 和alpha (opacity)
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Triangle() {
        // 为存放形状的坐标，初始化顶点字节缓冲
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (坐标数 * 4)float占四字节
                triangleCoords.length * 4);
        // 设用设备的本点字节序
        bb.order(ByteOrder.nativeOrder());
        // 从ByteBuffer创建一个浮点缓冲
        vertexBuffer = bb.asFloatBuffer();
        // 把坐标们加入FloatBuffer中
        vertexBuffer.put(triangleCoords);
        // 设置buffer，从第一个坐标开始读
        vertexBuffer.position(0);
    }
}
