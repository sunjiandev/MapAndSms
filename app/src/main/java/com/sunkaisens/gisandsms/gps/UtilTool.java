package com.sunkaisens.gisandsms.gps;

/**
 * Created by sun on 2017/9/22.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.sunkaisens.gisandsms.MyApp;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UtilTool {


    private static UtilTool utilTool;
    private static Context context;

    private UtilTool() {
    }


    public static UtilTool getUtilTool(Context context) {

        UtilTool.context = context;

        if (utilTool == null) {
            synchronized (UtilTool.class) {
                if (utilTool == null) {
                    utilTool = new UtilTool();
                }
            }
        }
        return utilTool;
    }

    /**
     * 根据基站信息获取经纬度
     * <p>
     * 原理向http://www.google.com/loc/json发送http的post请求，根据google返回的结果获取经纬度
     *
     * @param cellIds call
     * @return
     * @throws Exception
     */
    public Location callGear(Context context, ArrayList<CellInfo> cellIds) throws Exception {
        String result = "";
        JSONObject data = null;
        if (cellIds == null || cellIds.size() == 0) {
            return null;
        }

        try {
            result = getResponseResult(context, "http://www.google.com/loc/json", cellIds);

            if (result.length() <= 1)
                return null;
            data = new JSONObject(result);
            data = (JSONObject) data.get("location");

            Location loc = new Location(LocationManager.NETWORK_PROVIDER);
            loc.setLatitude((Double) data.get("latitude"));
            loc.setLongitude((Double) data.get("longitude"));
            loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
            loc.setTime(getUTCTime());
            return loc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 接收Google返回的数据格式
     * <p>
     * 出参：{"location":{"latitude":26.0673834,"longitude":119.3119936,
     * "address":{"country":"ä¸­å½","country_code":"CN","region":"ç¦å»ºç","city":"ç¦å·å¸",
     * "street":"äºä¸ä¸­è·¯","street_number":"128å·"},"accuracy":935.0},
     * "access_token":"2:xiU8YrSifFHUAvRJ:aj9k70VJMRWo_9_G"}
     * 请求路径：http://maps.google.cn/maps/geo?key=abcdefg&q=26.0673834,119.3119936
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     * @throws Exception
     * @param\
     */
    private String getResponseResult(Context ctx, String path, ArrayList<CellInfo> cellInfos)
            throws UnsupportedEncodingException, MalformedURLException,
            IOException, ProtocolException, Exception {
        String result = "";
        Log.i(ctx.getApplicationContext().getClass().getSimpleName(),
                "in param: " + getRequestParams(cellInfos));
        InputStream inStream = sendPostRequest(path,
                getRequestParams(cellInfos), "UTF-8");
        if (inStream != null) {
            byte[] datas = readInputStream(inStream);
            if (datas != null && datas.length > 0) {
                result = new String(datas, "UTF-8");
                //Log.i(ctx.getClass().getSimpleName(), "receive result:"+result);//服务器返回的结果信息
                Log.d(ctx.getApplicationContext().getClass().getSimpleName(),
                        "google cell receive data result:" + result);
            } else {
                Log.i(ctx.getApplicationContext().getClass().getSimpleName(),
                        "google cell receive data null");
            }
        } else {
            Log.i(ctx.getApplicationContext().getClass().getSimpleName(),
                    "google cell receive inStream null");
        }
        return result;
    }


    /**
     * 拼装json请求参数，拼装基站信息
     * <p>
     * 入参：{'version': '1.1.0','host': 'maps.google.com','home_mobile_country_code': 460,
     * 'home_mobile_network_code': 14136,'radio_type': 'cdma','request_address': true,
     * 'address_language': 'zh_CN','cell_towers':[{'cell_id': '12835','location_area_code': 6,
     * 'mobile_country_code': 460,'mobile_network_code': 14136,'age': 0}]}
     *
     * @param cellInfos
     * @return
     */
    private String getRequestParams(List<CellInfo> cellInfos) {
        StringBuffer sb = new StringBuffer("");
        sb.append("{");
        if (cellInfos != null && cellInfos.size() > 0) {
            sb.append("'version': '1.1.0',"); //google api 版本[必]
            sb.append("'host': 'maps.google.com',"); //服务器域名[必]
            sb.append("'home_mobile_country_code': " + cellInfos.get(0).getMobileCountryCode() + ","); //移动用户所属国家代号[选 中国460]
            sb.append("'home_mobile_network_code': " + cellInfos.get(0).getMobileNetworkCode() + ","); //移动系统号码[默认0]
            sb.append("'radio_type': '" + cellInfos.get(0).getRadioType() + "',"); //信号类型[选 gsm|cdma|wcdma]
            sb.append("'request_address': true,"); //是否返回数据[必]
            sb.append("'address_language': 'zh_CN',"); //反馈数据语言[选 中国 zh_CN]
            sb.append("'cell_towers':["); //移动基站参数对象[必]
            for (CellInfo cellInfo : cellInfos) {
                sb.append("{");
                sb.append("'cell_id': '" + cellInfo.getCellId() + "',"); //基站ID[必]
                sb.append("'location_area_code': " + cellInfo.getLocationAreaCode() + ","); //地区区域码[必]
                sb.append("'mobile_country_code': " + cellInfo.getMobileCountryCode() + ",");
                sb.append("'mobile_network_code': " + cellInfo.getMobileNetworkCode() + ",");
                sb.append("'age': 0"); //使用好久的数据库[选 默认0表示使用最新的数据库]
                sb.append("},");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 获取UTC时间
     * <p>
     * UTC + 时区差 ＝ 本地时间(北京为东八区)
     *
     * @return
     */
    private long getUTCTime() {
        //取得本地时间
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //取得时间偏移量
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        //取得夏令时差
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        //从本地时间里扣除这些差量，即可以取得UTC时间
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return cal.getTimeInMillis();
    }

    /**
     * 初始化，记得放在onCreate()方法里初始化，获取基站信息
     *
     * @return
     */
    public ArrayList<CellInfo> init(Context ctx) {
        ArrayList<CellInfo> cellInfos = new ArrayList<CellInfo>();

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //网络制式
        int type = tm.getNetworkType();
        /**
         * 获取SIM卡的IMSI码
         * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        String imsi = tm.getSubscriberId();
        //为了区分移动、联通还是电信，推荐使用imsi来判断(万不得己的情况下用getNetworkType()判断，比如imsi为空时)
        if (imsi != null && !"".equals(imsi)) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
                // 中国移动
                mobile(cellInfos, tm);
            } else if (imsi.startsWith("46001")) {
                // 中国联通
//                union(cellInfos, tm);
            } else if (imsi.startsWith("46003")) {
                // 中国电信
//                cdma(cellInfos, tm);
            }
        } else {
            // 在中国，联通的3G为UMTS或HSDPA，电信的3G为EVDO
            // 在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA
            // String OperatorName = tm.getNetworkOperatorName();

            //中国电信
            if (type == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || type == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || type == TelephonyManager.NETWORK_TYPE_CDMA
                    || type == TelephonyManager.NETWORK_TYPE_1xRTT) {
//                cdma(cellInfos, tm);
            }
            //移动(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
            else if (type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_GPRS) {

                try {

                    mobile(cellInfos, tm);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.d("sjy", "由于没有手机卡导致的空指针异常");
                }
            }
            //联通(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
            else if (type == TelephonyManager.NETWORK_TYPE_GPRS
                    || type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_UMTS
                    || type == TelephonyManager.NETWORK_TYPE_HSDPA) {
//                union(cellInfos, tm);
            }
        }

        return cellInfos;
    }


    /**
     * 电信
     *
     * @param cellInfos
     * @param tm
     */

    private void cdma(ArrayList<CellInfo> cellInfos, TelephonyManager tm) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        info.setCellId(location.getBaseStationId());
        info.setLocationAreaCode(location.getNetworkId());
        info.setMobileNetworkCode(String.valueOf(location.getSystemId()));
        info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        info.setRadioType("cdma");
        cellInfos.add(info);

        //前面获取到的都是单个基站的信息，接下来再获取周围邻近基站信息以辅助通过基站定位的精准性
        // 获得邻近基站信息
        List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(list.get(i).getCid());
            cell.setLocationAreaCode(location.getNetworkId());
            cell.setMobileNetworkCode(String.valueOf(location.getSystemId()));
            cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
            cell.setRadioType("cdma");
            cellInfos.add(cell);
        }
    }


    /**
     * 移动
     *
     * @param cellInfos
     * @param tm
     */
    private void mobile(ArrayList<CellInfo> cellInfos,
                        TelephonyManager tm) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        info.setCellId(location.getCid());
        info.setLocationAreaCode(location.getLac());
        info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
        info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        info.setRadioType("gsm");
        cellInfos.add(info);

        //前面获取到的都是单个基站的信息，接下来再获取周围邻近基站信息以辅助通过基站定位的精准性
        // 获得邻近基站信息
        List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(list.get(i).getCid());
            cell.setLocationAreaCode(location.getLac());
            cell.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
            cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
            cell.setRadioType("gsm");
            cellInfos.add(cell);
        }
    }


    /**
     * 联通
     *
     * @param cellInfos
     * @param tm
     */
    private void union(ArrayList<CellInfo> cellInfos, TelephonyManager tm) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        //经过测试，获取联通数据以下两行必须去掉，否则会出现错误，错误类型为JSON Parsing Error
        //info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
        //info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        info.setCellId(location.getCid());
        info.setLocationAreaCode(location.getLac());
        info.setMobileNetworkCode("");
        info.setMobileCountryCode("");
        info.setRadioType("gsm");
        cellInfos.add(info);
        List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(list.get(i).getCid());
            cell.setLocationAreaCode(location.getLac());
            cell.setMobileNetworkCode("");
            cell.setMobileCountryCode("");
            cell.setRadioType("gsm");
            cellInfos.add(cell);
        }
    }

    /**
     * 发送post请求，返回输入流
     *
     * @param path     访问路径
     * @param params   json数据格式
     * @param encoding 编码
     * @return
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     */
    private InputStream sendPostRequest(String path, String params, String encoding)
            throws UnsupportedEncodingException, MalformedURLException,
            IOException, ProtocolException {
        byte[] data = params.getBytes(encoding);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        //application/x-javascript text/xml->xml数据 application/x-javascript->json对象 application/x-www-form-urlencoded->表单数据
        conn.setRequestProperty("Content-Type", "application/x-javascript; charset=" + encoding);
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setConnectTimeout(5 * 1000);
        OutputStream outStream = conn.getOutputStream();
        outStream.write(data);
        outStream.flush();
        outStream.close();
        if (conn.getResponseCode() == 200)
            return conn.getInputStream();
        return null;
    }

    /**
     * 发送get请求
     *
     * @param path 请求路径
     * @return
     * @throws Exception
     */
    private String sendGetRequest(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        byte[] data = readInputStream(inStream);
        String result = new String(data, "UTF-8");
        return result;
    }

    /**
     * 从输入流中读取数据
     *
     * @param inStream liu
     * @return
     * @throws Exception
     */
    private byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//网页的二进制数据
        outStream.close();
        inStream.close();
        return data;
    }


}
