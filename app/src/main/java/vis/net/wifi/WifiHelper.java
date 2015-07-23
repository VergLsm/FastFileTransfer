package vis.net.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vision on 15/6/24.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class WifiHelper {
    //定义WifiManager对象
    private WifiManager mWifiManager;
    /**
     * 目标AP的NetID
     */
    private int targetNetID = -1;

    public WifiHelper(Context context) {
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 打开/关闭WIFI
     */
    public boolean setWifiEnabled(boolean isEnable) {
//        if (isEnable != mWifiManager.isWifiEnabled()) {
        return mWifiManager.setWifiEnabled(isEnable);
//        }
    }

    public boolean operateAllNetwork(boolean operate) {
        boolean operationSucceeded = false;
        List<WifiConfiguration> configured = mWifiManager.getConfiguredNetworks();
        if (null == configured) {
            return operationSucceeded;
        }
        for (WifiConfiguration wifiConfiguration : configured) {
            if (operate) {
                if (mWifiManager.enableNetwork(wifiConfiguration.networkId, false)) {
                    operationSucceeded = true;
                }
            } else {
                if (mWifiManager.disableNetwork(wifiConfiguration.networkId)) {
                    operationSucceeded = true;
                }
            }
        }
        return operationSucceeded;
    }

    public boolean startScan() {
        return mWifiManager.startScan();
    }

    public boolean addNetwork(WifiConfiguration wifiCfg) {
        //添加一个网络并连接
        targetNetID = mWifiManager.addNetwork(wifiCfg);
        return targetNetID != -1;
    }

    /**
     * 登入网络
     */
    public boolean enableNetwork(boolean b) {
//        mWifiManager.disconnect();
        //加入网络
        return mWifiManager.enableNetwork(targetNetID, b);
    }

    // 禁用一个链接
    public boolean disableNetWordLick(int NetId) {
        mWifiManager.disableNetwork(NetId);
        return mWifiManager.disconnect();
    }

    /**
     * 除移指定ID的网络
     */
    public void removeNetwork() {
        if (-1 != targetNetID) {
            mWifiManager.removeNetwork(targetNetID);
        }
    }

    public static WifiConfiguration createWifiCfg(String ssid) {
        WifiConfiguration wifiCfg = new WifiConfiguration();
//        wifiCfg.SSID = ssid;
        wifiCfg.SSID = "\"" + ssid + "\"";
        wifiCfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        wifiCfg.preSharedKey = "abcdefgh";
        return wifiCfg;
    }

    /**
     * 匹配SSID
     *
     * @param pattern 正则表达式
     * @return 如果匹配成功返回SSID，如果不成功返回Null
     */
//    public String findSSID(String pattern) {
    public ArrayList<String> findSSID(String pattern) {
        ArrayList<String> al = new ArrayList<String>();
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        Pattern p = Pattern.compile(pattern);
        for (int i = 0; i < wifiList.size(); i++) {
            //匹配SSID
            Matcher m = p.matcher(wifiList.get(i).SSID);
            if (m.find()) {//匹配成功
//                foundSSID = wifiList.get(i).SSID;
                al.add(wifiList.get(i).SSID);
//                break;
            }
        }
//        return foundSSID;
        return al;
    }

    public DhcpInfo getDhcpInfo() {
        return mWifiManager.getDhcpInfo();
    }

    public String getServerAddressByStr() {
        return intToIp(getServerAddressByInt());
    }

    public int getServerAddressByInt() {
        return getDhcpInfo().serverAddress;
    }

    /**
     * 把IP地址从int转换成String
     *
     * @param i int 型的IP地址
     * @return String 型的IP地址，如255.255.255.255
     */
    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 24) & 0xFF);
    }


}
