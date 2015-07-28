package vision.resourcemanager;

import android.graphics.drawable.Drawable;

import vis.UserFile;

/**
 * Created by Vision on 15/7/3.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FileApp extends UserFile {
    public Drawable icon;//FIXME 这里应该处理为软引用，防止内存溢出
}
