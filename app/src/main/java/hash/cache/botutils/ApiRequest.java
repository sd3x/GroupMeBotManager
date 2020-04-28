package hash.cache.botutils;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class ApiRequest {

    ApiRequest(String type, User user) {
        setType(type);
        setUser(user);
    }

    ApiRequest(String type, Bot bot, User user) {
        setType(type);
        setBot(bot);
        setUser(user);
    }

    ApiRequest(String type, String[] messages, Bot bot, User user) {
        setType(type);
        setMessages(messages);
        setBot(bot);
        setUser(user);
    }

    ApiRequest(String type, JSONObject body, Bot bot, User user) {
        setType(type);
        setBody(body);
        setBot(bot);
        setUser(user);
    }

    ApiRequest(String type, JSONObject body, User user) {
        setType(type);
        setBody(body);
        setUser(user);
    }

    private final String BASE_URL = "https://api.groupme.com/v3";

    private JSONObject body;
    private User user;
    private Bot bot;
    private String[] messages;

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    //types are: create, edit, message, index, delete, group
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
            case("GROUP"):
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

    public URL getUrl() throws MalformedURLException {
        String url = BASE_URL;
        switch (type) {
            case("INDEX"):
                url += "/bots?";
                break;
            case("CREATE"):
                return new URL("https://dev.groupme.com/bots");
            case("MESSAGE"):
                url += "/bots/post?";
                break;
            case("DESTROY"):
                return new URL("https://dev.groupme.com/bots/" + bot.getId() + "/delete");
            case("GROUP"):
                url += "/groups?&omit=memberships";
                break;
            case("EDIT"):
                    return new URL("https://dev.groupme.com/bots/" + bot.getId());
            default:
                throw new IllegalArgumentException("Invalid Type");
        }
            return new URL(url + "&token=" + user.getAccess_token());
    }
}
