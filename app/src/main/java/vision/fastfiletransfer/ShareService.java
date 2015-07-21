package vision.fastfiletransfer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import vis.net.protocol.ShareServer;
import vis.net.wifi.APHelper;

public class ShareService extends Service {
    private APHelper mAPHelper;
    public ShareServer mShareServer;

    public ShareService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ShareBinder();
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAPHelper = new APHelper(this);
        if (!mAPHelper.isApEnabled()) {
            //开启AP
            if (mAPHelper.setWifiApEnabled(APHelper.createWifiCfg(APHelper.SSID), true)) {
                Toast.makeText(this, "热点开启", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "打开热点失败", Toast.LENGTH_SHORT).show();
            }
        }
//        mShareServer = new ShareServer(this, mDevicesList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mShareServer.disable();
        //关闭AP
        if (mAPHelper.setWifiApEnabled(null, false)) {
            Toast.makeText(this, "热点关闭", Toast.LENGTH_SHORT).show();
        }
        mAPHelper = null;
        mShareServer = null;
    }

    public void sendFlies(Context context, String[] paths) {
        mShareServer.sendFlies(context, paths);
    }

    public class ShareBinder extends Binder {
        ShareService getService() {
            return ShareService.this;
        }
    }

    public void setActivity(ShareActivity shareActivity) {
        mShareServer = new ShareServer(this, shareActivity.mDevicesList);
        mShareServer.enable();
    }

}
