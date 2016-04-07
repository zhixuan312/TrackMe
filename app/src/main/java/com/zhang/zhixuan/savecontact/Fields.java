package com.zhang.zhixuan.savecontact;


import android.net.Uri;
import android.provider.BaseColumns;

public final class Fields {

    public static final String AUTHORITY = "com.zhang.zhixuan.savecontact.contactcontentprovider";

    private Fields() {}

    public static final class ContactColumns implements BaseColumns {

        private ContactColumns() {}

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/contacts");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.contact";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.contact";

        public static final String DEFAULT_SORT_ORDER = "_id ASC";

        public static final String NAME = "name";

        public static final String NUMBER = "number";

        public static final String TEXT = "text";
    }
}
