package vis.net.protocol;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import vis.net.CommandsTransfer;
import vis.net.FilesTransfer;

/**
 * FastFileTransfer 通讯服务<br>
 * 发送，接收，处理<br>
 * Created by Vision on 15/6/12.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FFTService {
    /**
     * 文件传输类
     */
    private final FilesTransfer mFilesTransfer;
    /**
     * 命令传输类
     */
    private CommandsTransfer mCommandsTransfer;
    private OnDataReceivedListener mOnDataReceivedListener;
    /**
     * 本地机型名
     */
    public final static byte[] LOCALNAME = android.os.Build.MODEL.replaceAll("\\s|-", "").getBytes();

    /**
     * 发送列表，设备连接列表，key为IP，value为设备名
     */
    private Map<String, String> mConnectedDevices;

    /**
     * 目标地址
     */
    private String targetAddress;

    public FFTService() {
        mConnectedDevices = new HashMap<String, String>();
        mCommandsTransfer = new CommandsTransfer(2222);
        mFilesTransfer = new FilesTransfer();
    }

    /**
     * 使能传输
     */
    public void enableTransmission() {
        mCommandsTransfer.enable();
    }

    /**
     * 失能传输
     */
    public void disableTransmission() {
        mCommandsTransfer.disable();
    }

    /**
     * 发送登入信息
     *
     * @param address 地址
     */
    public void sendLogin(String address) {
        //默认端口2222
        sendLogin(address, 2222);
    }

    /**
     * 发送登入信息
     *
     * @param address 地址
     * @param port    端口
     */
    public void sendLogin(String address, int port) {
        this.targetAddress = address;
        if (!mFilesTransfer.isReceiving()) {
            mFilesTransfer.receiveFile(2223, "/FFT");
//            Log.d(this.getClass().getName(), Environment.getExternalStorageDirectory().getAbsolutePath());
//            Log.d(this.getClass().getName(),Environment.getExternalStorageDirectory().getPath());
        }
        SwapPackage sp = new SwapPackage(address, port, SwapPackage.LOGIN, LOCALNAME);
        mCommandsTransfer.send(sp);
    }

    /**
     * 发送登出信息
     */
    public void sendLogout() {
        //默认端口2222
        sendLogout(this.targetAddress, 2222);
    }

    /**
     * 发送登出信息
     *
     * @param address 地址
     * @param port    端口
     */
    public void sendLogout(String address, int port) {
        SwapPackage sp = new SwapPackage(address, port, SwapPackage.LOGOUT, LOCALNAME);
        mCommandsTransfer.send(sp);
    }

    /**
     * 发送文件信息
     *
     * @param context 上下文
     * @param fileUri 文件Uri
     */
    public void sendFlies(Context context, Uri fileUri) {
        if (null == fileUri) {
            Toast.makeText(context, "没有选择文件", Toast.LENGTH_SHORT).show();
        } else if (mConnectedDevices.isEmpty()) {
            Toast.makeText(context, "没有设备连接", Toast.LENGTH_SHORT).show();
        } else {
            //发送文件
            Toast.makeText(context, fileUri.toString(), Toast.LENGTH_SHORT).show();
            //TODO  把这个Uri转成File
            for (Map.Entry<String, String> entry : mConnectedDevices.entrySet()) {
//                mFilesTransfer.sendFile(null,entry.getKey(),2223);
                Log.d(this.getClass().getName(), entry.getKey() + ":2333->" + fileUri.toString());
            }

        }

    }

    /**
     * 设置接收到数据时的监听器<br>
     *
     * @param listener 监听器
     */
    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.mOnDataReceivedListener = listener;
        if (listener == null) {
            mCommandsTransfer.setDateReceivedListener(null);
        } else {
            mCommandsTransfer.setDateReceivedListener(new CommandsTransfer.OnDataReceivedListener() {

                @Override
                public void onDataReceived(String address, byte[] data) {
                    SwapPackage sp = new SwapPackage(data);
//                    Log.d("SwapPackage", sp.getString().toString());
                    if (sp.getCmdByByte() == SwapPackage.LOGIN) {
                        //设备登入
                        addDevice(address, new String(sp.getData()));
                        Log.d("Login", address + "->" + new String(sp.getData()));
//                        mOnDataReceivedListener.onLogin(address, new String(sp.getData()));
                    } else if (sp.getCmdByByte() == SwapPackage.LOGOUT) {
                        removeDevice(address);
                        Log.d("Logout", address + "->" + new String(sp.getData()));
//                        mOnDataReceivedListener.onLogout(address, new String(sp.getData()));
                    }
                    mOnDataReceivedListener.onDataReceived(mConnectedDevices);
                }
            });

        }
    }

    /**
     * 接收到数据时的监听接口
     */
    public interface OnDataReceivedListener {
        /**
         * 有数据时回调
         *
         * @param devicesList 设备连接集合
         */
        void onDataReceived(Map<String, String> devicesList);
//        void onLogin(String address, String name);
//        void onLogout(String address, String name);
    }

    /**
     * 添加设备进列表中
     *
     * @param address IP地址，作为key
     * @param name    设备名称，作为value
     * @return 如果添加成功返回value，即为name;如果不成功返回null
     */
    private String addDevice(String address, String name) {
        return mConnectedDevices.put(address, name);
    }

    /**
     * 在列表中除移设备
     *
     * @param address IP地址，作为key
     * @return 如果添加成功返回对应的value，即为对应的name;如果不成功返回null
     */
    private String removeDevice(String address) {
        return mConnectedDevices.remove(address);
    }


    /**
     * 合并数组
     *
     * @param byte_1 数组一
     * @param byte_2 数组二
     * @return 合并之后的数组
     */
    public byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


}
