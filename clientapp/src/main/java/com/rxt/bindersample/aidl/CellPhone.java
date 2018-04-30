package com.rxt.bindersample.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Desc:
 *
 * @author raoxuting
 *         since 2018/4/27
 */

public class CellPhone implements Parcelable {

    public String grand;
    public double price;

    public String getGrand() {
        return grand;
    }

    public void setGrand(String grand) {
        this.grand = grand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.grand);
        dest.writeDouble(this.price);
    }

    public CellPhone(String grand, double price) {
        this.grand = grand;
        this.price = price;
    }

    protected CellPhone(Parcel in) {
        this.grand = in.readString();
        this.price = in.readDouble();
    }

    public static final Creator<CellPhone> CREATOR = new Creator<CellPhone>() {
        @Override
        public CellPhone createFromParcel(Parcel source) {
            return new CellPhone(source);
        }

        @Override
        public CellPhone[] newArray(int size) {
            return new CellPhone[size];
        }
    };
}
