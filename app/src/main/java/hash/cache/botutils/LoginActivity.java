package hash.cache.botutils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

 //   Button switchTheme;
  //  Button next;
  //  boolean nightMode;

    WebView web;

    String callbackPrefix = "botutils://oauth";
    String applicationToken = "YN2AU47O9Lh4oydkRJItExEy6ESMfbKWl9YWaMDCfR9Gfr3r";

    User u;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("GroupMe Oauth");

        web = findViewById(R.id.web);
        web.setVisibility(View.INVISIBLE);
        u = new User();

        //set browser settings needed for login and token to be cached by browser for auto-login later
        web.getSettings().setJavaScriptEnabled(true);
        WebSettings settings = web.getSettings();
        settings.setDomStorageEnabled(true);

        //Intercepts Deep Link to Log in and grabs access token
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().startsWith(callbackPrefix)) {
                    web.setVisibility(View.INVISIBLE);
                    setTitle("");
                    String callbackWithToken = request.getUrl().toString();

                    String accessToken = callbackWithToken.substring((callbackPrefix + "?access_token=").length());
                    u.setAccess_token(accessToken);

                    SharedPreferences sp = getSharedPreferences("editor_cookie", MODE_PRIVATE);
                    String authCookie = sp.getString("cookie", "");

                    //if editor auth cookie is found, skip getting it
                    if(authCookie.isEmpty())
                        login(u, false);
                    else {
                        u.setEditor_token(authCookie);
                        login(u, true);
                    }

                } else {
                    web.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        web.loadUrl("https://oauth.groupme.com/oauth/authorize?client_id=" + applicationToken);

    }


    public void login(User u, boolean hasCookie) {

        Intent intent;

        //if cookie is not found, get it by the DevLoginActivity
        if(hasCookie)
            intent = new Intent(getApplicationContext(), ListBotsActivity.class);
        else
            intent = new Intent(getApplicationContext(), DevLoginActivity.class);

        intent.putExtra("user", u);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);

        //start next activity with no transition
        overridePendingTransition(0,0);
    }
    
}
