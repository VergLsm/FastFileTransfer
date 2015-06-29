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
    public String name;
    /**
     * 已下载大小
     */
//    public int downloadSize;

    /**
     * 完成百分比
     */
    public int completed;
    /**
     * 下载状态:正常,正在下载，暂停，等待，已下载
     */
    public int state;
}
