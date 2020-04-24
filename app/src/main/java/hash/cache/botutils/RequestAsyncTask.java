package hash.cache.botutils;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RequestAsyncTask extends AsyncTask<ApiRequest, String, String> {

    @Override
    protected String doInBackground(ApiRequest... params) {

        try {
            ApiRequest request = params[0];

            switch (request.getType()) {
                case("INDEX"):
                    return makeGetRequest(request);
                case("EDIT"):
                    return makeEditRequest(request);
                case("MESSAGE"):
                    sendMessage(request);
                    break;
                default:
                    return makePostRequest(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String makeGetRequest(ApiRequest request) {
        URL url = request.getUrl();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                connection.disconnect();
                return response.toString();
            } else {
                connection.disconnect();
                return "Bad Status";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "IO Exception";
        }
    }

    public String makePostRequest(ApiRequest request) {
        URL url = request.getUrl();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(request.getBody().toString());

            writer.close();

            if(connection.getResponseCode() == 201) {
                connection.disconnect();
                return "SUCCESS";
            } else {
                connection.disconnect();
                return "FAIL";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "FAIL";
        }
    }

   public String makeEditRequest(ApiRequest request) {
        String payload;
        Bot b = request.getBot();

            //These cookies are needed, but can have arbitrary values
            String docsCookie = "_docs_session=COOKIE;";
            String ARRACookie = "ARRAffinity=COOKIE;";
            String TokenCookie = "token=cookie;";

            // This cookie is used for authentication
            String authCookie = "us=" + request.getUser().getEditor_token();

        try {
            payload = URLEncoder.encode("utf8", "UTF-8") +
                    "=" + URLEncoder.encode("✓", "UTF-8");

            payload += "&" + URLEncoder.encode("_method", "UTF-8") +
                    "=" + URLEncoder.encode("put", "UTF-8");

            payload += "&" + URLEncoder.encode("bot[name]", "UTF-8") +
                    "=" + URLEncoder.encode(b.getName(), "UTF-8");

            payload += "&" + URLEncoder.encode("bot[callback_url]", "UTF-8") +
                    "=" + URLEncoder.encode(b.getCallbackUrl(), "UTF-8");

            payload += "&" + URLEncoder.encode("bot[avatar_url]", "UTF-8") +
                    "=" + URLEncoder.encode(b.getAvatarUrl(),"UTF-8");

            payload += "&" + URLEncoder.encode("bot[bot_id]", "UTF-8") +
                    "=" + URLEncoder.encode(b.getId(), "UTF-8");

            System.out.println(payload);

            URL url = request.getUrl();
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            String line;
            StringBuilder jsonString = new StringBuilder();
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            uc.setRequestProperty("Cookie", "Cookie: " + docsCookie + " " + ARRACookie + " " + TokenCookie + " " + authCookie);
            uc.setRequestMethod("POST");
            uc.setDoInput(true);
            uc.setInstanceFollowRedirects(false);
            uc.connect();
            OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();

            //reading the output is necessary for the edit to complete, even though it's not used
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
                br.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                return "FAIL";
            } finally {
                writer.close();
                uc.disconnect();
            }
            uc.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
            return "SUCCESS";
        }

        public void sendMessage(ApiRequest request) {
            for(int i = 0; i < request.getMessages().length; i++) {
                JSONObject body = new JSONObject();
                try {
                    body.put("bot_id", request.getBot().getId());
                    body.put("text", request.getMessages()[i]);
                    System.out.println(body);
                    request.setBody(body);
                    makePostRequest(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }