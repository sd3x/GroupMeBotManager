package hash.cache.botutils;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class BotFragment extends Fragment {

    private TextView name, id, group, groupId;
    private ImageView avatar;
    private ImageButton settings;

    private Bot bot;
    private User u;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bot, container, false);

        name = view.findViewById(R.id.nameTag);
        id = view.findViewById(R.id.id);
        group = view.findViewById(R.id.group);
        groupId = view.findViewById(R.id.groupId);

        avatar = view.findViewById(R.id.avatarLabel);

        settings = view.findViewById(R.id.settings);


        if (getArguments() != null) {
            Bundle args = getArguments();

            bot = (Bot) args.getSerializable("bot");
            u = (User) args.getSerializable("user");

            name.setText(bot.getName());
            id.setText(bot.getId());
            group.setText(bot.getGroupName());
            groupId.setText(bot.getGroupId());

            if (bot.getAvatarUrl() != null &&
                    !bot.getAvatarUrl().isEmpty() &&
                    !bot.getAvatarUrl().equals("null")) {  //sometimes JSON null values are the actual string "null"
                try {
                    //download image avatar in background, if no image found uses default image
                    avatar.setImageBitmap(new DownloadImageAsyncTask().execute(bot.getAvatarUrl()).get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BotSettingsActivity.class);
                intent.putExtra("bot", bot);
                intent.putExtra("user", u);
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

}
