package com.rishabhrawat.feeleat;

public class addUser {
    String name;
    String phoneno;
    String email;
    String address;
    String sphotourl;
    String pin;

    public addUser(String name, String phoneno, String email, String address, String sphotourl, String pin) {
        this.name = name;
        this.phoneno = phoneno;
        this.email = email;
        this.address = address;
        this.sphotourl = sphotourl;
        this.pin = pin;
    }

    public addUser() {
    }



    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSphotourl() {
        return sphotourl;
    }

    public void setSphotourl(String sphotourl) {
        this.sphotourl = sphotourl;
    }


    @Override
    public String toString() {
        return "addUser{" +
                "name='" + name + '\'' +
                ", phoneno='" + phoneno + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", sphotourl='" + sphotourl + '\'' +
                ", pin='" + pin + '\'' +
                '}';
    }
}
