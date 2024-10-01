package gov.hibc.logwatcher;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import okhttp3.*;

public class App {
    final MediaType TEXT = MediaType.get("text/plain; charset=utf-8");

    HashMap<String, Long> mfiles = new HashMap<>(); // filename -> lastPosition

    static String url = System.getenv("DynatraceUrl");
    static String token = System.getenv("DynatraceToken");
    static String files = System.getenv("LogFiles");
    static String testing = System.getenv("Testing"); // any value

    // input args can be 1 of the following 3 options:
    // - just the word "test" (requires env vars DynatraceUrl and DynatraceToken)
    // - a single argument which is a comma separated list of logs (requires env vars DynatraceUrl and DynatraceToken)
    // - 3 arguments
    //      0) dynatrace url
    //      1) dynatrace token
    //      2) list of logs
    public static void main(String[] args) throws Exception {
        if (url == null) System.out.println("Env variable DynatraceUrl not found");
        if (token == null) System.out.println("Env variable DynatraceToken not found");
        if (files == null) System.out.println("Env variable LogFiles not found");

        if (url == null || token == null)
        {
            System.out.println("Url (" + url + ") and Token (" + token + ") are required");
            return;
        }

        if (testing != null)
        {
            new App("placeholder").test();
            return;
        }

        if (files == null) 
        {
            System.out.println("LogFiles is required");
            return;
        }

        System.out.println("LogFiles: " + files);
        new App(files).monitor();
    }

    App(String filearg) {
        System.out.println("pooper: " + filearg);
        String[] files = filearg.split(",");
        for (String file : files) {
            mfiles.put(file, 0L);
        }
    }

    void test() throws Exception {
        String log = Instant.now().toString() + " test only...";
        int response = post(log);
        System.out.println("Post response code: " + response);
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
                System.err.println("Error processing file: " + e.getMessage());
            }
        }
    }

    private void processFile(String filePath, long lastpos) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long fileLength = file.length();
            System.out.println("processFile: " + filePath + " length: " + fileLength);

            if (fileLength > lastpos) {
                file.seek(lastpos);
                String line;
                while ((line = file.readLine()) != null) {
                    if (line.length() > 0) {
                        System.out.println(line);
                        int response = post(line);
                        if (response > 399)
                            System.out.println("Response code " + response);
                        mfiles.put(filePath, file.getFilePointer());
                    }
                }
            } 
            else if (fileLength < lastpos) {
                System.out.println("File was truncated or rotated. Resetting position.");
                mfiles.put(filePath, 0L);
            }
        } 
        catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
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