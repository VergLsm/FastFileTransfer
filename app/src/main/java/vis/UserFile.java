package vis;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Vision on 15/6/23.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class UserFile {

    public static final int TRANSFER_STATE_NORMAL = 0x00;
    public static final int TRANSFER_STATE_TRANSFERRING = 0x01;
    public static final int TRANSFER_STATE_FINISH = 0x02;

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_TEXT = 4;
    public static final int TYPE_APP = 5;

    /**
     * id
     */
    public int id;
    public long oid;

    /**
     * file 名
     */
    public String name;

    /**
     * 数据，这里一般是指绝对地址
     */
    public String data;

    /**
     * file的大小
     */
    public long size;
    public String strSize = "";
    /**
     * 文件类型
     */
    public int type;

    /**
     * 修改时间
     */
    public long date;
    public String strDate = "";

    /**
     * 是否被选中
     */
    public boolean isSelected;
    /**
     * 完成百分比
     */
    public long completed;
    /**
     * 下载状态:正常,正在下载，暂停，等待，已下载
     */
    public int state;

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }

    public static String dateFormat(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(date * 1000);
    }

}
