package hash.cache.botutils;

import android.os.AsyncTask;
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

            if (request.getType().equals("INDEX")) //"INDEX" is the only GET request of the types
                return makeGetRequest(request);
            else if (request.getType().equals("EDIT")) { //EDIT requests are special
                return makeEditRequest(request);
            }

            else
                return makePostRequest(request);

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
            String line;
            StringBuffer jsonString = new StringBuffer();
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
                    "=" + URLEncoder.encode("", "UTF-8");

            payload += "&" + URLEncoder.encode("bot[bot_id]", "UTF-8") +
                    "=" + URLEncoder.encode(b.getId(), "UTF-8");

            System.out.println(payload);

            URL url = request.getUrl();
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            String line;
            StringBuffer jsonString = new StringBuffer();
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            uc.setRequestProperty("Cookie", "Cookie: " + docsCookie + " " + ARRACookie + " " + TokenCookie + " " + authCookie);
            uc.setRequestMethod("POST");
            uc.setDoInput(true);
            uc.setInstanceFollowRedirects(false);
            uc.connect();
            OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
                System.out.println(uc.getResponseCode());
                br.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                writer.close();
                uc.disconnect();
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

    }