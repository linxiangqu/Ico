package ico.ico.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @包名 sunrun.szogemray.util
 * @作者 ico
 * @创建日期 2015/1/20 0020
 * @版本 V 1.0
 */

public class TcpSocket extends IcoThread {
    //常量
    public final static int TCPSOCKET_CONNECT_SUCCESS = 100;
    public final static int TCPSOCKET_CONNECT_FAIL = 101;
    public final static int TCPSOCKET_RECEIVE = 102;
    public final static int TCPSOCKET_TIMEOUT = 103;
    /**
     * 实体参数
     */
    //TCP连接对象和IO流
    private Socket socket = null;
    private BufferedInputStream input = null;
    private BufferedOutputStream output = null;
    //TCP连接对象的IP地址和端口
    private String ip;
    private int port;
    //TCP连接超时时间
    private Long timeout = 20000l;
    //TCP连接次数上限，超过该次数将调用回调函数connectFail
    private int times = Integer.MAX_VALUE;
    //TCP连接数据接收超时时间
    private Long sotimeout = -1l;
    //TCP回调函数
    private TcpCallback tcpCallback;
    //要发送的数据
    private List<byte[]> data = new ArrayList<byte[]>();
    //心跳包相关
    private boolean isHeart = false;
    private byte[] heartData;
    private Long heartInterval = 0l;
    //发送线程
    private SendThread sendThread;
    //标记Udp是否已连接
    private boolean isConnected = false;
    // 最后一次发送命令的时间
    private long lastSendTime;
    // 每次命令需间隔时间
    private Long interval = 300l;

    /**
     * 创建一个TCP连接对象，不使用心跳机制
     *
     * @param ip          目标IP地址
     * @param port        目标IP端口号
     * @param timeout     连接超时设置，默认20S,-1位缺省值
     * @param sotimeout   数据读取超时，默认不设置，-1缺省值
     * @param times       最大失败连接次数，指定超过多少次连接失败后调用回调函数的connectFail
     * @param tcpCallback 回调函数
     */
    public TcpSocket(String ip, int port, Long timeout, Long sotimeout, int times, TcpCallback tcpCallback) {
        this.ip = ip;
        this.port = port;
        if (timeout > 0) {
            this.timeout = timeout;
        }
        this.sotimeout = sotimeout;
        this.tcpCallback = tcpCallback;
        this.times = times;
    }

    /**
     * 创建一个TCP连接对象，不使用心跳机制
     *
     * @param ip            目标IP地址
     * @param port          目标IP端口号
     * @param timeout       连接超时设置，默认20S,-1位缺省值
     * @param sotimeout     数据读取超时，默认不设置，-1缺省值
     * @param times         最大失败连接次数，指定超过多少次连接失败后调用回调函数的connectFail
     * @param tcpCallback   回调函数
     * @param heartData     心跳包数据
     * @param heartInterval 心跳包发送间隔
     */
    public TcpSocket(String ip, int port, Long timeout, Long sotimeout, int times, TcpCallback tcpCallback, byte[] heartData, Long heartInterval) {
        this(ip, port, timeout, sotimeout, times, tcpCallback);
        isHeart = true;
        this.heartInterval = heartInterval;
        this.heartData = heartData;
    }

    /**
     * 计算最后一次发送的时间,若间隔过小则睡眠相差的毫秒数
     *
     * @param thread
     * @throws InterruptedException
     */
    public static void calcLastSendTime(IcoThread thread, long lastSendTime, long interval)
            throws InterruptedException {
        if (lastSendTime == 0l || interval == 0l) {
            return;
        }
        Long _intervel = new Date().getTime() - lastSendTime;
        if (_intervel < interval) {
            thread.sleep(interval - _intervel);
        }
    }

    /**
     * 创建一个Tcp连接
     *
     * @param ip      TCP服务器的IP地址
     * @param port    TCP服务器的端口号
     * @param timeout TCP服务器连接超时时间
     * @return
     * @throws {@link IOException}
     */
    public static Socket createSocket(String ip, int port, Long timeout) throws IOException {
        Socket socket = null;
        // 创建socket
        socket = new Socket();
        // 创建Udp连接
        socket.connect(new InetSocketAddress(ip, port), timeout.intValue());
        return socket;
    }

    @Override
    public void run() {
        //进行TCP连接
        for (int i = 0; (!isClosed()); i++) {
            try {
                log.i(String.format("TCP正在建立连接%d,ip:%s,port:%d", i, ip, port), TcpSocket.class.getSimpleName(), "run");
                Socket _socket = TcpSocket.createSocket(ip, port, timeout);
                log.w(String.format("TCP建立连接成功%d,ip:%s,port:%d", i, ip, port), TcpSocket.class.getSimpleName(), "run");
                socket = _socket;
                break;
            } catch (Exception e) {
                log.e(String.format("TCP建立连接失败%d,ip:%s,port:%d,Exception:" + e.toString(), i, ip, port) + i, TcpSocket.class.getSimpleName(), "run");
                if (i == times - 1) {
                    tcpCallback.connectFail(this);
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
                log.e(String.format("TCP设置数据接收超时时间异常，ip:%s,port:%d，Exception:" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), "run");
            }
        }

        try {
            // 获取输入输出
            output = new BufferedOutputStream(socket.getOutputStream());
            input = new BufferedInputStream(socket.getInputStream());
        } catch (Exception e) {
//                e.printStackTrace();
            log.e(String.format("TCP获取IO流失败，ip:%s,port:%d，Exception:" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), "run");
            close();
            return;
        }
        setConnected(true);
        // 读取返回
        while (!isClosed()) {
            byte[] buffer = new byte[0];
            try {
                byte[] bytes = new byte[1024];
                log.i(String.format("TCP正在接收数据，ip：%s，port：%d", ip, port), TcpSocket.class.getSimpleName(), "run");
                int len = input.read(bytes);

                if (len == -1) {
                    throw new IOException("len==-1");
                }
                buffer = new byte[len];
                System.arraycopy(bytes, 0, buffer, 0, len);


                String d = "";
                for (byte b : buffer) {
                    d += Common.byte2Int16(b).toUpperCase() + " ";
                }
                log.w(String.format("TCP接收数据成功，ip：%s，port：%d，数据长度：%d；数据：" + d, ip, port, len), TcpSocket.class.getSimpleName(), "run");
            } catch (IOException e) {
//                e.printStackTrace();
                log.e(String.format("TCP连接IO流异常，ip:%s,port:%d，Exception:" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), "run");
                close();
                tcpCallback.connectDisconnect(this);
            } catch (Exception e) {
//                e.printStackTrace();
                log.e(String.format("TCP数据接收异常，ip:%s,port:%d，Exception:" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), "run");
                continue;
            }
            tcpCallback.receive(buffer);
        }
    }

    /**
     * 关闭TCP连接，不能再次使用
     */
    @Override
    public void close() {
        super.close();
        setConnected(false);
        if (sendThread != null) {
            sendThread.close();
        }
        if (input != null) {
            try {
                input.close();
                input = null;
            } catch (Exception e) {
                log.e(String.format("TCP输入流关闭异常，ip:%s,port:%d，Exception:" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), "close");
            }
        }
        if (output != null) {
            try {
                output.close();
                output = null;
            } catch (Exception e) {
                log.e(String.format("TCP输出流关闭异常，ip:%s,port:%d，Exception:" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), "close");
            }
        }
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (Exception e) {
                log.e(String.format("TCP关闭异常，ip:%s,port:%d，Exception:" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), "close");
            }
        }
        log.e(String.format("TCP连接断开,ip:%s,port:%d", ip, port), TcpSocket.class.getSimpleName(), "close");
    }


    /**
     * 将数据添加到数据队列中并立即发送
     *
     * @param buffer
     */
    public void send(byte[] buffer) {
        data.add(buffer);
        send();
    }

    /**
     * 将条数据添加至数据队列，不发送
     *
     * @param buffer
     */
    public void addData(byte[] buffer) {
        data.add(buffer);
    }

    /**
     * 将一组数据添加至数据队列中，不发送
     *
     * @param list
     */
    public void addDatas(List<byte[]> list) {
        data.addAll(list);
    }


    /**
     * 立即发送数据队列中的数据
     */
    public void send() {
        if (sendThread == null || sendThread.isClosed()) {
            sendThread = new SendThread();
            sendThread.start();
        } else {
            sendThread.interrupt();
        }
    }

    /**
     * 开启心跳包
     *
     * @param buffer        心跳包的数据
     * @param heartInterval 心跳包间隔时间
     */
    public void startHeart(byte[] buffer, Long heartInterval) {
        this.heartInterval = heartInterval;
        heartData = buffer;
        if (!isHeart) {
            isHeart = true;
            //不为null&&已关闭则重新开启
            if (sendThread == null || sendThread.isClosed()) {
                sendThread = new SendThread();
                sendThread.start();
            }
        }
    }

    /**
     * 关闭心跳包
     */
    public void stopHeart() {
        isHeart = false;
    }


    /**
     * **********************************************************************************GETSET
     */


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public TcpCallback getTcpCallback() {
        return tcpCallback;
    }

    public void setTcpCallback(TcpCallback tcpCallback) {
        this.tcpCallback = tcpCallback;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 设置当前连接状态，若设置true则会调用回调函数{@link ico.ico.util.TcpCallback} connectSuccess
     *
     * @param isConnected
     */
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
        if (isConnected) {
            tcpCallback.connectSuccess(this);
        }
    }

    /**
     * 数据发送线程
     * 数据队列中没有数据并且没有开启心跳时，该线程不开启
     * 数据队列中有数据时创建该线程并启动，当全部数据发送完成后，若不需要心跳则线程关闭，反之则根据设置进行心跳包的发送
     * 心跳睡眠中，若需要发送数据时，将唤醒线程；
     * 心跳睡眠被唤醒时会判断数据队列是否有数据，若有数据则直接发送数据，若没有数据则发送心跳包
     */
    public class SendThread extends IcoThread {
        @Override
        public void run() {
            synchronized (data) {
                while (!SendThread.this.isClosed()) {
                    if (data.size() == 0) {
                        if (isHeart) {
                            try {
                                calcLastSendTime(mThread, lastSendTime, heartInterval);
                            } catch (InterruptedException e) {
//                                e.printStackTrace();
                            }
                            if (data.size() != 0) {
                                continue;
                            }
                            try {
                                log.i(String.format("TCP正在发送心跳包，ip：%s，port：%d", ip, port), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName(), "run");
                                output.write(heartData);
                                output.flush();
                                log.w(String.format("TCP发送心跳包成功，ip：%s，port：%d", ip, port), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName(), "run");
                                tcpCallback.sendSuccess(TcpSocket.this, heartData);
                                lastSendTime = new Date().getTime();
                            } catch (Exception e) {
//                                e.printStackTrace();
                                log.e(String.format("TCP发送心跳包异常，ip：%s，port：%d，Exception：" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName(), "run");
                            }
                            try {
                                mThread.sleep(heartInterval);
                            } catch (InterruptedException e) {
                                log.e(String.format("TCP发送心跳包等待时被唤醒，ip：%s，port：%d，Exception：" + e.toString(), ip, port), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName(), "run");
                            }
                        } else {
                            SendThread.this.close();
                        }
                        continue;
                    }
                    List<byte[]> _data = new ArrayList<>();
                    _data.addAll(data);
                    for (int i = 0; i < _data.size() && (!isClosed()); i++) {
                        try {
                            calcLastSendTime(mThread, lastSendTime, interval);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                        try {
                            String d = "";
                            for (byte b : _data.get(i)) {
                                d += Common.byte2Int16(b).toUpperCase() + " ";
                            }

                            log.i(String.format("TCP正在发送数据，ip：%s，port：%d，数据长度：%d；数据：" + d, ip, port, _data.get(i).length), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName(), "run");
                            output.write(_data.get(i));
                            output.flush();
                            data.remove(_data.get(i));
                            log.w(String.format("TCP发送数据成功，ip：%s，port：%d，数据长度：%d；数据：" + d, ip, port, _data.get(i).length), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName(), "run");
                            tcpCallback.sendSuccess(TcpSocket.this, _data.get(i));
                            lastSendTime = new Date().getTime();
                        } catch (Exception e) {
                            e.printStackTrace();
                            i--;
                            try {
                                mThread.sleep(300l);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
                SendThread.this.close();
            }
        }
    }
}
