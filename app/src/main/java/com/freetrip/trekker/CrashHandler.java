package com.freetrip.trekker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * 未捕获异常处理类，当程序发生未捕获异常时，由该类来接管程序，并记录发送错误报告
 * 
 * @author mwp
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
    public static final String Tag = "CrashHandler";
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    private SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss");
    // 单例
    private CrashHandler() {
    }
    public static CrashHandler getInstance() {
        return INSTANCE;
    }
    /**
     * 初始化
     * 
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的未捕获异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将该CrashHandler设置为程序默认的处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    /**
     * 当uncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            SystemClock.sleep(3000);
        }
        // 退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     * 
     * @param ex
     * @return true: 如果处理了该异常信息； 否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用toast来显示异常信息
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉，程序出现异常，即将退出", Toast.LENGTH_SHORT)
                        .show();
                Looper.loop();
            };
        }.start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfo2Flie(ex);
        return true;
    }
    /**
     * 收集设备参数信息
     * 
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(Tag, "an error occured when collect package info", e);
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(Tag, field.getName() + ":" + field.get(null));
            } catch (Exception e) {
                Log.e(Tag, "an error occured when collect crash info", e);
            }
        }
    }
    /**
     * 保存错误信息到文件中
     * 
     * @param ex
     * @return 返回文件名称，便于将文件传送到服务器
     */
    private String saveCrashInfo2Flie(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            // 需要在清单文件中添加写SD卡权限
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = "/sdcard/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(Tag, "an error occured while writing file...", e);
            return null;
        }
    }
}  
