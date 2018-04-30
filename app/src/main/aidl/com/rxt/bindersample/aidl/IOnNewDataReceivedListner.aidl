// IOnNewDataReceivedListner.aidl
package com.rxt.bindersample.aidl;
import com.rxt.bindersample.aidl.CellPhone;

// Declare any non-default types here with import statements

interface IOnNewDataReceivedListner {
    void onNewDataReceived(in CellPhone phone);
}
