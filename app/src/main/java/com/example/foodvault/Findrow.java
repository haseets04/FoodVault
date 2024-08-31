package com.example.foodvault;

public class Findrow {
   private int product_id;

    public Findrow(int product_id, String recordTag) {
        this.product_id = product_id;
        this.recordTag = recordTag;
    }

    private String recordTag;

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getRecordTag() {
        return recordTag;
    }

    public void setRecordTag(String recordTag) {
        this.recordTag = recordTag;
    }
}
