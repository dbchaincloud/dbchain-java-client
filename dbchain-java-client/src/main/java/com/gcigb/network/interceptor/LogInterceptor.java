package com.gcigb.network.interceptor;

import static com.gcigb.network.util.LogKt.logI;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LogInterceptor implements Interceptor {

    public static String TAG = "tag_default_http";
    private static final String TAG_TIME = "box_okhttp_time";
    private static final String LOCK_STRING = "lock_String";

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        synchronized (LOCK_STRING) {
            Request request = chain.request();
            logRequest(request);
            Response response = chain.proceed(request);
            logI(TAG_TIME, "Time-consuming response: \n" + (response.receivedResponseAtMillis() - response.sentRequestAtMillis()));
            response = logResponse(response);
            return response;
        }
    }

    private void logRequest(Request request) throws IOException {
        Headers headers = request.headers();
        RequestBody body = request.body();
        logI(TAG, "Request url:\n" + request.url().toString());
        logI(TAG, "Request method:\n" + request.method());
        logI(TAG, "Request headers:\n" + (headers == null ? "no headers" : headers.toString()));
        if (body == null) {
            logI(TAG, "Request body(no body)");
        } else if (isString(body.contentType())) {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            logI(TAG, "Request body(string body)\n" + buffer.readUtf8());
        } else if (body instanceof FormBody) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < ((FormBody) body).size(); i++) {
                builder.append(((FormBody) body).name(i)).append(":").append(((FormBody) body).value(i)).append("\n");
            }
            if (builder.length() > 0) {
                logI(TAG, "Request body(form body)\n" + builder.substring(0, builder.lastIndexOf("\n")));
                if (logFileWriter != null) {
                    logFileWriter.writeLog(builder.substring(0, builder.lastIndexOf("\n")));
                }
            }
        } else {
            logI(TAG, "Request body(other body)");
        }
    }

    private Response logResponse(Response response) throws IOException {
        ResponseBody body = response.body();
        logI(TAG, "Response protocol\n" + response.protocol());
        logI(TAG, "Response url\n" + response.request().url().toString());
        logI(TAG, "Response code\n" + response.code());
        logI(TAG, "Response message\n" + response.message() == null ? "no message" : response.message());
        if (response.isSuccessful()) {
            if (body == null) {
                logI(TAG, "Response body(no body)");
            } else if (isString(body.contentType())) {
                String bodyString = body.string();
                i(TAG, "Response body(string body)\n" + bodyString);
                body = ResponseBody.create(body.contentType(), bodyString);
            } else {
                logI(TAG, "Response body(no String body)");
            }
        }
        return response.newBuilder().body(body).build();
    }

    private void i(String tag, String msg) {
        //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001 - tag.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            logI(tag, msg.substring(0, max_str_length));
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        logI(tag, msg);
    }

    private boolean isString(MediaType mediaType) {
        if (mediaType == null) return false;
        String subtype = mediaType.subtype();
        return "json".equals(subtype)
                || "xml".equals(subtype)
                || "html".equals(subtype)
                || "text".equals(subtype)
                || "webviewhtml".equals(subtype);
    }

    public interface LogFileWriter {
        void writeLog(String log);
    }

    private LogFileWriter logFileWriter;

    public LogInterceptor setLogFileWriter(LogFileWriter logFileWriter) {
        this.logFileWriter = logFileWriter;
        return this;
    }
}
