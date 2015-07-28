package vis.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vis.net.protocol.SwapPackage;

/**
 * 命令传输类，<br>
 * 使用前必须先调用enable使能，使用完毕必须再调用disable失能；
 * 发送直接调用 {@code send()} ；
 * 接收只需要设置好监听 {@code setCallbackHandler()} 就能开启，设置为Null即关闭<br>
 * <p/>
 * <br>
 * Created by Vision on 15/6/9.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class CommandsTransfer {

    //    private static final int PORT = 2048;
    private static final int RECEIVEPACKETLENGTH = 64;
    private final ExecutorService executorService;

    /**
     * 是否使能UDP
     */
//    private boolean isEnable = false;
    /**
     * 是否使能接收
     */
    private boolean isReceive = false;

    private boolean isEndReceive = true;

    /**
     * 接收监听端口
     */
    private int recvPort;
    private DatagramSocket mDatagramSocket = null;
    private DatagramPacket sendPacket;
    private Handler mHandler;
    //已经自行实例化
    private static CommandsTransfer single;

    private CommandsTransfer() {
        //默认接收端口为2048
        this(2048);
    }

    private CommandsTransfer(int recvPort) {
        this.recvPort = recvPort;
        executorService = Executors.newSingleThreadExecutor();
    }

    //静态工厂方法
    public static CommandsTransfer getInstance() {
        if (single == null) {
            single = new CommandsTransfer(2222);
        }
        return single;
    }
    /**
     * 使能UDP
     */
//    public void enable() {
//        this.isEnable = true;
//    }

    /**
     * @return the isEnable
     */
//    public boolean isEnable() {
//        return isEnable;
//    }

    /**
     * 失能UDP
     */
//    public void disable() {
//        this.isReceive = false;
//        this.isEnable = false;
//        closeSocket();
//    }

    /**
     * 获得DatagramSocket对象
     *
     * @return DatagramSocket
     */
//    private DatagramSocket getSocket() {
//        if (this.mDatagramSocket == null) {
//            try {
//                mDatagramSocket = new DatagramSocket(recvPort);
//                mDatagramSocket.setSoTimeout(5000);
//            } catch (SocketException e) {
//                e.printStackTrace();
//            }
//        }
//        return mDatagramSocket;
//    }

    /**
     * 关闭DatagramSocket
     */
//    private void closeSocket() {
//        if (mDatagramSocket != null) {
//            if (mDatagramSocket.isConnected()) {
//                mDatagramSocket.disconnect();
//            }
//            if (!mDatagramSocket.isClosed()) {
//                mDatagramSocket.close();
//            }
//            mDatagramSocket = null;
//        }
//    }

    /**
     * 发送
     *
     * @param sp SwapPackage 交换包
     */
    public void send(SwapPackage sp) {
//        if (isEnable) {
        executorService.execute(new Sender(sp.getString(), sp.getAddress(), sp.getPort()));
//            new Thread().start();
//        }
    }

    /**
     * 开启接收器
     */
    private void startReceiver() {
//        ExecutorService exec = Executors.newCachedThreadPool();
//        Receiver server = new Receiver();
//        exec.execute(server);
//        if (isEnable ) {
        isReceive = true;
        if (isEndReceive) {
            executorService.execute(new Receiver());
        }
//            new Thread().start();
//        }
    }

    /**
     * 停止接收
     */
    private void stopReceiver() {
        isReceive = false;
        if (!mDatagramSocket.isClosed()) {
            mDatagramSocket.close();
        }
    }

    /**
     * 设置数据接收监听
     *
     * @param handler 回调Handler
     */
    public void setCallbackHandler(Handler handler) {
        this.mHandler = handler;
        if (null == handler) {
            stopReceiver();
        } else {
            startReceiver();
        }
    }

    /**
     * 发送线程
     */
    class Sender implements Runnable {

        private final byte[] msg;
        //        private final String TAG = Sender.class.getName();
        private final String address;
        private final int port;

        public Sender(byte[] msg, String address, int port) {
            this.address = address;
            this.port = port;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                mDatagramSocket = new DatagramSocket();
                sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(address),
                        port);
            } catch (UnknownHostException e) {
                Log.d("Sender", "UnknownHostException");
            } catch (SocketException e) {
                Log.d("Sender", "SocketException");
            }
            try {
                mDatagramSocket.send(sendPacket);
                Log.d("send", new String(msg));
            } catch (IOException e) {
                Log.d("Sender","IOException");
            } finally {
                if (!mDatagramSocket.isClosed()) {
                    mDatagramSocket.close();
                }
            }
        }
    }

    /**
     * 接收线程
     */
    class Receiver implements Runnable {
        private Message msg;

        @Override
        public void run() {
            try {
                mDatagramSocket = new DatagramSocket(recvPort);
//                mDatagramSocket.setSoTimeout(1000);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            byte[] receiveBuf = new byte[RECEIVEPACKETLENGTH];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
            while (isReceive) {
                try {
                    mDatagramSocket.receive(receivePacket);
                    Object[] objects = new Object[2];
                    objects[0] = receivePacket.getAddress().getAddress();
                    objects[1] = receivePacket.getData();
                    msg = Message.obtain();
                    msg.obj = objects;
                    mHandler.sendMessage(msg);
                    Log.i("received", new String((byte[]) objects[0]) + "->" + new String(receivePacket.getData()).trim());
                } catch (IOException e) {
                    Log.d("", "IOException");
                }
            }
            Log.d("", "I am closing!");
            isEndReceive = true;
            executorService.shutdown();
            CommandsTransfer.single = null;
        }
    }

}
