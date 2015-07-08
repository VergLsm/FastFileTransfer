package vis.net.protocol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

import vis.DevicesList;
import vis.UserDevice;
import vis.net.CommandsTransfer;
import vis.net.FilesTransfer;

/**
 * Created by Vision on 15/7/7.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class ShareServer {
    /**
     * 文件传输类
     */
    private final FilesTransfer mFilesTransfer;
    /**
     * 命令传输类
     */
    private CommandsTransfer mCommandsTransfer;

    /**
     * 用户接入列表
     */
    DevicesList<UserDevice> mDevicesList;

    private Handler mCommandHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<ShareServer> mShareServer;

        public MyHandler(ShareServer s) {
            mShareServer = new WeakReference<ShareServer>(s);
        }

        @Override
        public void handleMessage(Message msg) {
            ShareServer shareServer = mShareServer.get();
            if (shareServer != null) {
                Object[] objects = (Object[]) msg.obj;
                byte[] address = (byte[]) objects[0];
                byte[] data = (byte[]) objects[1];
                SwapPackage sp = new SwapPackage(data);
                if (sp.getCmdByByte() == SwapPackage.LOGIN) {
                    //设备登入
                    UserDevice us = new UserDevice();
                    us.ip = byteArray2IpAddress(address);
                    us.name = new String(sp.getData());
                    int ip = byteArray2Int(address);
                    shareServer.mDevicesList.put(ip, us);
                    Log.d("Login", us.ip + "->" + new String(sp.getData()));
                } else if (sp.getCmdByByte() == SwapPackage.LOGOUT) {
                    int ip = byteArray2Int(address);
                    shareServer.mDevicesList.remove(ip);
                    Log.d("Logout", new String(address) + "->" + new String(sp.getData()));
                }
            }
        }
    }


    public ShareServer(Context context, DevicesList<UserDevice> devicesList) {
        mDevicesList = devicesList;
        mCommandsTransfer = new CommandsTransfer(2222);
        mFilesTransfer = new FilesTransfer(context, FilesTransfer.SERVICE_RECEIVE);
//        mAdapter = new UserDevicesAdapter(context, devicesList);
        //把适配器的handler交给mFilesTransfer，以便transfer控制适配器
//        Log.d("FFTService", String.valueOf(mAdapter.getHandler()));

        // 这里adapter已经不在这里了，在这里传个DevicesList，
        // 当数据发生变化时，通知DevicesList，然后再刷新界面
        mFilesTransfer.setCallbackHandler(devicesList.getHandler());
    }

    public void sendFlies(Context context, String[] paths) {
        if (null != paths && paths.length != 0) {
            File[] files = new File[paths.length];
            for (int i = 0; i < paths.length; i++) {
                Log.d("Paths", paths[i]);
                files[i] = new File(paths[i]);
            }
            sendFlies(context, files);
        } else {
            Toast.makeText(context, "没有选择文件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送文件信息
     *
     * @param context 上下文
     * @param files   文件
     */
    public void sendFlies(Context context, File[] files) {
        if (null == files) {
            Toast.makeText(context, "没有选择文件", Toast.LENGTH_SHORT).show();
//        } else if (mAdapter.getCount() == 0) {
        } else if (mDevicesList.size() == 0) {

            Toast.makeText(context, "没有设备连接", Toast.LENGTH_SHORT).show();
        } else {
            //发送文件
//            Toast.makeText(context, file.toString(), Toast.LENGTH_SHORT).show();
//            for (int i = 0, nsize = mAdapter.getCount(); i < nsize; i++) {
            for (int i = 0, nsize = mDevicesList.size(); i < nsize; i++) {
//                UserDevice ud = (UserDevice) mAdapter.getObject(i);
                UserDevice ud = (UserDevice) mDevicesList.valueAt(i);
                if (ud.state != UserDevice.TRANSFER_STATE_TRANSFERRING) {
                    ud.state = UserDevice.TRANSFER_STATE_TRANSFERRING;
                    mFilesTransfer.sendFile(i, files, ud.ip, 2223);
                    Log.d(this.getClass().getName(), ud.ip + ":2333->" + files.toString());
                }
            }
        }
    }

    public void enable() {
        mCommandsTransfer.setCallbackHandler(this.mCommandHandler);
    }

    public void disable() {
        mCommandsTransfer.setCallbackHandler(null);
    }


    /**
     * @param array
     * @return int
     */
    private static int byteArray2Int(byte[] array) {
        int temp = 0;
        for (int i = 0; i < 4; i++) {
            temp |= array[i] << (24 - (i * 8));
        }
        return temp;
    }

    /**
     * byte数组型IP地址转成String型IP地址
     *
     * @param array
     * @return String
     */
    private static String byteArray2IpAddress(byte[] array) {
        return (array[0] & 0xff) + "." + (array[1] & 0xff) + "." + (array[2] & 0xff) + "." + (array[3] & 0xff);
    }

}
