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
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import vis.FilesList;
import vis.UserFile;
import vis.net.protocol.ReceiveServer;
import vis.net.wifi.WifiHelper;
import vision.resourcemanager.AdapterAP;
import vision.resourcemanager.RMGridFragment;

public class ReceiveService extends Service {
    private WifiStateChangedReceiver wscr;
    private NetworkStateChangeReceiver nscr;
    private ScanResultsAvailableReceiver srar;

    private WifiHelper mWifiHelper;
    private ReceiveServer mReceiveServer;

    /**
     * 是否已经连接上AP
     */
    private boolean isConnected = false;
    /**
     * 是否已经使能AP成功
     */
    private boolean isEnabledNetwork = false;
    /**
     * 是否多AP
     */
    private boolean isMultipleAP = false;

    private ReceiveFragment mReceiveFragment;
    private ReceiveScanFragment mReceiveScanFragment;
    private ReceiveActivity mActivity;

    private ArrayList<String> mAllAP;
    private String ssid;
    private FragmentManager mFragmentManager;
    private RMGridFragment mMultipleResultsFragment;

    public ReceiveService() {
    }

    public void setActivity(ReceiveActivity activity) {
        this.mActivity = activity;
        mFragmentManager = mActivity.getSupportFragmentManager();
        // 载入第一个Fragment
        if (null == mReceiveScanFragment) {
            mReceiveScanFragment = ReceiveScanFragment.newInstance();
        }
        jumpToFragment(R.id.receiveContain, mReceiveScanFragment, false);
    }

    public void setFilesList(FilesList<UserFile> filesList) {
        mReceiveServer = new ReceiveServer(this, filesList);
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
        Log.d("service", "onCreate()");

        mWifiHelper = new WifiHelper(this);
        wscr = new WifiStateChangedReceiver();

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("service", "onBind()");
        return new ReceiveBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("service", "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("service", "onDestroy()");

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

    /**
     * 连接AP
     *
     * @param ssid 要连接的SSID
     */
    public void connectAP(String ssid) {
        if (isConnected) {
            return;
        }
        Log.d("connectAP()", String.valueOf("尝试连接" + ssid));
        if (null == mReceiveScanFragment) {
            mReceiveScanFragment = ReceiveScanFragment.newInstance();
        }
        jumpToFragment(R.id.receiveContain, mReceiveScanFragment, false);
        if (mWifiHelper.addNetwork(WifiHelper.createWifiCfg(ssid))) {   //尝试加入网络
            isEnabledNetwork = mWifiHelper.enableNetwork(true);      //尝试使能网络
            Log.d("isEnabledNetwork", String.valueOf(isEnabledNetwork));
        }
        if (isEnabledNetwork) {         //成功使能网络
            this.ssid = ssid;
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

    /**
     * 跳到新的fragment
     *
     * @param containerViewId  容器view的ID
     * @param fragment         要跳到的新fragment
     * @param isAddToBackStack 是否要加入回退栈
     */
    public void jumpToFragment(@IdRes int containerViewId, Fragment fragment, boolean isAddToBackStack) {
        if (fragment.isResumed()) {
            return;
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(containerViewId, fragment).commit();
    }

    class WifiStateChangedReceiver extends BroadcastReceiver {

        public WifiStateChangedReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLING:
//                    Log.d("onReceive()", "WIFI_STATE_DISABLING");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
//                    Log.d("onReceive()", "WIFI_STATE_DISABLED");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
//                    Log.d("onReceive()", "WIFI_STATE_ENABLING");
                    if (null != mReceiveScanFragment) {
                        mReceiveScanFragment.setTips("正在打开wifi……");
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
//                    Log.d("onReceive()", "WIFI_STATE_ENABLED");
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
        /**
         * 搜索不到目标AP的计数
         */
        private int noFindCount = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
//            isEnabledNetwork = false;
            if (!isEnabledNetwork) {
                mAllAP = mWifiHelper.findSSID("YDZS_*");
                Log.d(TAG, "found:" + String.valueOf(mAllAP.size()));

                if (mAllAP.size() == 0) {
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
                } else if (mAllAP.size() == 1) {
                    //如果已经进入用户选择fragment，但新发现只有一个AP，返回原来fragment继续
                    if (isMultipleAP) {
                        //状态改变
                        isMultipleAP = false;
                    }
                    //当只有一个AP的时候，直接连接这个AP
                    connectAP(mAllAP.get(0));
                } else if (mAllAP.size() > 1) {
                    //当有存在不止一个AP的时候
                    if (!isMultipleAP) {
                        //状态改变，跳到新fragment让用户选择要连接的AP
                        if (null == mMultipleResultsFragment) {
                            mMultipleResultsFragment = new RMGridFragment();
                        }
                        if (null == mMultipleResultsFragment.getGridAdapter()) {
                            mMultipleResultsFragment.setGridAdapter(new AdapterAP(ReceiveService.this, mAllAP));
                        }
                        jumpToFragment(R.id.receiveContain, mMultipleResultsFragment, false);
                        isMultipleAP = true;
                    } else {
                        mMultipleResultsFragment.setGridAdapter(new AdapterAP(ReceiveService.this, mAllAP));
                    }
                }
//                for (int i = 0; i < mAllAP.size(); i++) {
//                    ssid = mAllAP.get(i);
//                    mReceiveScanFragment.setTips("尝试连接" + ssid);
//                    if (mWifiHelper.addNetwork(WifiHelper.createWifiCfg(ssid))) {   //尝试加入网络
//                        isEnabledNetwork = mWifiHelper.enableNetwork(true);      //尝试使能网络
//                        Log.d("isEnabledNetwork", String.valueOf(isEnabledNetwork));
//                    }
//                }
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
                if (null == mReceiveFragment) {
                    mReceiveFragment = ReceiveFragment.newInstance();
                }
                jumpToFragment(R.id.receiveContain, mReceiveFragment, false);
                sendLogin();
                mReceiveFragment.setTitle("已连接：" + ssid.substring(5, ssid.length() - 6));
            } else if (isConnected && NetworkInfo.State.DISCONNECTED.equals(info.getState()) && !info.isConnected()) {
                Log.d("NetworkChange", String.valueOf(info.getState()));
                if (null == mReceiveScanFragment) {
                    mReceiveScanFragment = ReceiveScanFragment.newInstance();
                }
                jumpToFragment(R.id.receiveContain, mReceiveScanFragment, false);
                isConnected = false;
                isEnabledNetwork = false;
                mWifiHelper.startScan();
//                mFFTService.disable();
//                unregisterReceiver(this);
//                nscr = null;
//                Toast.makeText(ReceiveActivity.this, String.valueOf(info.getState()), Toast.LENGTH_SHORT).show();
//                tvTips.setText("断开了");
//                mReceiveWifiManager.recoveryNetwork();
            }
        }
    }

}
