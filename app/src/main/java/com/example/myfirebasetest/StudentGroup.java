package com.example.myfirebasetest;

import java.util.ArrayList;
import java.util.List;

public class StudentGroup {
    private String title, stgrouptitle, type;
    private int total = 0, available = 0, id;
    private List<Integer> unit = new ArrayList<Integer>();


  /*  public StudentGroup(String title) {
    }*/

    public StudentGroup() {
        this.title = title;
        this.type = type;
        this.total = total;
        this.id = id;
        this.available=total;
        this.stgrouptitle = stgrouptitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStgrouptitle(){return getStgrouptitle();}

    public void setStgrouptitle(String stgrouptitle) {
        this.stgrouptitle = stgrouptitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTotal() {
        return total;
    }


}
