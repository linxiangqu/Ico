package com.nbank.study.util;

import java.net.DatagramPacket;

/**
 * Created by admin on 2015/5/8 0008.
 */
public interface UdpCallback {
    void receive(DatagramPacket packet);


    void createfail(UdpSocket _udpSocket);

    void createSuccess(UdpSocket _udpSocket);
}
