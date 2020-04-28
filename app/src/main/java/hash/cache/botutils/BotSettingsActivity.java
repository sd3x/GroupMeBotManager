package hash.cache.botutils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class BotSettingsActivity extends AppCompatActivity {


    EditText name, callback, avatarUrl, toSend;
    ImageView avatar;
    TextView botId, groupName, groupId;

    Button send;
    ImageButton delete;
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

        u = (User) Objects.requireNonNull(getIntent().getExtras()).getSerializable("user");
        bot = (Bot) Objects.requireNonNull(getIntent().getExtras().getSerializable("bot"));
        setTitle("Bot Editor");


        name = findViewById(R.id.name);

        callback = findViewById(R.id.callbackText);
        avatarUrl = findViewById(R.id.avatarText);

        avatar = findViewById(R.id.avatarImage);
        send = findViewById(R.id.send);
        delete = findViewById(R.id.delete);
        loading = findViewById(R.id.loading);

        botId = findViewById(R.id.botId);
        groupName = findViewById(R.id.groupName);
        groupId = findViewById(R.id.groupId);
        toSend = findViewById(R.id.toSend);

        setAvatar();
        setTexts();


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteConfirmationFragment deleteFrag = new DeleteConfirmationFragment();
                Bundle args = new Bundle();
                args.putSerializable("bot", bot);
                deleteFrag.setArguments(args);
                deleteFrag.show(getSupportFragmentManager(), "Confirmation");
            }
        });

    }

    public void sendMessage(View v) {
        String message = toSend.getText().toString();
        String[] parsedMessages = charLimitParse(message);

        ApiRequest request = new ApiRequest("MESSAGE", parsedMessages, bot, u);
        new RequestAsyncTask(this).execute(request);
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

        JSONObject update = new JSONObject();
        try {
            update.put("bot[name]", name.getText().toString());
            update.put("bot[callback_url]", callback.getText().toString());
            update.put("bot[avatar_url]", avatarUrl.getText().toString());
            update.put("bot[bot_id]", bot.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest apiRequest = new ApiRequest("EDIT", update, bot, u);
        new RequestAsyncTask(this).execute(apiRequest);

    }

    public void deleteBot() {
        ApiRequest request = new ApiRequest("DESTROY", bot, u);
        new RequestAsyncTask(this).execute(request);
    }

    public void goBack() {
        Intent intent = new Intent(getApplicationContext(), ListBotsActivity.class);
        finish();
        startActivity(intent);
    }
}
