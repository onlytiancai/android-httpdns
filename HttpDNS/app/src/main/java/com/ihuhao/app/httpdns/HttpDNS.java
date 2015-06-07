package com.ihuhao.app.httpdns;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class HttpDNS {

    static class CacheEntity {
        private long timestamp;
        private String[] ips;

        public CacheEntity(long timestmp, String[] ips) {
            this.timestamp = timestmp;
            this.ips = ips;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public String[] getIps() {
            return this.ips;
        }
    }

    private static String m_httpdns_url = "http://119.29.29.29/d?dn=";
    private static HashMap<String, SoftReference<CacheEntity>> m_dnscache = new HashMap<String, SoftReference<CacheEntity>>();
    private static long m_defaultTTL = 600 * 1000;


    public static String getStrWithHttpDNS(String strUrl) {
        HttpURLConnection conn = getConnWithHttpDNS(strUrl);
        String ret = null;

        try {
            InputStream is = conn.getInputStream();
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            ret = scanner.hasNext() ? scanner.next() : "";
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return ret;
    }

    public static Bitmap getBitmapWithHttpDNS(String strUrl) {
        Bitmap ret = null;
        HttpURLConnection conn = getConnWithHttpDNS(strUrl);

        try {
            ret = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return ret;
    }

    public static String getAddressByName(String host) {
        String[] ips = null;
        SoftReference<CacheEntity> refer = m_dnscache.get(host);
        if (refer != null)
        {
            CacheEntity entity = refer.get();
            if (entity != null) {
                if ((System.currentTimeMillis() - entity.timestamp) < m_defaultTTL) {
                    ips = entity.getIps();
                    Log.i("cachedns", "get ips from cache:" + host);
                }
            }else{
                Log.i("cachedns", "soft reference miss");
            }
        }

        if (ips == null || ips.length == 0)
        {
            ips = getips(host);
            CacheEntity willadd  = new CacheEntity(System.currentTimeMillis(), ips);
            m_dnscache.put(host, new SoftReference<CacheEntity>(willadd));
            Log.i("cachedns", "add ips to cache:" + host);
        }
        Log.i("cachedns", "get ips:" + host + " ");

        if (ips != null && ips.length > 0) {
            int index = (int) (Math.random() * ips.length);
            return ips[index];
        } else {
            return "";
        }
    }

    private static String[] getips(String host) {
        String[] ret = null;
        HttpURLConnection conn = null;
        try {
            String strUrl = m_httpdns_url + host.trim();
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String rsp = scanner.hasNext() ? scanner.next() : "";
            Log.i("getips", "getips: " + host + " " + rsp);

            if (rsp.indexOf(";") > -1) {
               ret = rsp.split(";");
            }else{
                ret = new String[] {rsp.trim()};
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return ret;
    }


    private static HttpURLConnection getConnWithHttpDNS(String strUrl) {
        HttpURLConnection ret = null;

        try {
            URL url = new URL(strUrl);
            String host = url.getHost();
            String ip = getAddressByName(host);
            URL ipurl = new URL(url.getProtocol(), ip, url.getPath());
            ret = (HttpURLConnection) ipurl.openConnection();
            ret.setRequestProperty("Host", host);
            ret.setConnectTimeout(10 * 1000);
            ret.setReadTimeout(10 * 1000);
            ret.setDoInput(true);
            ret.connect();
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
