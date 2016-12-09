package zmartec.wifidemo;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;
import android.util.Log;


import java.util.List;

public class WiFiAdmin {
    /** * 定义一个WifiManager对象,提供Wifi管理的各种主要API，主要包含wifi的扫描、建立连接、配置信息等 */
    private WifiManager mWifiManager;
    public static final String TAG = "WiFiAdmin";

    // WIFIConfiguration描述WIFI的链接信息，包括SSID、SSID隐藏、password等的设置
    private List<WifiConfiguration> wifiConfigList;

    // 定义一个WifiInfo对象
    private WifiInfo mWifiInfo;

    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;

    // 网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;

    WifiLock mWifiLock;

    public static final int TYPE_NO_PASSWD = 0x11;
    public static final int TYPE_WEP = 0x12;
    public static final int TYPE_WPA = 0x13;

    public WiFiAdmin(Context context) {
        // 获得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /** * 打开wifi */
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /** * 关闭wifi */
    public void closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /** * 检查当前wifi状态 * * @return */
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    /** * 锁定wifiLock */
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    /** * 解锁wifiLock */
    public void releaseWifiLock() {
        // 判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    /** * 创建一个wifiLock */
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    /** * 得到配置好的网络 * * @return */
    public List<WifiConfiguration> getWiFiConfiguration() {
        return mWifiConfigurations;
    }

    /** * 指定配置好的网络进行连接 * * @param index */
    public void connetionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }
        // 连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,true);
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }



    public void goScan() {
        mWifiManager.startScan();
    }

    /** * 得到网络列表 * * @return */
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    /** * 查看扫描结果 * * @return */
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /** * 得到连接的ID * * @return */
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /** * 得到wifiInfo的所有信息 * * @return */
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    /** * 添加一个网络并连接 * * @param configuration */
    public void addNetwork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);
    }

    public WifiConfiguration createWifiInfo(String SSID, String Password,
                                            int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.isExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration isExsits(String ssid) {
        WifiConfiguration config = new WifiConfiguration();

        if (mWifiList.size() > 0) {
            for (int i = 0; i < mWifiList.size(); i++) {
                if (mWifiList.get(i).SSID.equals(ssid)) {

                    config = mWifiConfigurations.get(i);
                }
            }
        }
        return config;
    }

    /** * 断开指定ID的网络 * * @param netId */
    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    // ============
    /** * 连接指定Id的WIFI * * @param wifiId * @return */
    public boolean ConnectWifi(int wifiId) {
        for (int i = 0; i < wifiConfigList.size(); i++) {
            WifiConfiguration wifi = wifiConfigList.get(i);
            if (wifi.networkId == wifiId) {
                while (!(mWifiManager.enableNetwork(wifiId, true))) {// 激活该Id，建立连接
                    Log.i("ConnectWifi",
                            String.valueOf(wifiConfigList.get(wifiId).status));// status:0--已经连接，1--不可连接，2--可以连接
                }
                return true;
            }
        }
        return false;
    }


    public List<ScanResult> GetScan() {
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        return mWifiList;
    }
    /** * 得到Wifi配置好的信息 */
    public void getConfiguration() {
        wifiConfigList = mWifiManager.getConfiguredNetworks();// 得到配置好的网络信息
        if (wifiConfigList == null) {
            return;
        }
        for (int i = 0; i < wifiConfigList.size(); i++) {
            Log.i("getConfiguration", wifiConfigList.get(i).SSID);
            Log.i("getConfiguration",
                    String.valueOf(wifiConfigList.get(i).networkId));
        }
    }

    /** * 判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId * * @param SSID * @return */
    public int IsConfiguration(String SSID) {
        Log.i("IsConfiguration", String.valueOf(wifiConfigList.size()));
        for (int i = 0; i < wifiConfigList.size(); i++) {
            Log.i(wifiConfigList.get(i).SSID,
                    String.valueOf(wifiConfigList.get(i).networkId));
            if (wifiConfigList.get(i).SSID.equals(SSID)) {// 地址相同
                return wifiConfigList.get(i).networkId;
            }
        }
        return -1;
    }

    /** * 添加指定WIFI的配置信息,原列表不存在此SSID * * @param wifiList * @param ssid * @param pwd * @return */
    public int AddWifiConfig(List<ScanResult> wifiList, String ssid, String pwd) {
        int wifiId = -1;
        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult wifi = wifiList.get(i);
            if (wifi.SSID.equals(ssid)) {
                Log.i("AddWifiConfig", "equals");
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.SSID = "\"" + wifi.SSID + "\"";// \"转义字符，代表"
                wifiCong.preSharedKey = "\"" + pwd + "\"";// WPA-PSK密码
                wifiCong.hiddenSSID = false;
                wifiCong.status = WifiConfiguration.Status.ENABLED;
                wifiId = mWifiManager.addNetwork(wifiCong);// 将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
                if (wifiId != -1) {
                    return wifiId;
                }
            }
        }
        return wifiId;
    }

    public enum WifiCipherType {
        WIFICIPHER_WEP,
        WIFICIPHER_WPA,
        WIFICIPHER_NOPASS,
        WIFICIPHER_INVALID
    }

    public void connect(String ssid, String password, WifiCipherType type) {
        Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
        thread.start();
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    class ConnectRunnable implements Runnable{
        private String mSsid;
        private String mPassword;
        private WifiCipherType mType;

        public ConnectRunnable(String ssid, String password, WifiCipherType type) {
            mSsid = ssid;
            mPassword = password;
            mType = type;
        }

        @Override
        public void run() {
            int iLoop = 0;

            openWifi();

            while (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Log.d(TAG, "Thread wait enableing InterruptedException!");
                }

                if(iLoop++ > 1000){
                    Log.d(TAG, "Thread wait too long time exit!");
                    return;
                }else{
                    Log.d(TAG, "WifiState:"+mWifiManager.getWifiState());
                }
            }

            WifiConfiguration wifiConfig = createWifiInfo(mSsid, mPassword, mType);

            if (wifiConfig == null) {
                Log.d(TAG, "wifiConfig is null!");
                return;
            }

            int netID = mWifiManager.addNetwork(wifiConfig);

            boolean enabled = mWifiManager.enableNetwork(netID, true);
            Log.d(TAG, "enableNetwork status enable=" + enabled);

            boolean connected = mWifiManager.reconnect();
            Log.d(TAG, "enableNetwork connected=" + connected);

        }
    }

    private static boolean isHexWepKey(String wepKey){
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key){
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }

        return true;
    }
}