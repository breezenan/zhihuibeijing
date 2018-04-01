package com.nan.zhbj.domain;

import java.util.ArrayList;

/**
 * 组图数据bean
 * Created by nan on 2018/3/26.
 */

public class Photos {
    public PhotosData data;

    public class PhotosData {
        public ArrayList<PhotosNews> news;

        @Override
        public String toString() {
            return "PhotosData{" +
                    "news=" + news +
                    '}';
        }
    }

    public class PhotosNews {
        public String id;
        public String listimage;
        public String title;

        @Override
        public String toString() {
            return "PhotosNews{" +
                    "id='" + id + '\'' +
                    ", listimage='" + listimage + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Photos{" +
                "data=" + data +
                '}';
    }
}
