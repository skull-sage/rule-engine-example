package com.example.ruleengine.model;

public class FreeGift {
    private String giftId;
    private String giftName;
    private String qualifyingReason;

    public FreeGift() {
    }

    public FreeGift(String giftId, String giftName, String qualifyingReason) {
        this.giftId = giftId;
        this.giftName = giftName;
        this.qualifyingReason = qualifyingReason;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getQualifyingReason() {
        return qualifyingReason;
    }

    public void setQualifyingReason(String qualifyingReason) {
        this.qualifyingReason = qualifyingReason;
    }
}
