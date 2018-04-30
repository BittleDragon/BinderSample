package com.rxt.clientapp;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rxt.bindersample.aidl.CellPhone;

import java.util.List;
import java.util.Locale;

/**
 * Desc:
 * Company: xuehai
 * Copyright: Copyright (c) 2018
 *
 * @author raoxuting
 * @since 2018/04/26 15/06
 */

public class CellPhoneAdapter extends RecyclerView.Adapter<CellPhoneAdapter.CellPhoneHolder> {

    private List<CellPhone> dataList;

    public void setDataList(List<CellPhone> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<CellPhone> getDataList() {
        return dataList;
    }

    @Override
    public CellPhoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                parent.getContext().getResources().getDisplayMetrics());
        textView.setPadding(padding, padding, padding, padding);
        return new CellPhoneHolder(textView);
    }

    @Override
    public void onBindViewHolder(CellPhoneHolder holder, int position) {
        ((TextView)holder.itemView).setText(String.format(Locale.getDefault(), "品牌: %s, 价格: %.0f",
                dataList.get(position).getGrand(), dataList.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return dataList == null? 0 : dataList.size();
    }

    static class CellPhoneHolder extends RecyclerView.ViewHolder {

        public CellPhoneHolder(View itemView) {
            super(itemView);
        }
    }
}
