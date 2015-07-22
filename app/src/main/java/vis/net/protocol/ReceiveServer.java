package vis.net.protocol;

import android.content.Context;

import vis.FilesList;
import vis.UserFile;
import vis.net.CommandsTransfer;
import vis.net.FilesTransfer;
import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/7/8.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class ReceiveServer {

    public final static byte[] LOCALNAME = android.os.Build.MODEL.replaceAll("\\s|-", "").getBytes();

    /**
     * 文件传输类
     */
    private final FilesTransfer mFilesTransfer;
    private final String recFolder;
    /**
     * 命令传输类
     */
    private CommandsTransfer mCommandsTransfer;
    /**
     * 目标地址
     */
    private String targetAddress;

    public ReceiveServer(Context context, FilesList<UserFile> filesList) {
        mCommandsTransfer = CommandsTransfer.getInstance();
        mFilesTransfer = new FilesTransfer(context, FilesTransfer.SERVICE_RECEIVE);
//        if (SERVICE_SHARE == serviceType) {
//            mAdapter = new UserDevicesAdapter(context, mDevicesList);
//        } else if (SERVICE_RECEIVE == serviceType) {
//            mAdapter = new UserFilesAdapter(context);
//        }
        //把适配器的handler交给mFilesTransfer，以便transfer控制适配器
//        Log.d("FFTService", String.valueOf(mAdapter.getHandler()));
        //发送文件时可以控制进度条
        // 这里adapter已经不在这里了，在这里传个DevicesList，
        // 当数据发生变化时，通知DevicesList，然后再刷新界面
//        mFilesTransfer.setCallbackHandler(mAdapter.getHandler());
        mFilesTransfer.setCallbackHandler(filesList.getHandler());
        recFolder = context.getResources().getString(R.string.recFolder);
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
            mFilesTransfer.receiveFile(2223, recFolder);
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
        mFilesTransfer.stopReceiving();
    }


}
