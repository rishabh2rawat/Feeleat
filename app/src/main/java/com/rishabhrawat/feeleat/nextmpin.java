package com.rishabhrawat.feeleat;

public class nextmpin {

    String mpin;

    public nextmpin(String mpin) {
        this.mpin = mpin;
    }

    public nextmpin() {
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    @Override
    public String toString() {
        return "nextmpin{" +
                "mpin='" + mpin + '\'' +
                '}';
    }
}
