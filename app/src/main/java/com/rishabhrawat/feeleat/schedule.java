package com.rishabhrawat.feeleat;

public class schedule {

        String checkin;
        String checkout;

    public schedule(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public schedule() {
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    @Override
    public String toString() {
        return "schedule{" +
                "checkin='" + checkin + '\'' +
                ", checkout='" + checkout + '\'' +
                '}';
    }
}
