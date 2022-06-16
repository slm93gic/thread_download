import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadThread implements Runnable {

    private HttpURLConnection conn;
    private int threadId;
    private int startPos;
    private int endPos;
    private String fileName;
    private String suffix;

    public DownLoadThread(int threadId, int startPos, int endPos, HttpURLConnection conn, String fileName, String suffix) {
        this.threadId = threadId;
        this.startPos = startPos;
        this.endPos = endPos;
        this.fileName = fileName;
        this.conn = conn;
        this.suffix = suffix;

        try {
            conn.setRequestMethod("GET"); // 设置URL请求的方法（具体参考API）
            conn.setReadTimeout(5000);// 设置500毫秒为超时值
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    private String path;
//    private String suffix;
//
//    private HttpURLConnection conn;
//
//    public DownLoadThread(int threadId, int startPos, int endPos, String path) {
//        super();
//
//        this.path = path;
//        setSuffix();
//        setFileName();
//        try {
//            this.conn = (HttpURLConnection) new URL(path).openConnection();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    /**
     *
     */
//    private void setSuffix() {
////        String[] split = path.split("\\.");
////        this.suffix = split[split.length - 1];
//        this.suffix = "tar.gz";
//    }
//
//    /**
//     *
//     */
//    private void setFileName() {
//        this.fileName = new File(path).getName();
//    }
    @Override
    public void run() {
        try {
            File file = new File(threadId + "." + suffix);
            checkFile(file);

            // 注意双引号内的格式，不能包含空格（等其他字符），否则报416
            conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            RandomAccessFile raf = new RandomAccessFile(fileName, "rwd");// 存储下载文件的随机写入文件
            raf.seek(startPos);// 设置开始下载的位置
            System.out.println("线程" + threadId + ":" + startPos + "~~" + endPos);
            stream(conn, file, raf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param conn
     * @param file
     * @param raf
     */
    private void stream(HttpURLConnection conn, File file, RandomAccessFile raf) {
        try {
            InputStream is = conn.getInputStream();
            byte[] b = new byte[1024 * 1024 * 10];
            int len = -1;
            int newPos = startPos;
            while ((len = is.read(b)) != -1) {
                RandomAccessFile rr = new RandomAccessFile(file, "rwd");// 存储下载标记的文件
                raf.write(b, 0, len);
                // 将下载标记存入指定文档
                String savaPoint = String.valueOf(newPos += len);
                rr.write(savaPoint.getBytes());
                rr.close();
            }
            is.close();
            raf.close();
            System.out.println(file.getName() + "下载完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
    }


    /**
     * 检测文件情况
     *
     * @param file
     */
    private void checkFile(File file) {
        try {
            if (file.exists() && file.length() > 0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String saveStartPos = br.readLine();
                if (saveStartPos != null && saveStartPos.length() > 0) {
                    startPos = Integer.parseInt(saveStartPos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
