package vision.fastfiletransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vis.FilesList;
import vis.UserFile;
import vis.net.protocol.ReceiveServer;
import vis.net.wifi.WifiHelper;


public class ReceiveActivity extends FragmentActivity {

    private FragmentManager mFragmentManager;
    private ReceiveScanFragment mReceiveScanFragment;
    private ReceiveFragment mReceiveFragment;

    private WifiHelper mWifiHelper;
    private ReceiveServer mReceiveServer;
    private WifiStateChangedReceiver wscr;
    private NetworkStateChangeReceiver nscr;
    private ScanResultsAvailableReceiver srar;
    private boolean isConnected = false;
    private String ssid;
    public FilesList<UserFile> mFilesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_receive);
        getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_titlebar
        );

        mFragmentManager = getSupportFragmentManager();
        Button btnTitleBarLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragmentManager.getBackStackEntryCount() > 0) {
                    mFragmentManager.popBackStack();
                } else {
                    finish();
                }
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("我要接收");

        mWifiHelper = new WifiHelper(this);

        mFilesList = new FilesList<UserFile>();
        mReceiveServer = new ReceiveServer(this, mFilesList);
        mReceiveScanFragment = ReceiveScanFragment.newInstance();

        // 载入第一个Fragment
        jumpToFragment(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isConnected) {
            mReceiveServer.sendLogin(mWifiHelper.getServerAddressByStr());
        } else {
            wscr = new WifiStateChangedReceiver();
            registerReceiver(wscr, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
            mWifiHelper.setWifiEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mReceiveServer.sendLogout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mFFTService.disable();
//        mReceiveWifiManager.disableNetwork();
        mWifiHelper.removeNetwork();

        if (wscr != null) {
            unregisterReceiver(wscr);
        }
        if (srar != null) {     //扫描监听不为空
            unregisterReceiver(srar);
        }
        if (nscr != null) {     //网络状态监听不为空
            unregisterReceiver(nscr);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void jumpToFragment(int fragmentType) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        switch (fragmentType) {
            case 0: {
                fragmentTransaction.replace(R.id.receiveContain, mReceiveScanFragment);
                break;
            }
            case 1: {
                mReceiveFragment = ReceiveFragment.newInstance();
                fragmentTransaction.replace(R.id.receiveContain, mReceiveFragment);
                //隐藏
//                fragmentTransaction.hide(mRMFragment);
//                fragmentTransaction.add(R.id.shareContain, mShareFragment);
                //这里可以回退
//                fragmentTransaction.addToBackStack(null);
                break;
            }
            default: {
                return;
            }
        }
        fragmentTransaction.commit();
    }


    class WifiStateChangedReceiver extends BroadcastReceiver {

        public WifiStateChangedReceiver() {
            mReceiveScanFragment.setTips("正在打开wifi……");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    unregisterReceiver(this);
                    wscr = null;
                    srar = new ScanResultsAvailableReceiver();
                    registerReceiver(srar,
                            new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    mReceiveScanFragment.setTips("正在扫描附近热点…");
                    mWifiHelper.startScan();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
            }
        }
    }

    class ScanResultsAvailableReceiver extends BroadcastReceiver {

        private final String TAG = ScanResultsAvailableReceiver.class.getName();
        private int noFindCount = 0;
        private boolean bln = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            bln = false;
            ArrayList<String> al = mWifiHelper.findSSID("YDZS_*");
            Log.d(TAG, String.valueOf(al.size()));
            for (int i = 0; i < al.size(); i++) {
                ssid = al.get(i);
                mReceiveScanFragment.setTips("尝试连接" + ssid);
                Log.d(TAG, String.valueOf("尝试连接" + ssid));
                if (mWifiHelper.addNetwork(WifiHelper.createWifiCfg(ssid))) {   //尝试加入网络
                    bln = mWifiHelper.enableNetwork(true);      //尝试使能网络
                    Log.d("bln", String.valueOf(bln));
                }
            }
            if (!bln) {         //使能网络不成功
                Log.d("noFindCount", String.valueOf(noFindCount));
                if (++noFindCount < 30) {
                    mReceiveScanFragment.setTips("正在扫描(" + noFindCount + ")…");
                    mWifiHelper.startScan();
                } else {
                    mReceiveScanFragment.setTips("没有发现可以连接的热点(" + noFindCount + ")");
                    Toast.makeText(ReceiveActivity.this, "没有发现AP", Toast.LENGTH_SHORT).show();
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
                Log.d(this.getClass().getName(), String.valueOf(info.getState()));
                isConnected = true;
                Toast.makeText(ReceiveActivity.this, String.valueOf(info.getState()), Toast.LENGTH_SHORT).show();
                jumpToFragment(1);
                mReceiveServer.sendLogin(mWifiHelper.getServerAddressByStr());
            } else if (isConnected && NetworkInfo.State.DISCONNECTED.equals(info.getState()) && !info.isConnected()) {
                Log.d(this.getClass().getName(), String.valueOf(info.getState()));
                jumpToFragment(0);
                isConnected = false;
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
