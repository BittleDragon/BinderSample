// ICellPhoneManager.aidl
package com.rxt.bindersample.aidl;
import com.rxt.bindersample.aidl.CellPhone;
import com.rxt.bindersample.aidl.IOnNewDataReceivedListner;
// Declare any non-default types here with import statements

interface ICellPhoneManager {
    List<CellPhone> getCellPhoneList();
    void addCellPhone(in CellPhone phone);
    void registerOnNewDataReceivedListner(IOnNewDataReceivedListner listner);
    void unRegisterOnNewDataReceivedListner(IOnNewDataReceivedListner listner);
}
