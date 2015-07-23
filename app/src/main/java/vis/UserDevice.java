package vis;

/**
 * Created by Vision on 15/6/19.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class UserDevice {

    // 传输状态：正常，下载中，已下载
    public static final int TRANSFER_STATE_NORMAL = 0x00;
    public static final int TRANSFER_STATE_TRANSFERRING = 0x01;
    public static final int TRANSFER_STATE_FINISH = 0x02;

    /**
     * IP
     */
    public String ip;

    /**
     * int 型 IP
     */
    public int ipInt;

    public int port;

    public String name;

    /**
     * 发送文件总数
     */
    public int fileTotal;

    /**
     * 当前发送文件序号
     */
    public int currentFile;

    /**
     * 当前发送文件名字
     */
    public String currentFileName;
    /**
     * 完成百分比
     */
    public int completed;
    /**
     * 下载状态:正常,正在下载，暂停，等待，已下载
     */
    public int state;
}
