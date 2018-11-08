package com.example.hammer.lecturenoteapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hammer.lecturenoteapp.Common.Common;
import com.example.hammer.lecturenoteapp.Interface.ItemClickListener;
import com.example.hammer.lecturenoteapp.R;


/**
 * Created by A.Richard on 19/09/2017.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder
        implements
        View.OnClickListener,
        View.OnCreateContextMenuListener


{


    public TextView txtMenuName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;



    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName =  (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);


        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);


    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }



    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
         contextMenu.setHeaderTitle("Select the action");
         contextMenu.setHeaderIcon(R.drawable.ic_picture_as_pdf_black_24dp);

         contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
         contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);

    }
}
