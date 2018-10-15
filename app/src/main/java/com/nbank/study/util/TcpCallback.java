package com.nbank.study.util;

/**
 * Created by admin on 2015/5/8 0008.
 */
public interface TcpCallback {
    /**
     * 当接收到数据时调用该回调函数
     *
     * @param buffer
     */
    void receive(byte[] buffer);

    /**
     * 当TCP连接失败次数超过设置时触发
     */
    void connectFail(TcpSocket tcpSocket);

    /**
     * 当TCP连接成功时触发
     */
    void connectSuccess(TcpSocket tcpSocket);


    /**
     * 数据发送成功时触发
     *
     * @param tcpSocket
     * @param buffer
     */
    void sendSuccess(TcpSocket tcpSocket, byte[] buffer);

    /**
     * 当Tcp连接因IO异常被断开时
     *
     * @param tcpSocket
     */
    void connectDisconnect(TcpSocket tcpSocket);
}
