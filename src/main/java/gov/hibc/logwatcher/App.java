package gov.hibc.logwatcher;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import org.apache.log4j.Logger;
import okhttp3.*;

public class App {
    final MediaType TEXT = MediaType.get("text/plain; charset=utf-8");

    HashMap<String, Long> mfiles = new HashMap<>(); // filename -> lastPosition

    static String url = System.getenv("DynatraceUrl");
    static String token = System.getenv("DynatraceToken");
    static String files = System.getenv("LogFiles");
    static String testing = System.getenv("Testing"); // any value

    static final Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        logger.info("log watcher startup with " + args.length + " arg(s)");
        if (args.length == 2)
        {
            // assume this is a test
            url = args[0];
            token = args[1];
            new App("placeholder").test();
        }
        else
            mainWithEnvVars(args);
    }

    public static void mainWithEnvVars(String[] args) throws Exception {
        if (url == null) logger.error("Env variable DynatraceUrl not found");
        if (token == null) logger.error("Env variable DynatraceToken not found");
        if (files == null) logger.error("Env variable LogFiles not found");

        if (url == null || token == null)
        {
            logger.info("Url (" + url + ") and Token (" + token + ") are required");
            return;
        }

        if (files == null) 
        {
            logger.error("LogFiles is required");
            return;
        }

        logger.info("LogFiles: " + files);
        new App(files).monitor();
    }

    App(String filearg) {
        String[] files = filearg.split(",");
        for (String file : files) {
            mfiles.put(file, 0L);
        }
    }

    void test() throws Exception {
        String log = Instant.now().toString() + " test only...";
        int response = post(log);
        logger.info("Post response code: " + response);
    }

    void monitor() {
        while (true) {
            try {
                mfiles.forEach((filePath, pos) -> {
                    if (Files.exists(Paths.get(filePath)))
                        processFile(filePath, pos);
                });
                Thread.sleep(10000);
            } 
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } 
            catch (Exception e) {
                logger.error("Error processing file: " + e.getMessage());
            }
        }
    }

    private void processFile(String filePath, long lastpos) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long fileLength = file.length();
            if (fileLength > lastpos) {
                file.seek(lastpos);
                String line;
                while ((line = file.readLine()) != null) {
                    if (line.length() > 0) {
                        int response = post(line);
                        if (response > 399)
                            logger.error("Response code " + response);
                        mfiles.put(filePath, file.getFilePointer());
                    }
                }
            } 
            else if (fileLength < lastpos) {
                logger.info("File was truncated or rotated. Resetting position.");
                mfiles.put(filePath, 0L);
            }
        } 
        catch (Exception e) {
            logger.error("Error reading file: " + filePath + " " + e.getMessage());
        }
    }

    int post(String log) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(log, TEXT);

        Request request = new Request.Builder()
            .url(url.trim())
            .header("User-Agent", "Log Watcher")
            .addHeader("Authorization", "Api-Token " + token.trim())
            .addHeader("Content-Type", "text/plain; charset=utf-8")
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.code();
        }
    }
}