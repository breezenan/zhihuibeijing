package com.nan.zhbj.domain;

import java.util.ArrayList;

/**
 * Created by nan on 2018/3/18.
 */

public class NewsTabDataDeatil {
    public TabData data;

    public class TabData {
        public String more;
        public String title;
        public ArrayList<TabNews> news;
        public ArrayList<TabTopNews> topnews;

        @Override
        public String toString() {
            return "TabData{" +
                    "more='" + more + '\'' +
                    ", title='" + title + '\'' +
                    ", news=" + news +
                    ", topnews=" + topnews +
                    '}';
        }
    }

    public class TabNews {
        public int id;
        public String listimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;

        @Override
        public String toString() {
            return "TabNews{" +
                    "id=" + id +
                    ", listimage='" + listimage + '\'' +
                    ", pubdate='" + pubdate + '\'' +
                    ", title='" + title + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public class TabTopNews {
        public int id;
        public String topimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;

        @Override
        public String toString() {
            return "TabTopNews{" +
                    "id=" + id +
                    ", topimage='" + topimage + '\'' +
                    ", pubdate='" + pubdate + '\'' +
                    ", title='" + title + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewsTabDataDeatil{" +
                "data=" + data +
                '}';
    }
}
