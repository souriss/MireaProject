package ru.mirea.lednevadd.mireaproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.lednevadd.mireaproject.databinding.FragmentSiteBinding;

public class SiteFragment extends Fragment {
    FragmentSiteBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSiteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Привязка обработчика нажатия к кнопке
        binding.button.setOnClickListener(this::onClick);

        return view;
    }

    public void onClick(View view) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = null;
        if (connectivityManager != null) {
            networkinfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkinfo != null && networkinfo.isConnected()) {
            new IpInfo().execute(); // запуск нового потока
        } else {
            Toast.makeText(getActivity(), "Нет интернета", Toast.LENGTH_SHORT).show();
        }
    }

    private class IpInfo extends DownloadPageTask {
        public IpInfo() {
            super("https://ipinfo.io/json", "GET");
        }
    }

    private class DownloadPageTask extends AsyncTask<String, Void, String> {
        private String path;
        private String request_method;

        public DownloadPageTask(String path, String request_method) {
            this.path = path;
            this.request_method = request_method;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "Идет загрузка...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadIpInfo();
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(SiteFragment.class.getSimpleName(), result);
            try {
                JSONObject responseJson = new JSONObject(result);
                binding.TextViewcity.setText("Город: " + responseJson.getString("city"));
                binding.TextViewregion.setText("Регион: " + responseJson.getString("region"));
                binding.TextViewcountry.setText("Страна: " + responseJson.getString("country"));

                String[] coordinates = responseJson.getString("loc").split(",");

                new RequestWeatherInfo(Float.parseFloat(coordinates[0]), Float.parseFloat(coordinates[1])).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

        private class RequestWeatherInfo extends DownloadPageTask {
            public RequestWeatherInfo(float latitude, float longitude) {
                super(String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true",
                        latitude, longitude), "GET");
            }

            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject responseJson = new JSONObject(result);
                    JSONObject weatherJson = new JSONObject(responseJson.getString("current_weather"));
                    binding.TextViewweather.setText("Погода: \n" +
                            "температура: " + weatherJson.getString("temperature") + "\n" +
                            "скорость ветра: " + weatherJson.getString("windspeed") + "\n" +
                            "направление ветра: " + weatherJson.getString("winddirection"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private String downloadIpInfo() throws IOException {
            InputStream inputStream = null;
            String data = "";
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(100000);
                connection.setConnectTimeout(100000);
                connection.setRequestMethod(request_method);
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                    inputStream = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read;
                    while ((read = inputStream.read()) != -1) {
                        bos.write(read);
                    }
                    data = bos.toString();
                } else {
                    data = connection.getResponseMessage() + ". Error Code: " + responseCode;
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return data;
        }
    }
}
