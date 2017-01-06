package cn.mingyuan.pullword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * pullword  http://api.pullword.com/ <br/>
 * pullword get method and post method
 * <p>
 * source [a paragraph of chinese words] for example: source=清华大学是好学校<br/>
 * param1 [threshold] for example: param1=0 to pull all word, param1=1 to pull word with probability with 100%.<br/>
 * param2 [debug] for example: param2=0 debug model is off, param2=1 debug mode in on(show all probabilities of each word)<br/>
 *
 * @author cn.mingyuan@foxmail.com
 * @version 2016/12/30 10:56
 * @since jdk1.8
 */
public class Pullword {
    private static final String BASE_URL_GET = "http://api.pullword.com/get.php";
    private static final String BASE_URL_POST = "http://api.pullword.com/post.php";

    public static class Result {
        public String keyword;
        public double probability;

        public Result(String keyword, double probability) {
            this.keyword = keyword;
            this.probability = probability;
        }

        @Override
        public String toString() {
            return String.format("%s:%f", keyword, probability);
        }
    }

    public static Collection<Result> get(String source, double param1, int param2) throws IOException {
        String result = requestGet(source, param1, param2, "\n").trim();
        if (result.equals("error")) {
            return Collections.emptyList();
        }
        return wrap(result);
    }

    private static String requestGet(String source, double param1, int param2, String delimiter) throws IOException {
        String requestURL = String.format("%s?source=%s&param1=%f&param2=%d", BASE_URL_GET, source, param1, param2);
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            urlConnection = (HttpURLConnection) new URL(requestURL).openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append(delimiter);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignored) {
                }
            }

            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                } catch (Exception ignored) {

                }
            }
        }

        return sb.toString();
    }

    private static Collection<Result> wrap(String result) {
        if (result.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] split = result.split("\n");
        Collection<Result> list = new LinkedList<>();

        for (String string : split) {
            if (string.trim().isEmpty()) {
                continue;
            }
            String[] keywordAndProb = string.split(":");

            if (keywordAndProb.length == 1) {
                list.add(new Result(string, -1));
            } else if (keywordAndProb.length == 2) {
                list.add(new Result(keywordAndProb[0], Double.parseDouble(keywordAndProb[1])));
            }
        }
        return list;
    }

    public static Collection<Result> get(String source) throws IOException {
        return get(source, 0, 0);
    }

    public static String getPlainText(String source) throws IOException {
        String result = requestGet(source, 0, 0, " ").trim();
        if (result.equals("error")) {
            return "";
        }

        return result;
    }

    public static String getPlainText(String source, double param1, int param2) throws IOException {
        String result = requestGet(source, param1, param2, " ");
        if (result.equals("error")) {
            return "";
        }
        return result;
    }

    private static String requestPost(String source, double param1, int param2, String delimiter) throws IOException {
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        String parameters = String.format("source=%s&param1=%f&param2=%d", source, param1, param2);
        try {
            conn = (HttpURLConnection) new URL(BASE_URL_POST).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(parameters.getBytes());
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append(delimiter);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignored) {
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ignored) {
                }
            }
        }
        return sb.toString();
    }

    public static Collection<Result> post(String source, double param1, int param2) throws IOException {
        String result = requestPost(source, param1, param2, "\n").trim();
        if (result.equals("error")) {
            return Collections.emptyList();
        }

        return wrap(result);
    }

    public static Collection<Result> post(String source) throws IOException {
        return post(source, 0, 0);
    }

    public static String postPlainText(String source, double param1, int param2) throws IOException {
        String result = requestPost(source, param1, param2, " ").trim();
        if (result.equals("error")) {
            return "";
        }
        return result;
    }

    public static String postPlainText(String source) throws IOException {
        return postPlainText(source, 0, 0);
    }

}
