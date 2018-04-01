package com.nan.zhbj.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nan.zhbj.MainActivity;
import com.nan.zhbj.R;
import com.nan.zhbj.domain.NewsMenu;
import com.nan.zhbj.pager.NewsPager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * Created by nan on 2018/3/3.
 */
@ContentView(R.layout.fragment_left_menu)
public class LeftMenuFragment extends BaseFragment {
    private List<NewsMenu.NewsMenuData> mData;
    @ViewInject(R.id.lv_left_list)
    private ListView mListView;

    private int mSelectedPosition;

    @Override
    public void initData() {
    }

    /*@Override
    public View initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_left_menu, null);
        mListView = view.findViewById(R.id.lv_left_list);
        return view;
    }*/

    public void setMenuData(List<NewsMenu.NewsMenuData> data) {
        //NewsPager数据刷新时，默认选择新闻项
        mSelectedPosition = 0;
        mData = data;
        final MenuDataAdaper menuDataAdaper = new MenuDataAdaper();
        mListView.setAdapter(menuDataAdaper);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                setCurrentMenuDetailPager(position);
                menuDataAdaper.notifyDataSetChanged();
                toggle();
            }
        });
    }

    /**
     * 将用户点击的位置传递给contentFragment 中的newspager
     *
     * @param position
     */
    private void setCurrentMenuDetailPager(int position) {
        //得到父级容器Activity
        MainActivity mainUI = (MainActivity) mActivity;
        //找到NewsPager所在的ContentFragment
        ContentFragment contentFragment = (ContentFragment) mainUI.getFragmentByTag(MainActivity.TAG_CONTENT_FRAGMENT);
        //由ContentFragment去找当前页
        NewsPager newsPager = (NewsPager) contentFragment.getContentPager(1);
        //将用户的选择传递给NewsPager
        newsPager.setCurrentMenuDetailPager(position);
    }

    /**
     * 侧边栏展开与收回
     */
    private void toggle() {
        ((MainActivity) mActivity).getSlidingMenu().toggle();
    }

    class MenuDataAdaper extends BaseAdapter {
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mActivity, R.layout.left_menu_list, null);
            TextView textView = view.findViewById(R.id.tv_item);
            textView.setText(getItem(position).title);
            if (position == mSelectedPosition) {
                textView.setEnabled(true);
            } else {
                textView.setEnabled(false);
            }
            return view;
        }
    }
}
