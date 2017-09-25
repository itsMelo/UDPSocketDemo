package melo.com.udpsocketdemo.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by melo on 2017/9/23.
 */

public class WifiUtil {

    private static final String TAG = "LocationUtils";

    private static volatile WifiUtil instance = null;

    private WifiManager mWifiManager;

    private Context mContext;

    private WifiUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiUtil.class) {
                if (instance == null) {
                    instance = new WifiUtil(context);
                }
            }
        }
        return instance;
    }

    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getLocalIPAddress() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return intToIp(wifiInfo.getIpAddress());
    }

    public String getServerIPAddress() {
        DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
        return intToIp(mDhcpInfo.gateway);
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 24) & 0xFF);
    }



}
