package hash.cache.botutils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BotSettingsActivity extends AppCompatActivity {


    EditText name, callback, avatarUrl, toSend;
    ImageView avatar;
    TextView botId, groupName, groupId;

    Button send;
    ProgressBar loading;

    Bot bot;
    User u;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            saveBot();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_settings);

        u = (User) getIntent().getExtras().getSerializable("user");
        bot = (Bot) getIntent().getExtras().getSerializable("bot");
        setTitle("Bot Editor");


        name = findViewById(R.id.nameText);

        callback = findViewById(R.id.callbackText);
        avatarUrl = findViewById(R.id.avatarText);

        avatar = findViewById(R.id.avatarImage);
        send = findViewById(R.id.send);
        loading = findViewById(R.id.loading);

        botId = findViewById(R.id.botId);
        groupName = findViewById(R.id.groupName);
        groupId = findViewById(R.id.groupId);
        toSend = findViewById(R.id.toSend);

        setAvatar();
        setTexts();

    }

    public void sendMessage(View v) {
        String message = toSend.getText().toString();
        String[] parsedMessages = charLimitParse(message);

        ApiRequest request = new ApiRequest("MESSAGE", parsedMessages, bot, u);
        new RequestAsyncTask().execute(request);
        toSend.setText("");
        Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
    }

    public String[] charLimitParse(String s) {
        if(s.length() > 1000) {

            List<String> parts = new ArrayList<>();

            int length = s.length();
            for (int i = 0; i < length; i += 1000) {
                parts.add(s.substring(i, Math.min(length, i + 1000)));
            }
            return parts.toArray(new String[0]);
        } else {
            return new String[] {s};
        }
    }

    public void setAvatar() {
        if(bot.getAvatarUrl() != null && !bot.getAvatarUrl().isEmpty()) {
            try {
                //download image avatar in background, if no image found uses default image
                avatar.setImageBitmap(new DownloadImageAsyncTask().execute(bot.getAvatarUrl()).get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTexts() {
        name.setText(bot.getName());
        callback.setText(bot.getCallbackUrl());
        avatarUrl.setText(bot.getAvatarUrl());

        botId.setText(bot.getId());
        groupName.setText(bot.getGroupName());
        groupId.setText(bot.getGroupId());
    }

    public void saveBot() {

        loading.setVisibility(View.VISIBLE);

        Bot botUpdate = new Bot();
        botUpdate.setName(name.getText().toString());
        botUpdate.setCallbackUrl(callback.getText().toString());
        botUpdate.setAvatarUrl(avatarUrl.getText().toString());
        botUpdate.setId(bot.getId());

        ApiRequest apiRequest = new ApiRequest("EDIT", botUpdate, u);
        new RequestAsyncTask(this).execute(apiRequest);

    }

    public void goBack() {
        Intent intent = new Intent(getApplicationContext(), ListBotsActivity.class);
        finish();
        startActivity(intent);
    }
}
