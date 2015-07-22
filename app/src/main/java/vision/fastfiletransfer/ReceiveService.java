package vision.fastfiletransfer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import vis.FilesList;
import vis.UserFile;
import vis.net.protocol.ReceiveServer;
import vis.net.wifi.WifiHelper;

public class ReceiveService extends Service {
    private WifiStateChangedReceiver wscr;
    private NetworkStateChangeReceiver nscr;
    private ScanResultsAvailableReceiver srar;

    private WifiHelper mWifiHelper;
    private ReceiveServer mReceiveServer;

    private boolean isConnected = false;
    private boolean isEnabledNetwork = false;

    private FilesList<UserFile> mFilesList;


    private ReceiveScanFragment mReceiveScanFragment;
    private ReceiveActivity mActivity;

    public ReceiveService() {
    }

    public void setActivity(ReceiveActivity activity) {
        this.mActivity = activity;
    }

    public void setFilesList(FilesList<UserFile> filesList) {
        mFilesList = filesList;
        mReceiveServer = new ReceiveServer(this, mFilesList);
    }

    public void setFragment(ReceiveScanFragment receiveScanFragment) {
        mReceiveScanFragment = receiveScanFragment;
        registerReceiver(wscr, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        mWifiHelper.setWifiEnabled(true);
    }

    public class ReceiveBinder extends Binder {
        ReceiveService getService() {
            return ReceiveService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("service", "onCreate()");

        mWifiHelper = new WifiHelper(this);
        wscr = new WifiStateChangedReceiver();

    }

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d("service", "onBind()");
        return new ReceiveBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("service", "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        Log.d("service", "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d("service", "onDestroy()");

        mWifiHelper.removeNetwork();
        mWifiHelper.operateAllNetwork(true);

        if (wscr != null) {
            unregisterReceiver(wscr);
            wscr = null;
        }
        if (srar != null) {     //扫描监听不为空
            unregisterReceiver(srar);
            srar = null;
        }
        if (nscr != null) {     //网络状态监听不为空
            unregisterReceiver(nscr);
            nscr = null;
        }

    }

    public void sendLogin() {
        if (isConnected) {
            mReceiveServer.sendLogin(mWifiHelper.getServerAddressByStr());
        }
    }

    public void sendLogout() {
        if (isConnected) {
            mReceiveServer.sendLogout();
        }
    }

    class WifiStateChangedReceiver extends BroadcastReceiver {

        public WifiStateChangedReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLING:
                    Log.d("onReceive()", "WIFI_STATE_DISABLING");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.d("onReceive()", "WIFI_STATE_DISABLED");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Log.d("onReceive()", "WIFI_STATE_ENABLING");
                    if (null != mReceiveScanFragment) {
                        mReceiveScanFragment.setTips("正在打开wifi……");
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.d("onReceive()", "WIFI_STATE_ENABLED");
//                    unregisterReceiver(this);
//                    wscr = null;
                    srar = new ScanResultsAvailableReceiver();
                    registerReceiver(srar,
                            new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    if (null != mReceiveScanFragment) {
                        mReceiveScanFragment.setTips("正在扫描附近热点…");
                    }
                    mWifiHelper.operateAllNetwork(false);
                    mWifiHelper.startScan();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Log.d("onReceive()", "WIFI_STATE_UNKNOWN");
                    break;
            }
        }
    }

    class ScanResultsAvailableReceiver extends BroadcastReceiver {

        private final String TAG = "Scan";
        private int noFindCount = 0;
        private String ssid;

        @Override
        public void onReceive(Context context, Intent intent) {
//            isEnabledNetwork = false;
            if (!isEnabledNetwork) {
                ArrayList<String> al = mWifiHelper.findSSID("YDZS_*");
                Log.d(TAG, "found:" + String.valueOf(al.size()));
                for (int i = 0; i < al.size(); i++) {
                    ssid = al.get(i);
                    mReceiveScanFragment.setTips("尝试连接" + ssid);
                    Log.d(TAG, String.valueOf("尝试连接" + ssid));
                    if (mWifiHelper.addNetwork(WifiHelper.createWifiCfg(ssid))) {   //尝试加入网络
                        isEnabledNetwork = mWifiHelper.enableNetwork(true);      //尝试使能网络
                        Log.d("isEnabledNetwork", String.valueOf(isEnabledNetwork));
                    }
                }
            }
            if (!isEnabledNetwork) {         //使能网络不成功
                Log.d("noFindCount", String.valueOf(noFindCount));
                if (++noFindCount < 30) {
                    mReceiveScanFragment.setTips("正在扫描(" + noFindCount + ")…");
                    mWifiHelper.startScan();
                } else {
                    mReceiveScanFragment.setTips("没有发现可以连接的热点(" + noFindCount + ")");
                    Toast.makeText(ReceiveService.this, "没有发现AP", Toast.LENGTH_SHORT).show();
//                    srar = null;
//                    unregisterReceiver(this);
                }
            } else {        //成功使能网络
                //注销搜索广播接收
//                unregisterReceiver(srar);
//                srar = null;
//                Toast.makeText(ReceiveActivity.this, "使能网络成功", Toast.LENGTH_SHORT).show();

                //注册接收网络变化
                if (nscr == null) {
                    nscr = new NetworkStateChangeReceiver();
                    registerReceiver(nscr, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
                }
            }
        }
    }

    class NetworkStateChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (NetworkInfo.State.CONNECTED.equals(info.getState()) && info.isConnected()) {
                Log.d("NetworkChange", String.valueOf(info.getState()));
                isConnected = true;
                Toast.makeText(ReceiveService.this, String.valueOf(info.getState()), Toast.LENGTH_SHORT).show();
                mActivity.jumpToFragment(1);
                sendLogin();
            } else if (isConnected && NetworkInfo.State.DISCONNECTED.equals(info.getState()) && !info.isConnected()) {
                Log.d("NetworkChange", String.valueOf(info.getState()));
                mActivity.jumpToFragment(0);
                isConnected = false;
                isEnabledNetwork = false;
                mWifiHelper.startScan();
//                mFFTService.disable();
//                isConnected = false;
//                unregisterReceiver(this);
//                nscr = null;
//                Toast.makeText(ReceiveActivity.this, String.valueOf(info.getState()), Toast.LENGTH_SHORT).show();
//                tvTips.setText("断开了");
//                mReceiveWifiManager.recoveryNetwork();
            }
        }
    }

}
