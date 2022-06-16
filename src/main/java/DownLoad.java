import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoad {

    public static void main(String[] args) {
        doTask();
    }

    //mysql的下载地址
//    public static final String PATH1 = "https://cdn.mysql.com/archives/mysql-8.0/mysql-8.0.25-winx64.zip";
    public static final String PATH = "https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-3.3.1/hadoop-3.3.1-src.tar.gz";
    // 自定义线程数量
    public static final int threadCount = 10;

    public static void doTask() {
        String fileName = new File(PATH).getName();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(PATH).openConnection();
            //获取文件大小
            int fileLength = conn.getContentLength();

            // 在本地创建一个与服务器大小一致的可随机写入文件
//            RandomAccessFile raf = new RandomAccessFile(fileName, "rwd");
//            raf.setLength(fileLength);

            thread2Load(conn, fileLength, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动线程下载
     */
    private static void thread2Load(HttpURLConnection conn, int fileLength, String fileName) {
        int blockSize = fileLength / threadCount; // 计算每条线程下载数据的大小
        for (int threadId = 1; threadId <= threadCount; threadId++) {
            int startPos = (threadId - 1) * blockSize;// 开始下载的位置
            int endPos = (threadId * blockSize) - 1;// 结束下载的位置（不包含最后一块）
            if (threadCount == threadId) {
                endPos = fileLength;
            }
//            new Thread(new DownLoadThread(threadId, startPos, endPos, PATH)).start();

            DownLoadThread download = new DownLoadThread(threadId, startPos, endPos, conn, fileName, ".tar.gz");
            new Thread(download).start();

        }

    }


}