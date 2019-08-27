package xyz.raieen.couponreaderapp;

public class Coupon {

    String id;
    private boolean redeemable;
    private String action, receipient;
    private int quantity;
    private long redeemed;

    public Coupon(String id, boolean redeemable, String action, String receipient, int quantity, long redeemed) {
        this.id = id;
        this.redeemable = redeemable;
        this.action = action;
        this.receipient = receipient;
        this.quantity = quantity;
        this.redeemed = redeemed;
    }

    public String getId() {
        return id;
    }

    public boolean isRedeemable() {
        return redeemable;
    }

    public void setRedeemable(boolean redeemable) {
        this.redeemable = redeemable;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReceipient() {
        return receipient;
    }

    public void setReceipient(String receipient) {
        this.receipient = receipient;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getRedeemed() {
        return redeemed;
    }

    public void setRedeemed(long redeemed) {
        this.redeemed = redeemed;
    }
}
