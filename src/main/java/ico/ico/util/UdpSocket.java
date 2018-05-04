package ico.ico.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Random;

/**
 * @包名 sunrun.szogemray.util
 * @作者 ico
 * @创建日期 2015/1/20 0020
 * @版本 V 1.0
 */
public class UdpSocket extends IcoThread {
    //常量
    public final static int UDPSOCKET_CREATE_SUCCESS = 100;
    public final static int UDPSOCKET_CREATE_FAIL = 101;
    public final static int UDPSOCKET_RECEIVE = 102;
    public final static int UDPSOCKET_TIMEOUT = 103;
    // 最后一次发送命令的时间
    public static Date lastSendTime;
    // 每次命令需间隔时间
    public static Long interval = 300l;
    /**
     * 实体对象
     */
    //Udp连接对象
    private DatagramSocket socket;
    //UDP发送的端口号
    private int port;
    //TCP连接次数上限，超过该次数将调用回调函数createFail
    private int times = Integer.MAX_VALUE;
    //TCP连接数据接收超时时间
    private Long sotimeout = -1l;
    //Udp回调函数
    private UdpCallback udpCallback;
    //标记Udp是否已连接
    private boolean isConnected = false;


    /**
     * 创建一个UDP连接对象
     *
     * @param port        目标IP端口号
     * @param sotimeout   数据读取超时，默认不设置，-1缺省值
     * @param times       最大失败连接次数，指定超过多少次连接失败后调用回调函数的connectFail
     * @param udpCallback 回调函数
     */
    public UdpSocket(int port, Long sotimeout, int times, UdpCallback udpCallback) {
        this.port = port;
        this.sotimeout = sotimeout;
        this.udpCallback = udpCallback;
        this.times = times;
    }

    /**
     * 单次通信（发，收，关）
     *
     * @param sotimeout
     * @param data
     * @param toIp
     * @param port
     * @return
     */
    public synchronized static String[] sendMessage(Long sotimeout, String data, String toIp, int port) throws SocketTimeoutException, NullPointerException {
        String[] result = new String[2];
        DatagramSocket udpSocket = null;
        try {
            // 计算随机的端口地址
            int aport = -1;
            while (true) {
                aport = random(65535);
                if ((aport != -1) && (aport != port)) {
                    break;
                }
            }
            // 创建Udp连接
            udpSocket = new DatagramSocket(aport);
            // 设置超时
            udpSocket.setSoTimeout(sotimeout.intValue());
            byte[] bytes = data.getBytes("UTF-8");
            // 创建发送包
            DatagramPacket send = new DatagramPacket(bytes, bytes.length,
                    InetAddress.getByName(toIp), port);
            // 发送
            udpSocket.send(send);
            // 记录发送的时间
            UdpSocket.lastSendTime = new Date();
            // 创建接收包，接收
            DatagramPacket receive = new DatagramPacket(new byte[9999], 0, 9999);
            udpSocket.receive(receive);
            if (receive.getLength() == -1) {
                throw new IOException();
            }
            // 获取数据，进行处理
            result[0] = new String(receive.getData(), 0, receive.getLength(),
                    "UTF-8");
            result[1] = receive.getAddress().toString().substring(1);
        } catch (Exception e) {
            log.e(String.format("UDP异常,ip:%s,port:%d,Exception:" + e.toString(), toIp, port), UdpSocket.class.getSimpleName(), "sendMessage");
            // e.printStackTrace();
        } finally {
            if (udpSocket != null) {
                udpSocket.close();
                udpSocket = null;
            }
        }
        return result;
    }

    /**
     * 生成一个小于等于X,大于0的值
     *
     * @param x
     * @return
     */
    private static int random(int x) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        return (random.nextInt(x) + 1);
    }

    /**
     * 将单字节转化为16进制
     *
     * @param buffer
     * @return
     */
    private static String byte2Int16(byte buffer) {
        String str = Integer.toString(buffer & 0xFF, 16).toUpperCase();
        return str.length() == 1 ? 0 + str : str;
    }

    /**
     * 计算最后一次发送的时间,若间隔过小则睡眠相差的毫秒数
     *
     * @param thread
     * @throws InterruptedException
     */
    public static void calcLastSendTime(IcoThread thread)
            throws InterruptedException {
        if (UdpSocket.lastSendTime != null) {
            Date date = new Date();
            Long _intervel = date.getTime() - UdpSocket.lastSendTime.getTime();
            if (_intervel < interval) {
                thread.sleep(interval - _intervel);
            }
        }
    }

    /**
     * 发送一次广播，等待时间内无限收响应
     *
     * @return
     */
    public static DatagramSocket createSocket(int port, int times) throws SocketException {
        DatagramSocket udpSocket = null;

        // 计算随机的端口地址
        int aport = -1;
        while (true) {
            aport = random(65535);
            if ((aport != -1) && (aport != port)) {
                break;
            }
        }
        // 创建Udp连接
        udpSocket = new DatagramSocket(aport);
        return udpSocket;
    }

    @Override
    public void run() {
        for (int i = 0; (!isClosed()); i++) {
            try {
                log.i(String.format("UDP正在建立连接%d,port:%d", i, port) + i, UdpSocket.class.getSimpleName(), "run");
                DatagramSocket _socket = UdpSocket.createSocket(port, times);
                socket = _socket;
                break;
            } catch (Exception e) {
                log.e(String.format("UDP建立连接失败%d,port:%d,Exception:" + e.toString(), i, port) + i, UdpSocket.class.getSimpleName(), "run");
                if (i == times - 1) {
                    udpCallback.createfail(this);
                    close();
                    return;
                }
                try {
                    mThread.sleep(500l);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        // 设置数据接收超时时间
        if (sotimeout != -1) {
            try {
                socket.setSoTimeout(sotimeout.intValue());
            } catch (Exception e) {
//                    e.printStackTrace();
                log.e(String.format("UDP设置数据接收超时时间异常，port:%d，Exception:" + e.toString(), port), UdpSocket.class.getSimpleName(), "run");
            }
        }
        setConnected(true);
        //循环接收数据
        while (!isClosed()) {
            DatagramPacket receive = null;
            try {
                // 创建接收包，接收
                receive = new DatagramPacket(new byte[1024], 0, 1024);
                log.i(String.format("UDP正在接收数据，port:%d", port), UdpSocket.class.getSimpleName(), "run");
                socket.receive(receive);

                String d = "";
                for (int i = 0; i < receive.getLength(); i++) {
                    d += byte2Int16(receive.getData()[i]).toUpperCase() + " ";
                }

                log.w(String.format("UDP接收数据成功，port:%d，ip：%s，数据长度：%d；数据：" + d, port, receive.getAddress().toString(), receive.getLength()), UdpSocket.class.getSimpleName(), "run");
            } catch (IOException e) {
//                    e.printStackTrace();
                log.e("UDP连接IO流异常，Exception:" + e.toString(), UdpSocket.class.getSimpleName(), "run");
                close();
                return;
            } catch (Exception e) {
//                    e.printStackTrace();
                log.e("UDP异常，Exception:" + e.toString(), UdpSocket.class.getSimpleName(), "run");
            }
            udpCallback.receive(receive);
        }

    }

    /**
     * 关闭该UDP连接，关闭后必须重新创建
     */
    @Override
    public void close() {
        super.close();
        setConnected(false);
        if (socket != null) {
            socket.close();
        }
    }

    /**
     * 发送UDP广播
     *
     * @param buffer
     * @param ip
     * @param _port
     * @throws {@link IOException}
     */
    public void send(byte[] buffer, String ip, int _port) throws IOException {
        // 创建发送包
        DatagramPacket send = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), _port);
        log.w(String.format("UDP发送数据，ip：%s，port：%d，数据长度：%d", ip, _port, buffer.length), UdpSocket.class.getSimpleName(), "send");
        // 发送
        socket.send(send);
    }

    /**
     * 判断Udp是否连接
     *
     * @return boolean
     */
    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
        if (isConnected) {
            udpCallback.createSuccess(this);
        }
    }
}
