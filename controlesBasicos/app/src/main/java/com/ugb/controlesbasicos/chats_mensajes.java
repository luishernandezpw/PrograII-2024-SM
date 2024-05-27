package com.ugb.controlesbasicos;

public class chats_mensajes {
    public String para, de, para_de, msg;

    public chats_mensajes(){}
    public chats_mensajes(String para, String de, String para_de, String msg) {
        this.para = para;
        this.de = de;
        this.para_de = para_de;
        this.msg = msg;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getPara_de() {
        return para_de;
    }

    public void setPara_de(String para_de) {
        this.para_de = para_de;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
