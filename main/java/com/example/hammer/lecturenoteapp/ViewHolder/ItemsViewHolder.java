package com.example.hammer.lecturenoteapp.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hammer.lecturenoteapp.Common.Common;
import com.example.hammer.lecturenoteapp.Interface.ItemClickListener;
import com.example.hammer.lecturenoteapp.R;

import org.w3c.dom.Text;


/**
 * Created by A.Richard on 19/09/2017.
 */



public class ItemsViewHolder extends RecyclerView.ViewHolder
        implements
        View.OnClickListener,
        View.OnCreateContextMenuListener


{


    public TextView LecName,Year,pdfName;


    private ItemClickListener itemClickListener;



    public ItemsViewHolder(View itemView) {
        super(itemView);

        LecName =  (TextView)itemView.findViewById(R.id.edtLectName);
        Year = (TextView) itemView.findViewById(R.id.edtYear);
        pdfName = itemView.findViewById(R.id.pdf_name);


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
