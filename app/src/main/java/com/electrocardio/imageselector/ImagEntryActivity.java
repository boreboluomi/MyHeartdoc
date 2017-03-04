package com.electrocardio.imageselector;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.imageselector.adapter.FolderAdapter;
import com.electrocardio.imageselector.bean.Folder;
import com.electrocardio.imageselector.bean.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by yangzheng on 2016/05/10.
 */
public class ImagEntryActivity extends FragmentActivity{

    private ListView mListView;
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    private List<Folder> folderList = new ArrayList<>();
    private boolean hasFolderGened = false;
    private ImageConfig imageConfig;
    private FolderAdapter folderAdapter;
    private Boolean isFrist=true;
    public static final String EXTRA_RESULT = "select_result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagentry_activity);

        init();
        getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    private void init() {
        findViewById(R.id.back).setVisibility(View.GONE);
        TextView title= (TextView) findViewById(R.id.title_text);
         TextView title_right= (TextView) findViewById(R.id.title_right);
        mListView = (ListView) findViewById(R.id.im_listview);
        imageConfig = ImageSelector.getImageConfig();
        folderAdapter = new FolderAdapter(this, imageConfig);
        BaseApplication.getInstance().addActivity(ImagEntryActivity.this);
        if(isFrist){
            Intent intent = new Intent(ImagEntryActivity.this, ImageSelectorActivity.class);
            intent.putExtra("position", 0);
            startActivityForResult(intent, 2000);
            isFrist=false;
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ImagEntryActivity.this, ImageSelectorActivity.class);
                intent.putExtra("position", i);
                startActivityForResult(intent,2000);
            }
        });
        title.setText("相册");
        title_right.setText("取消");
      title_right.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              setResult(RESULT_CANCELED);
              finish();
          }
      });
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader =
                        new CursorLoader(ImagEntryActivity.this,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                                null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(ImagEntryActivity.this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    List<Image> tempImageList = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        tempImageList.add(image);
                        if (!hasFolderGened) {
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.cover = image;
                            if (!folderList.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                folderList.add(folder);
                            } else {
                                Folder f = folderList.get(folderList.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    } while (data.moveToNext());
                    mListView.setAdapter(folderAdapter);
                    folderAdapter.setData(folderList);
                    hasFolderGened = true;

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == 2000 && resultCode == RESULT_OK){
           Intent intent = new Intent();
           intent.putExtra(EXTRA_RESULT,data.getStringExtra(ImageSelectorActivity.EXTRA_RESULT));
           System.out.println(data.getStringExtra(ImageSelectorActivity.EXTRA_RESULT)+"");
           setResult(RESULT_OK, intent);
           finish();
       }
        if(resultCode==1001){
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}