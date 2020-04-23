package hash.cache.botutils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class BotSettingsActivity extends AppCompatActivity {


    EditText name, callback, avatarUrl;
    ImageView avatar;
    TextView botId, groupName, groupId;

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

        botId = findViewById(R.id.botId);
        groupName = findViewById(R.id.groupName);
        groupId = findViewById(R.id.groupId);

        setAvatar();
        setTexts();

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
        Bot botUpdate = new Bot();
        botUpdate.setName(name.getText().toString());
        botUpdate.setCallbackUrl(callback.getText().toString());
        botUpdate.setAvatarUrl(avatarUrl.getText().toString());
        botUpdate.setId(bot.getId());

        ApiRequest apiRequest = new ApiRequest("EDIT", botUpdate, u);
        new RequestAsyncTask().execute(apiRequest);
    }
}
