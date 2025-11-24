package com.chuanyun.downloader.tabbar.me;

import android.graphics.Paint;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chuanyun.downloader.R;
import com.chuanyun.downloader.base.adapter.TTBaseMultiItemAdapter;
import com.chuanyun.downloader.tabbar.me.model.VipGoodsModel;

import java.util.List;

public class VipGoodsAdapter extends TTBaseMultiItemAdapter<VipGoodsModel> {
    public VipGoodsAdapter(List<VipGoodsModel> data) {
        super(data);
        addItemType(VipGoodsModel.MODEL_TYPE_HEADER,R.layout.item_vip_header);
        addItemType(VipGoodsModel.MODEL_TYPE_GOODS,R.layout.item_vip_goods);
        addItemType(VipGoodsModel.MODEL_TYPE_ITEM,R.layout.item_image_title);
        addItemType(VipGoodsModel.MODEL_TYPE_TITLE,R.layout.item_vip_title);
        addItemType(VipGoodsModel.MODEL_TYPE_INV,R.layout.item_inv_view);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VipGoodsModel vipGoodsModel) {
        super.convert(baseViewHolder, vipGoodsModel);

        if (vipGoodsModel.getItemType() == VipGoodsModel.MODEL_TYPE_GOODS) {
            baseViewHolder.setText(R.id.vip_title_tv,vipGoodsModel.getName())
                    .setText(R.id.vip_money_tv,"￥" + vipGoodsModel.getMoney())
                    .setText(R.id.vip_sup_title_tv,vipGoodsModel.getSubTitle());

            TextView yjTv = baseViewHolder.getView(R.id.yj_tv);
            yjTv.setText("原价：￥" + vipGoodsModel.getYuanJia() + ".00");
            yjTv.setPaintFlags(yjTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int rootCardBg = vipGoodsModel.isSelect() ? getContext().getColor(R.color.yellow_vip_text_mon_s) : getContext().getColor(R.color.black);
            int cardBg = vipGoodsModel.isSelect() ? getContext().getColor(R.color.black) : getContext().getColor(R.color.white);

            CardView rootCardView = baseViewHolder.getView(R.id.root_card_v);
            rootCardView.setCardBackgroundColor(rootCardBg);

            CardView cardView = baseViewHolder.getView(R.id.show_card_view);
            cardView.setCardBackgroundColor(cardBg);
        }else if (vipGoodsModel.getItemType() == VipGoodsModel.MODEL_TYPE_HEADER) {
            baseViewHolder.setText(R.id.user_nike_name_tv,vipGoodsModel.getName())
                    .setText(R.id.vip_time_tv,vipGoodsModel.getVipTime());

            TextView timeTv = baseViewHolder.findView(R.id.vip_time_tv);
            timeTv.setTextColor(vipGoodsModel.getTextColor());

        }else if (vipGoodsModel.getItemType() == VipGoodsModel.MODEL_TYPE_TITLE) {
            baseViewHolder.setText(R.id.title_tv,vipGoodsModel.getName());
        }else if (vipGoodsModel.getItemType() == VipGoodsModel.MODEL_TYPE_INV) {
            baseViewHolder.setText(R.id.detail_tv,vipGoodsModel.getSubTitle());

        } else {
            baseViewHolder.setImageResource(R.id.icon_im, vipGoodsModel.getImageRes())
                    .setText(R.id.title_tv,vipGoodsModel.getName());
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                VipGoodsModel model = getData().get(position);
                if (model.getItemType() == VipGoodsModel.MODEL_TYPE_ITEM) {
                    return 1;
                }else {
                    return 3;
                }
            }
        });
    }
}
