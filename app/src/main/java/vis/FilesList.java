package vis;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

/**
 * Created by Vision on 15/7/8.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FilesList<E> extends SparseArray {
    private OnDataChangedListener onDataChangedListener;

    /**
     * 交给其它线程控制的Handler
     */
    private final MyHandler mHandler = new MyHandler<FilesList>(this);

    private static class MyHandler<T> extends Handler {
        private final WeakReference<T> mT;

        public MyHandler(T t) {
            mT = new WeakReference<T>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            FilesList filesList = (FilesList) mT.get();
            if (filesList != null) {
                UserFile userFile = (UserFile)
                        msg.obj;
                if (filesList.size() <= userFile.id) {
                    filesList.put(userFile.id, userFile);
                    if (null != filesList.onDataChangedListener) {
                        filesList.onDataChangedListener.onAdded(filesList.size());
                    }
                }
                // notifyDataSetChanged会执行getView函数，更新所有可视item的数据
//                filesList.notifyDataSetChanged();
                if (null != filesList.onDataChangedListener) {
                    filesList.onDataChangedListener.onDataChanged();
                }
                // 只更新指定item的数据，提高了性能
//            updateView(msg.what);
            }
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    public interface OnDataChangedListener {
        void onAdded(int size);

        void onDataChanged();
//        void onRemoved(int size);
    }

}
