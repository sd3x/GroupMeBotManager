package hash.cache.botutils;

import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String access_token;
    private String editor_token;

    public String getEditor_token() {
        return editor_token;
    }

    public void setEditor_token(String editor_token) {
        this.editor_token = editor_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
