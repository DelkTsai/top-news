package qb.com.top_news.adatper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import qb.com.top_news.R;
import qb.com.top_news.application.MyApplication;
import qb.com.top_news.vo.News;

public class MyListViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<News> newsList;
    private DbUtils db;
    public int count;
    public int MAX_ITEM;

    public MyListViewAdapter(Context mContext, List<News> newsList) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.newsList = newsList;
        db = MyApplication.getDb();
        MAX_ITEM = newsList.size();
        count = MAX_ITEM / 3;

    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.author = (TextView) convertView.findViewById(R.id.author);
            viewHolder.realtype = (TextView) convertView.findViewById(R.id.realtype);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.pic = (ImageView) convertView.findViewById(R.id.pic);
            viewHolder.like = (ImageView) convertView.findViewById(R.id.like);
            viewHolder.tvZan = (TextView) convertView.findViewById(R.id.tvZan);
            viewHolder.tvCai = (TextView) convertView.findViewById(R.id.tvCai);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        News news = null;
        try {
            news = db.findFirst(Selector.from(News.class).where("title", "=", newsList.get(position).getTitle()));
        } catch (DbException e) {
            e.printStackTrace();
        }

        viewHolder.title.setText(newsList.get(position).getTitle());
        viewHolder.author.setText(newsList.get(position).getAuthor_name());
        if (newsList.get(position).getRealtype() != null) {
            viewHolder.realtype.setText(newsList.get(position).getRealtype());
        } else {
            viewHolder.realtype.setText(newsList.get(position).getCategory());
        }
        viewHolder.date.setText(newsList.get(position).getDate());
        Picasso.with(mContext).load(newsList.get(position).getThumbnail_pic_s03()).into(viewHolder.pic);
        if (news == null) {
            news = newsList.get(position);
            news.setZan(new Random().nextInt(10000) + 100);
            news.setCai(new Random().nextInt(500) + 100);
            viewHolder.tvZan.setText(String.valueOf(news.getZan()));
            viewHolder.tvCai.setText(String.valueOf(news.getCai()));
            viewHolder.like.setImageResource(R.drawable.like_normol);
        } else {
            if (news.getLike() == 0) {
                viewHolder.like.setImageResource(R.drawable.like_normol);
            } else {
                viewHolder.like.setImageResource(R.drawable.like_press);
            }
            viewHolder.tvZan.setText(String.valueOf(news.getZan()));
            viewHolder.tvCai.setText(String.valueOf(news.getCai()));
        }
        final News finalNews = news;
        final int[] zanAll = {10};
        final int[] caiAll = {10};
        viewHolder.tvZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (--zanAll[0] < 0) {
                    Toast.makeText(mContext, "一人只能点赞10次，不要恶意刷赞哦", Toast.LENGTH_SHORT).show();
                } else {
                    viewHolder.tvZan.setText(String.valueOf(finalNews.getZan() + 1));
                    finalNews.setZan(finalNews.getZan() + 1);
                    try {
                        db.saveOrUpdate(finalNews);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        viewHolder.tvCai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (--caiAll[0] < 0) {
                    Toast.makeText(mContext, "一人只能差评10次，不要恶意乱点哦", Toast.LENGTH_SHORT).show();
                } else {
                    viewHolder.tvCai.setText(String.valueOf(finalNews.getCai() + 1));
                    finalNews.setCai(finalNews.getCai() + 1);
                    try {
                        db.saveOrUpdate(finalNews);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalNews.getLike() == 0) {
                    viewHolder.like.setImageResource(R.drawable.like_press);
                    finalNews.setLike(1);
                    Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();

                } else {
                    viewHolder.like.setImageResource(R.drawable.like_normol);
                    finalNews.setLike(0);
                    Toast.makeText(mContext, "取消收藏", Toast.LENGTH_SHORT).show();
                }
                try {
                    db.saveOrUpdate(finalNews);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            db.saveOrUpdate(finalNews);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView author;
        TextView realtype;
        TextView date;
        ImageView pic;
        TextView tvCai;
        TextView tvZan;
        ImageView like;
    }

}
