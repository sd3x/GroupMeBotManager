package hash.cache.botutils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ListBotsActivity extends AppCompatActivity {

    User u;
    ArrayList<Bot> bots;
    ArrayList<Fragment> botsFragList;

    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bots);

        u = (User) getIntent().getExtras().get("user");

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        bots = new ArrayList<>();
        botsFragList = new ArrayList<>();

        getBots();
    }

    @Override
    protected void onRestart() {
        //refreshes activity on restart
        loading.setVisibility(View.VISIBLE);
        removeBots();
        getBots();
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //when instance saves, remove bots and their fragments
        loading.setVisibility(View.VISIBLE);
        removeBots();
        super.onSaveInstanceState(outState);
    }

    public void getBots() {

        //make bot index request

        ApiRequest apiRequest = new ApiRequest("INDEX", u);
        new RequestAsyncTask(this).execute(apiRequest);
    }

    public void parseBots(String index) {
        try {
            //parse JSON from response to bot index request
            JSONArray response = new JSONObject(index).getJSONArray("response");
            for (int i = 0; i < response.length(); i++) {
                Bot b = new Bot();
                JSONObject fields = response.getJSONObject(i);
                b.setName(fields.getString("name"));
                b.setId(fields.getString("bot_id"));
                b.setGroupName(fields.getString("group_name"));
                b.setGroupId(fields.getString("group_id"));
                b.setAvatarUrl(fields.getString("avatar_url"));
                b.setCallbackUrl(fields.getString("callback_url"));
                b.setDm(fields.getBoolean("dm_notification"));

                addBot(b);
            }

            addCreateBotFrag();

            loading.setVisibility(View.INVISIBLE);
        } catch (JSONException e) {
           showErrToast();
           e.printStackTrace();
        }
    }

    public void addBot(Bot bot) {

        //send bot argument to fragment
        BotFragment botFrag = new BotFragment();
        Bundle args = new Bundle();
        args.putSerializable("bot", bot);
        args.putSerializable("user", u);
        botFrag.setArguments(args);

        //adds bot fragment to botFragHolder
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
        fragmentTransaction.add(R.id.botFragHolder, botFrag);
        fragmentTransaction.commitAllowingStateLoss();

        //add fragment and bot to list to keep track of
        botsFragList.add(botFrag);
        bots.add(bot);
    }

    public void addCreateBotFrag() {

        //give user object to the create fragment to pass to create activity
        CreateBotFragment createFrag = new CreateBotFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", u);
        createFrag.setArguments(args);

        //adds fragment to botFragHolder
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
        fragmentTransaction.add(R.id.botFragHolder, createFrag);
        fragmentTransaction.commitAllowingStateLoss();

        //add fragment and bot to list to keep track of
        botsFragList.add(createFrag);
    }

    public void removeBots() {

        //remove each fragment
        for(int i = 0; i < botsFragList.size(); i++)
            if(botsFragList.get(i) != null) {
                FragmentManager fragMan = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
                fragmentTransaction.remove(botsFragList.get(i));
                fragmentTransaction.commitAllowingStateLoss();
            }

        //clear lists
        botsFragList.clear();
        bots.clear();
    }

    public void showErrToast() {
        Toast.makeText(getApplicationContext(), "Error while making request", Toast.LENGTH_LONG).show();
    }
}
