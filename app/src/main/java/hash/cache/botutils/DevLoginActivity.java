package hash.cache.botutils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

public class DevLoginActivity extends AppCompatActivity {


    //This additional login is needed to grab a cookie used for authentication when making bot edit requests


    WebView web;
    User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_login);

        web = findViewById(R.id.web);
        u = (User) Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");

        setTitle("GroupMe Dev Authentication");

        web.getSettings().setJavaScriptEnabled(true);
        WebSettings settings = web.getSettings();
        settings.setDomStorageEnabled(true);

        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //pin verification is not needed to get the auth cookie
                if (request.getUrl().toString().equals("https://dev.groupme.com/session/pin")) {
                    web.setVisibility(View.INVISIBLE);
                    setTitle("");
                    String cookies = CookieManager.getInstance().getCookie(request.getUrl().toString());

                    //format is ... us=COOKIE; other_cookie=COOKIE; ...
                    //the auth cookie is the us one
                    String authCookie = cookies.split("us=")[1].split(";")[0];

                    //store cookie as shared pref, so user isn't taken here and sent a text whenever they launch the app
                    //afaik, the cookie never expires
                    SharedPreferences sp = getSharedPreferences("editor_cookie", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sp.edit();

                    ed.putString("cookie", authCookie);
                    ed.commit();

                    u.setEditor_token(authCookie);
                    login(u);

                    System.out.println(authCookie);
                }
                return false;
            }
        });
        web.loadUrl("https://dev.groupme.com/session/new");
    }

    public void login(User u) {
        Intent intent = new Intent(getApplicationContext(), ListBotsActivity.class);
        intent.putExtra("user", u);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);

        //start next activity with no transition
        overridePendingTransition(0,0);
    }
}
