package com.trickyandroid.playmusic.view.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trickyandroid.playmusic.R;
import com.trickyandroid.playmusic.models.SongInfoModel;
import com.trickyandroid.playmusic.view.fragement.CommonSearchFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class Common_Registration_Adapter extends BaseAdapter {

    private Context context;
    private ArrayList<SongInfoModel> arrayList = new ArrayList<>();
    private ArrayList<SongInfoModel> search_arrayList;
    private LayoutInflater mInflater;
    private int curTabPos;

    //    CommonSearchFragment.CommonRightMenuFragmentListener obj;
    public Common_Registration_Adapter(Context context, ArrayList<SongInfoModel> arrayList, int curTabPos) {
        super();
        this.context = context;
//       this.obj=obj;
        this.curTabPos = curTabPos;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;
        search_arrayList = new ArrayList<>();
        search_arrayList.addAll(arrayList);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public SongInfoModel getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.common_registration_adapter, null);
            holder.linearView = convertView.findViewById(R.id.linearView);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180);
            holder.linearView.setLayoutParams(params);
            holder.imageView = convertView.findViewById(R.id.ivSong);
            holder.value_textView = convertView.findViewById(R.id.tvMainName);
            holder.SubName = convertView.findViewById(R.id.tvSubName);
//            holder.cardView = convertView.findViewById(R.id.cardView);
//            holder.cardView.setBackgroundResource(R.drawable.card_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            Glide.with(context)
                    .load(Uri.parse(arrayList.get(position).getSongImgPath()))
                    .error(R.drawable.default_album_bg)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(holder.imageView);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.default_album_bg);
        }


//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                obj.onItemSelect(search_arrayList.get(position).getId(),"key",search_arrayList.get(position).getSongName());
//            }
//        });

        switch (curTabPos) {
            case 0:
                holder.SubName.setText(arrayList.get(position).getSongArtist());
                break;
            case 1:
                holder.SubName.setText(arrayList.get(position).getSongMoviename());
                break;
            case 2:
                holder.SubName.setText(arrayList.get(position).getSongArtist());
                break;
        }

        if (arrayList.size() != 0)//For set no result found
        {
            holder.value_textView.setText(arrayList.get(position).getSongName());
        } else
            holder.value_textView.setText("");
        return convertView;
    }

    /*private view holder class*/
    private class ViewHolder {
        private LinearLayout linearView;
        private TextView value_textView;
        private TextView SubName;
        private ImageView imageView;

    }

    // Filter Class
    public void filter(String charText) {
        try {
            charText = charText.toLowerCase(Locale.getDefault());
            arrayList.clear();
            if (charText.length() == 0) {
                arrayList.addAll(search_arrayList);
            } else {
                for (SongInfoModel wp : search_arrayList) {
                    if (wp.getSongName().toLowerCase(Locale.getDefault()).contains(charText) || wp.getSongMoviename().toLowerCase().contains(charText)
                            || wp.getSongArtist().toLowerCase().contains(charText))  //contains
                        arrayList.add(wp);
                }
                final String finalCharText = charText;
                Collections.sort(arrayList, new Comparator<SongInfoModel>() {

                    public int compare(SongInfoModel s1, SongInfoModel s2) {
                        String s1name = s1.getSongName().toLowerCase();
                        String s2name = s2.getSongName().toLowerCase();
                        String s11name = s1.getSongMoviename().toLowerCase();
                        String s22name = s2.getSongMoviename().toLowerCase();
                        String s111name = s1.getSongArtist().toLowerCase();
                        String s222name = s2.getSongArtist().toLowerCase();

                        int returnValue = 0;


                        if (!s1name.startsWith(finalCharText) && !s2name.startsWith(finalCharText))
                            returnValue = s1name.compareTo(s2name);
                        else if (s1name.startsWith(finalCharText) && s2name.startsWith(finalCharText))
                            returnValue = s1name.compareTo(s2name);
                        else if (!s11name.startsWith(finalCharText) && !s22name.startsWith(finalCharText))
                            returnValue = s11name.compareTo(s22name);
                        else if (s11name.startsWith(finalCharText) && s22name.startsWith(finalCharText))
                            returnValue = s11name.compareTo(s22name);
                        else if (!s111name.startsWith(finalCharText) && !s222name.startsWith(finalCharText))
                            returnValue = s111name.compareTo(s222name);
                        else if (s111name.startsWith(finalCharText) && s222name.startsWith(finalCharText))
                            returnValue = s11name.compareTo(s222name);
                        else {
                            if (s1name.startsWith(finalCharText) && !s2name.startsWith(finalCharText))
                                returnValue = -1;
                            else if (!s1name.startsWith(finalCharText) && s2name.startsWith(finalCharText))
                                returnValue = 1;
                            else if (s11name.startsWith(finalCharText) && !s22name.startsWith(finalCharText))
                                returnValue = -1;
                            else if (!s11name.startsWith(finalCharText) && s22name.startsWith(finalCharText))
                                returnValue = 1;
                            else if (s111name.startsWith(finalCharText) && !s222name.startsWith(finalCharText))
                                returnValue = -1;
                            else if (!s111name.startsWith(finalCharText) && s222name.startsWith(finalCharText))
                                returnValue = 1;
                        }
                        return returnValue;
                    }
                });

                if (arrayList.size() == 0) {
                    SongInfoModel modelClass = new SongInfoModel();
                    modelClass.setSongName("No Result Found"); //context.getResources().getString(R.string.noresultfound);
                    arrayList.add(0, modelClass);
                }
            }
            removeDuplicates(arrayList);
            notifyDataSetChanged();
        } catch (Exception e) {
//            ExceptionTrack.getInstance().TrackLog(e);
            e.printStackTrace();
        }
    }

    private ArrayList<SongInfoModel> removeDuplicates(List<SongInfoModel> list) {
        Set set = new TreeSet(new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                if (String.valueOf(((SongInfoModel) o1).getId()).equalsIgnoreCase(String.valueOf(((SongInfoModel) o2).getId())) /*&&
                ((Blog)o1).getName().equalsIgnoreCase(((Blog)o2).getName())*/) {
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);
        if (arrayList != null && arrayList.size() > 0) {
            arrayList.clear();
            arrayList.addAll(set);
        }
        return arrayList;
    }


}
