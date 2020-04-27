package hash.cache.botutils;

import androidx.annotation.NonNull;

public class Group {
    private String name;
    private String id;

    public Group(){}
    public Group(String name, String id) { setName(name); setId(id); }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return getName();
    }
}
