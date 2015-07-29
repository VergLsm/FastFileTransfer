package vision.resourcemanager;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Vision on 15/7/29.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class ResourceManager {


    /**
     * 检测SD卡是否就绪
     *
     * @param dirName 测试目录
     * @return 如果就绪返回true
     */
    public static boolean checkSDcard(String dirName) {
        boolean isOK = false;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
//            Log.d(this.getClass().getName(), Environment.getExternalStorageState());
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + dirName);
            if (!dir.exists()) {            //文件夹不存在
                if (dir.mkdirs()) {         //创建文件夹
//                    Toast.makeText(this.context, "创建文件夹成功", Toast.LENGTH_SHORT).show();
                    Log.d("checkSDcard()", "created document success");
                }
            }
            if (dir.exists()) {             //已经存在或者已经创建成功
                if (dir.canWrite()) {       //可以写入
                    Log.d("checkSDcard()", "the dir is OK!");
                    isOK = true;
                } else {
                    Log.e("checkSDcard()", "the dir can not write");
                }
            } else {
                Log.d("checkSDcard()", "没有这个目录");
            }
        } else {
            Log.d("checkSDcard()", "请检查SD卡是否正确安装");
        }
        return isOK;
    }


    public static int numCount2(int a) {
        int num = 0;
        while (a != 0) {
            a &= (a - 1);
            num++;
        }
        return num;
    }

}
