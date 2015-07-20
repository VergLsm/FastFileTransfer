package vis;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import vis.net.FilesTransfer;

/**
 * Created by Vision on 15/7/7.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class DevicesList<E> extends SparseArray {
    private OnDataChangedListener onDataChangedListener;
    /**
     * 交给其它线程控制的Handler
     */
    private Handler mHandler = new MyHandler<DevicesList>(this);

    private static class MyHandler<T> extends Handler {
        private final WeakReference<T> mT;

        public MyHandler(T t) {
            mT = new WeakReference<T>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            DevicesList devicesList = (DevicesList) mT.get();
            if (devicesList != null) {
//                UserDevice ud = (UserDevice) devicesList.valueAt(msg.what);
                if (FilesTransfer.DEIVCE_VALID == msg.what) {
                } else if (FilesTransfer.DEIVCE_INVALID == msg.what) {
                    //设备无效，删除之
                    devicesList.remove(msg.arg1);
                }
                // notifyDataSetChanged会执行getView函数，更新所有可视item的数据
//                devicesList.notifyDataSetChanged();
                devicesList.onDataChangedListener.onDataChanged();
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

        void onRemoved(int size);
    }

    @NonNull
    @Override
    public E valueAt(int index) {
        return (E) super.valueAt(index);
    }

    @Override
    public void put(int key, Object value) {
        super.put(key, value);
        if (null != onDataChangedListener) {
            onDataChangedListener.onAdded(this.size());
            onDataChangedListener.onDataChanged();
        }
    }

    @Override
    public void remove(int key) {
        super.remove(key);
        if (null != onDataChangedListener) {
            onDataChangedListener.onRemoved(this.size());
            onDataChangedListener.onDataChanged();
        }
    }
}
