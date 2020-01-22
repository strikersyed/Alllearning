package com.ast.taskApp;

import java.io.Serializable;

public class linksmodel implements Serializable {

    private String full,raw,regular,small,thumb;


    public linksmodel(String full, String raw, String regular, String small, String thumb) {
        this.full = full;
        this.raw = raw;
        this.regular = regular;
        this.small = small;
        this.thumb = thumb;
    }

    public linksmodel () {}

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }


}
