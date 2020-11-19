package com.example.justificanteemulsa.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

  private static final Uri FolderUri = Uri.parse(Environment.DIRECTORY_DOWNLOADS);

  public static void showToast(Activity activity, String msg) {
    Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
  }


  public static Uri getFolderUri() {
    return FolderUri;
  }

  public static void SaveSignature(Activity activity, String filename, Bitmap signature) {
    Log.d("UTILS", "PDF Filename: " + filename);
    try {
      FileOutputStream stream = activity.openFileOutput(filename, Context.MODE_PRIVATE);
      signature.compress(Bitmap.CompressFormat.PNG, 100, stream);

      // Limpieza
      stream.close();
      signature.recycle();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <T extends View> List<T> findViewsWithType(View root, Class<T> type) {
    List <T> views = new ArrayList<>();
    findViewsWithType(root, type, views);
    return views;
  }

  private static <T extends View> void findViewsWithType(View view, Class<T> type, List<T> views) {
    if (type.isInstance(view)) {
      views.add(type.cast(view));
    }

    if (view instanceof ViewGroup) {
      ViewGroup viewGroup = (ViewGroup) view;
      for (int i = 0; i < viewGroup.getChildCount(); i++) {
        findViewsWithType(viewGroup.getChildAt(i), type, views);
      }
    }
  }

  public static Bitmap LoadSignature(Activity activity, String s) {
    Bitmap bitmap = null;
    try {
      FileInputStream stream = activity.openFileInput(s);
      bitmap = BitmapFactory.decodeStream(stream);
      stream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return bitmap;
  }
}
