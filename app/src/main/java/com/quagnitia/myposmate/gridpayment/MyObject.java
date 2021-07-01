package com.quagnitia.myposmate.gridpayment;

import java.io.Serializable;

public class MyObject implements Serializable {
    public MyObject(String alipay_disabled, boolean alipaySelected) {
        this.name=alipay_disabled;
        this.display=alipaySelected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;
    public boolean isConv() {
        return isConv;
    }

    public void setConv(boolean conv) {
        isConv = conv;
    }

    boolean isConv;
    public String getCnv_amt() {
        return cnv_amt;
    }

    public void setCnv_amt(String cnv_amt) {
        this.cnv_amt = cnv_amt;
    }

    String cnv_amt="";
    public int getSelected_option() {
        return selected_option;
    }

    public void setSelected_option(int selected_option) {
        this.selected_option = selected_option;
    }

    int selected_option;
    public MyObject() {

    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    int logo;

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    boolean display;
    public MyObject(String name, int color) {
        this.name = name;
        this.color = color;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name="";

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    int color;
}
