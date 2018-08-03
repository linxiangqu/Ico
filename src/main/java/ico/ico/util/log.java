package ico.ico.util;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ico.ico.ico.BaseApplication;

/**
 * Created by admin on 2015/4/21.
 */
public class log {
    final static String COMMON_TAG = "ico_";
    /**
     * 日志等级,从e到v依次为1到5，若输出全关则设置0
     * out等同i，err等同e
     */
    final static int LEVEL = 5;
    /**
     * 每次日志输入的最大长度,如果太大将分段输出
     */
    final static int MAX_SIZE = 200;
    //用于存储错误日志的保存地址
    public static String LOG;

    static {
        LOG = BaseApplication.getInstance().getDir("error", android.content.Context.MODE_APPEND).getAbsolutePath() + "/error.log";
    }

    public static void v(String msg, String... tags) {
        if (LEVEL < 5) {
            return;
        }
        String tag = COMMON_TAG + "v_" + Common.concat("_", tags);
        List<String> data = Common.split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.v(tag, data.get(i));
        }
    }

    public static void d(String msg, String... tags) {
        if (LEVEL < 4) {
            return;
        }
        String tag = COMMON_TAG + "d_" + Common.concat("_", tags);
        List<String> data = Common.split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.d(tag, data.get(i));
        }
    }

    public static void i(String msg, String... tags) {
        if (LEVEL < 3) {
            return;
        }
        String tag = COMMON_TAG + "i_" + Common.concat("_", tags);
        List<String> data = Common.split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.i(tag, data.get(i));
        }
    }

    public static void w(String msg, String... tags) {
        if (LEVEL < 2) {
            return;
        }
        String tag = COMMON_TAG + "w_" + Common.concat("_", tags);
        List<String> data = Common.split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.w(tag, data.get(i));
        }
    }

    public static void e(String msg, String... tags) {
        if (LEVEL < 1) {
            return;
        }
        String tag = COMMON_TAG + "e_" + Common.concat("_", tags);
        List<String> data = Common.split(msg, MAX_SIZE);
        for (int i = 0; i < data.size(); i++) {
            Log.e(tag, data.get(i));
        }
    }


    public static void ee(String msg, String... tags) {
        if (LEVEL < 1) {
            return;
        }
        String tag = COMMON_TAG + Common.concat("_", tags);
        Log.e(tag, msg);
        //获取当前时间并格式化，不适用DateUtil是为了降低耦合性
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String currentDate = sdf.format(new Date());
        //将错误信息写入错误日志中
        final String text = String.format("%s   %s  %s\n", currentDate, tag, msg);
        new IcoThread() {
            @Override
            public void run() {
                try {
                    writeFile(text, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void out(String msg, String... tags) {
        if (LEVEL < 3) {
            return;
        }
        String tag = COMMON_TAG + Common.concat("_", tags);
        System.out.println(tag + "," + msg);
    }

    public static void err(String msg, String... tags) {
        if (LEVEL < 1) {
            return;
        }
        String tag = COMMON_TAG + Common.concat("_", tags);
        System.err.println(tag + "," + msg);
    }

    /**
     * 读取错误日志中的文本，用于开发时进行查看
     */
    public static void print() {
        //读取错误日志数据，只是为了开发方便
        try {
            List<Byte> list = Common.readFile(new File(LOG));
            String str = Common.bytes2Str(list);
            log.i(str, "error.log");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向错误日志中写入数据
     *
     * @param text
     * @param isAppend
     */
    public static void writeFile(String text, boolean isAppend) throws IOException {
        File file = new File(LOG);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(file, isAppend);
            if (isAppend) {
                fw.write(text + "\n");
            } else {
                fw.write(text + "\n");
            }
            fw.flush();
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
        log.out("已写入" + LOG);
    }
}
