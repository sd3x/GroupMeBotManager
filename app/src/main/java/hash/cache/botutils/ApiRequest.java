package hash.cache.botutils;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class ApiRequest {

    ApiRequest(String type, User user) {
        setType(type);
        setUser(user);
    }

    ApiRequest(String type, JSONObject body, User user) {
        setType(type);
        setBody(body);
        setUser(user);
    }

    ApiRequest(String type, Bot bot, User user) {
        setType(type);
        setUser(user);
        setBot(bot);
    }

    private final String BASE_URL = "https://api.groupme.com/v3";

    private JSONObject body;
    private User user;
    private Bot bot;

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    //types are: create, message, index, delete
    private String type;

    public void setBody(JSONObject json) {
        this.body = json;
    }

    public JSONObject getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        switch (type) {
            case("EDIT"):
            case("CREATE"):
            case("MESSAGE"):
            case("INDEX"):
            case("DESTROY"):
                this.type = type;
                break;
            default:
                throw new IllegalArgumentException("Invalid Type");
        }
    }

    public URL getUrl() {
        String url = BASE_URL;
        switch (type) {
            case("INDEX"):
            case("CREATE"):
                url += "/bots";
                break;
            case("MESSAGE"):
                url += "/bots/post";
                break;
            case("DESTROY"):
                url += "/bots/destroy";
                break;
            case("EDIT"):
                try {
                    return new URL("https://dev.groupme.com/bots/" + bot.getId());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
            default:
                throw new IllegalArgumentException("Invalid Type");
        }
        try {
            return new URL(url + "?&token=" + user.getAccess_token());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
