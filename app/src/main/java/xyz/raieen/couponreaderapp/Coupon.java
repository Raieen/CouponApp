package xyz.raieen.couponreaderapp;

/**
 * Represents a coupon
 */
public class Coupon {

    String id; // Id in db
    private boolean redeemable;
    private String action, recipient;
    private int quantity;
    private long redeemed;

    public Coupon(String id, boolean redeemable, String action, String recipient, int quantity, long redeemed) {
        this.id = id;
        this.redeemable = redeemable;
        this.action = action;
        this.recipient = recipient;
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

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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
