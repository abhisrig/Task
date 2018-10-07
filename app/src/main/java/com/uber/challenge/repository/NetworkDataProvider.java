package com.uber.challenge.repository;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;

import com.uber.challenge.TaskQueue;
import com.uber.challenge.parser.GsonParser;
import com.uber.challenge.search.ImageData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public enum NetworkDataProvider implements DataProvider {
    INSTANCE;
    private Handler mResponseDelivery = new Handler(Looper.getMainLooper());

    @WorkerThread
    @Override
    public <T> T getDataSync(Query query) {
        String urlToFetch = query.build();
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(urlToFetch);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(30_000);
            connection.setConnectTimeout(30_000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(reader);
            String read;
            StringBuilder sb = new StringBuilder();
            while ((read = in.readLine()) != null) {
                sb.append(read);
            }
            String output = sb.toString();
            GsonParser gsonParser = new GsonParser();
            ImageData imageData = gsonParser.fromJson(output, ImageData.class);

            reader.close();
            in.close();
            return (T) imageData;
        } catch (ProtocolException e) {
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    public <T> void getDataAsync(final Query query, final ResponseListener<T> listener) {
        TaskQueue.queue(new Runnable() {
            @Override
            public void run() {
                final T data = getDataSync(query);
                mResponseDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null && data != null) {
                            listener.onResponse(data);
                        } else if (listener != null) {
                            listener.onError(new Exception("Error occurred"));
                        }
                    }
                });
            }
        });
    }
}
