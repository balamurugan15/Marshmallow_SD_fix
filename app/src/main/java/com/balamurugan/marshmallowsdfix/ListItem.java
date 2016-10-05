package com.balamurugan.marshmallowsdfix;

import android.graphics.drawable.Drawable;

/**
 * Created by Balamurugan M on 6/17/2016.
 */
public class ListItem {
    private String pkgName;
    private String name;
    private Drawable icon;
    private boolean isSelected;


    public String getPkgName() {
       return this.pkgName;
    }

    public String getName() {
        return this.name;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public ListItem(String pname, String name, Drawable icon1, boolean isSelected){
        this.icon = icon1;
        this.name = name;
        this.pkgName = pname;
        this.isSelected = isSelected;
    }

}