package vis.net;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vis.DevicesList;
import vis.UserDevice;
import vis.UserFile;

/**
 * 文件传输类
 * Created by Vision on 15/6/16.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FilesTransfer {

    public static final int SERVICE_SHARE = 1;
    public static final int SERVICE_RECEIVE = 2;


    public static final int DEIVCE_INVALID = 0;
    public static final int DEIVCE_VALID = 1;

    private final ExecutorService executorService;
    private ServerSocket mServerSocket;
    private Socket mSocket;
    /**
     * 是否在接收模式
     */
    private boolean isReceiving = false;
    private Context context;
    private Handler mHandler;
    private Message msg;
    private DevicesList<UserDevice> mDevicesList;

    public FilesTransfer(Context context, int serviceType) {
        this.context = context;
        if (SERVICE_SHARE == serviceType) {
            executorService = Executors.newFixedThreadPool(3);
        } else if (SERVICE_RECEIVE == serviceType) {
            executorService = Executors.newSingleThreadExecutor();
        } else {
            executorService = null;
        }
    }

    public void setCallbackHandler(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 发送文件，最多可同时发往3个地址
     *
     * @param index 目标用户序号
     * @param files 要发送的文件
     * @param ud    用户对象
     */
    public void sendFile(int index, File[] files, UserDevice ud) {
        executorService.execute(new Sender(index, files, ud));
    }


    public void sendFile(File[] files, DevicesList<UserDevice> devicesList) {
        mDevicesList = devicesList;
        for (int i = 0, nsize = mDevicesList.size(); i < nsize; i++) {
            UserDevice ud = (UserDevice) mDevicesList.valueAt(i);
            if (ud.state != UserDevice.TRANSFER_STATE_TRANSFERRING) {
                ud.state = UserDevice.TRANSFER_STATE_TRANSFERRING;
                sendFile(i, files, ud);
                Log.d(this.getClass().getName(), ud.ip + ":2333->" + files.toString());
            }
        }
    }

    /**
     * 接收文件
     *
     * @param dirName 文件存放位置，文件夹名
     */
    public void receiveFile(int port, String dirName) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
//            Log.d(this.getClass().getName(), Environment.getExternalStorageState());
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + dirName);
            if (!dir.exists()) {            //文件夹不存在
                if (dir.mkdirs()) {         //创建文件夹
//                    Toast.makeText(this.context, "创建文件夹成功", Toast.LENGTH_SHORT).show();
                    Log.d(this.getClass().getName(), "created document success");
                }
            }
            if (dir.exists()) {             //已经存在或者已经创建成功
                if (dir.canWrite()) {       //可以写入
                    Log.d(this.getClass().getName(), "the dir is OK!");
                    executorService.execute(new Receiver(port, dir));
                } else {
                    Log.e(this.getClass().getName(), "the dir can not write");
                }
            } else {
                Log.d(this.getClass().getName(), "没有这个目录");
            }
        } else {
            Toast.makeText(this.context, "请检查SD卡是否正确安装", Toast.LENGTH_SHORT).show();
            Log.e(this.getClass().getName(), "请检查SD卡是否正确安装");
        }
    }

    public boolean isReceiving() {
        return this.isReceiving;
    }

    public void stopReceiving() {
        this.isReceiving = false;
        executorService.shutdown();
    }

    class Receiver implements Runnable {
        private DataInputStream din = null;
        private FileOutputStream fout;
        private int length = 0;
        private byte[] inputByte = null;
        private int port;
        private File dir;
        private UserFile userFile;
        /**
         * 本次接收的文件序号
         */
        private int index;

        public Receiver(int port, File dir) {
            this.port = port;
            this.dir = dir;
        }

        @Override
        public void run() {
            isReceiving = true;
            inputByte = new byte[1024];
            try {
                mServerSocket = new ServerSocket(port);
                mServerSocket.setSoTimeout(2000);
                while (isReceiving) {
                    try {
                        Log.d(this.getClass().getName(), "accepting the connect");
                        mSocket = mServerSocket.accept();
                        mSocket.setSoTimeout(2000);
                        Log.d(this.getClass().getName(), "start translate");
                        din = new DataInputStream(mSocket.getInputStream());
                        userFile = new UserFile();
                        userFile.name = din.readUTF();
                        File file;
                        int i = 1;
                        while ((file = new File(dir.getPath() + "/" + userFile.name)).exists()) {
                            userFile.name = userFile.name.replaceAll("(\\(\\d*\\))?\\.", "(" + String.valueOf(i++) + ").");
                        }
                        Log.d("isExists", file.getPath());
                        fout = new FileOutputStream(file);
                        userFile.size = din.readLong();
                        userFile.state = UserFile.TRANSFER_STATE_TRANSFERRING;
                        userFile.id = index;
                        while (true) {
                            if (din != null) {
                                length = din.read(inputByte, 0, inputByte.length);
                            }
                            if (length == -1) {
                                break;
                            }
                            fout.write(inputByte, 0, length);
                            fout.flush();
                            userFile.completed += length;
                            if (userFile.completed == userFile.size) {
                                userFile.state = UserFile.TRANSFER_STATE_FINISH;
                            }
                            msg = Message.obtain();
                            msg.obj = userFile;
                            mHandler.sendMessage(msg);
                        }
                        index++;
                        Log.d(this.getClass().getName(), "finish translate");
                    } catch (SocketTimeoutException e) {
                        Log.d("Exception", "SocketTimeoutException");
                    } catch (SocketException e) {
                        Log.d("Exception", "SocketException");
                    }
                }
                if (din != null)
                    din.close();
                if (fout != null)
                    fout.close();
                if (mSocket != null)
                    mSocket.close();
                if (mServerSocket != null) {
                    mServerSocket.close();
                }
                Log.d(this.getClass().getName(), "end all thing");
            } catch (IOException e) {
                Log.d("Exception", "IOException");
            }
        }
    }

    class Sender implements Runnable {
        private final UserDevice ud;
        private int length = 0;
        private byte[] sendByte = null;
        private Socket socket = null;
        private DataOutputStream dout = null;
        private FileInputStream fin = null;

        private File[] files;

        private long sendLength;
        private int index;
        private int completionPercentage;

        /**
         * @param index 目标用户序号
         * @param files 要发送的文件
         * @param ud    用户对象
         */
        public Sender(int index, File[] files, UserDevice ud) {
            this.index = index;
            this.files = files;
            this.ud = ud;
        }

        @Override
        public void run() {
            for (File file : files) {
                sendLength = completionPercentage = 0;
                Log.d(this.getClass().getName(), "start send files :" + file.length());
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ud.ip, ud.port), 1000);
                    dout = new DataOutputStream(socket.getOutputStream());
//                File files = new File("E:\\TU\\DSCF0320.JPG");
                    fin = new FileInputStream(file);
                    sendByte = new byte[1024];
                    dout.writeUTF(file.getName());
                    dout.writeLong(file.length());
                    while ((length = fin.read(sendByte, 0, sendByte.length)) > 0) {
                        dout.write(sendByte, 0, length);
                        dout.flush();
                        sendLength += length;
                        int transferred = (int) (sendLength * 100 / file.length());
                        if (completionPercentage < transferred) {       //减少发送message
//                        Log.d("completed", String.valueOf(completionPercentage));
                            completionPercentage = transferred;
                            msg = Message.obtain();
                            msg.what = DEIVCE_VALID;
                            ud.completed = completionPercentage;
                            if (100 > completionPercentage) {
                                ud.state = UserDevice.TRANSFER_STATE_TRANSFERRING;
                            } else {
                                ud.state = UserDevice.TRANSFER_STATE_FINISH;
                            }
                            mHandler.sendMessage(msg);
                        }
//                    Log.d("sendLength:", String.valueOf(sendLength));
                    }
                } catch (IOException e) {
                    Log.d("", e.getMessage() + "这个目标设备有问题了");
                    msg = Message.obtain();
                    msg.what = DEIVCE_INVALID;
                    msg.arg1 = ud.ipInt;
                    mHandler.sendMessage(msg);
                } finally {
                    Log.d(this.getClass().getName(), "end send");
                    try {
                        if (dout != null)
                            dout.close();
                        if (fin != null)
                            fin.close();
                        if (socket != null)
                            socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
