package com.galaxy.loadviewdemo;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.nexta.launcher.constant.AppConstants.MAX_LENGTH_DEVICE_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.TypeConverter;

import com.google.android.exoplayer2.C;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.merchant.base.helper.DirectoryHelper;
import com.merchant.base.helper.LocalDataHelper;
import com.merchant.base.utils.DateTimeUtils;
import com.nexta.launcher.BuildConfig;
import com.nexta.launcher.MyApplication;
import com.nexta.launcher.R;
import com.nexta.launcher.base.model.CategoryDefine;
import com.nexta.launcher.base.model.VideoDetailResponse;
import com.nexta.launcher.base.network.ApiConstant;
import com.nexta.launcher.constant.AppConstants;
import com.nexta.launcher.functions.block.app.service.ConstantsService;
import com.nexta.launcher.functions.block.app.service.TrackingAndSocketService;
import com.nexta.launcher.manager.BookManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by Vuongph on 22/08/2019
 */
public class Utils {
    private static final String FORMAT_JP_NUMBER = "#,###";
    private static final String FORMAT_AMOUNT_NUMBER = "#,###";
    public static final long CLICK_DELAY_MSEC_500 = 500;



    public static void setFullScreen(AppCompatActivity activity) {
        View decorView = activity.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static void hideActionBar(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static void showActionBar(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    public static int getScreenWidth(Context mContext) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = Objects.requireNonNull(wm).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    public static int getScreenWidth(Activity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getHeightOfView(View contentView) {
        contentView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //contentview.getMeasuredWidth();
        return contentView.getMeasuredHeight();
    }

    public static void setupDialog(Dialog dialog, int width, int height, int x, int y) {
        if (dialog == null) {
            return;
        }
        //set dialog size
        Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);

        //set dialog position
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.START;
        wmlp.y = y;
        wmlp.x = x;
        dialog.getWindow().setAttributes(wmlp);
    }

    public static void setupDialog(Dialog dialog, int width, int height) {
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        //set dialog size
        dialog.getWindow().setLayout(width, height);
    }

    public static int[] getViewLocation(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location;
    }

    public static SpannableStringBuilder getTextDifferentSize(String text, float propotion, int start, int end) {
        if (text == null || text.isEmpty()) {
            return new SpannableStringBuilder(AppConstants.EMPTY_STR);
        }
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(text);
        //make the textsize 2 times.
        spanTxt.setSpan(new RelativeSizeSpan(propotion), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanTxt;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float dpToPx(float dp) {
        return (float) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void pickGallery(Fragment fragment, int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "No App.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("IntentReset")
    public static void pickImageOrVideo(Fragment fragment, int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fragment.startActivityForResult(Intent.createChooser(intent, "Select Video or Image"), requestCode);
        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "No App.", Toast.LENGTH_SHORT).show();
        }
    }


    public static Uri pickCamera(Fragment fragment, int requestCode) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            Uri uri = fragment.getContext().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            fragment.startActivityForResult(intent, requestCode);
            return uri;
        } catch (Exception e) {
            Log.d("pickCamera: ", e.getMessage());
            Toast.makeText(fragment.getContext(), "No App.", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    public static Uri pickRecord(Fragment fragment, int requestCode) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, "New Video");
            values.put(MediaStore.Video.Media.DESCRIPTION, "From your Camera");
            Uri uri = fragment.getContext().getContentResolver().insert(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            fragment.startActivityForResult(intent, requestCode);
            return uri;
        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "No App.", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private static long mOldClickTime = 0;

    public static boolean isEnabledClick(long clickDelayMillis) {
        long time = System.currentTimeMillis();
        long diffTime = time - mOldClickTime;
        if (diffTime > 0 && diffTime < clickDelayMillis) {
            return false;
        }
        mOldClickTime = time;
        return true;
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceToken(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getFullNameDefault(Context context) {
        String deviceToken = Utils.getDeviceToken(context);
        if (deviceToken != null && deviceToken.length() > AppConstants.ZERO) {
            return deviceToken.length() > MAX_LENGTH_DEVICE_ID ? context.getString(R.string.title_name_default, deviceToken.substring(AppConstants.ZERO, MAX_LENGTH_DEVICE_ID)) : context.getString(R.string.title_name_default, deviceToken);
        }
        return AppConstants.EMPTY_STR;
    }

    @WorkerThread
    public static String convertFileToBase64(String imageFilePath) {
        Bitmap bm = null;
        try {
            File imgFile = new File(imageFilePath);
            if (imgFile.exists() && imgFile.length() > 0) {
                bm = BitmapFactory.decodeFile(imageFilePath);
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 80, bOut);
                return ApiConstant.BASE64 + Base64.encodeToString(bOut.toByteArray(), Base64.NO_WRAP);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bm != null) {
                bm.recycle();
            }
        }
    }

    public static String convertBitmapToBase64(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bOut);
                return ApiConstant.BASE64 + Base64.encodeToString(bOut.toByteArray(), Base64.NO_WRAP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapByStringBase64(String base64) {
        try {
            if (base64 == null) {
                return null;
            }
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (bitmap.getHeight() < bitmap.getWidth() * 4) {
                Bitmap newb = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth() * 4, Bitmap.Config.ARGB_8888);
                Canvas cv = new Canvas(newb);
                cv.drawBitmap(bitmap, 0, 0, null);
                cv.save();
                cv.restore();
                return newb;
            }
            return bitmap;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String getImagePath(String pathUriImage, Activity activity) {
        Uri selectedImage = Uri.parse(pathUriImage);
        String filePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(Objects.requireNonNull(selectedImage), filePathColumn, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath == null ? selectedImage.getPath() : filePath;
    }

    public static void sendInstallApp(Context context, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getColumnAutoFitGridLayout(Context context, float columnWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidth / columnWidth + 0.5);
    }

    public static String getDurationString(int seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat(seconds >= 3600 ? "HH:mm:ss" : "mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

    @SuppressLint("IntentReset")
    public static void pickFileGallery(Fragment fragment, int requestCode) {
        try {
            Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            fragment.startActivityForResult(takePictureIntent, requestCode);
        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "No App.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getMimeType(String url) {
        try {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return type;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMimeType(Uri uri) {
        try {
            String extension;
            final MimeTypeMap mime = MimeTypeMap.getSingleton();

            //Check uri format to avoid null
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                //If scheme is a content
                extension = mime.getExtensionFromMimeType(MyApplication.applicationContext.getContentResolver().getType(uri));
            } else {
                //If scheme is a File
                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

            }

            return mime.getMimeTypeFromExtension(extension);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Loading html string in textView
     *
     * @param tv:         TextView
     * @param htmlString: string of html
     */
    public static void loadHtml(TextView tv, String htmlString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv.setText(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv.setText(Html.fromHtml(htmlString));
        }
    }

    /**
     * get video path from local
     *
     * @param videoUrl video url
     * @return
     */
    public static String getVideoPath(String videoUrl) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + DirectoryHelper.ROOT_DIRECTORY_NAME + "/" + DirectoryHelper.MEDIA + "/" + getVideoName(videoUrl);
    }

    public static String getVideoName(String videoUrl) {
        return videoUrl != null ? videoUrl.substring(videoUrl.lastIndexOf('/') + 1) : AppConstants.EMPTY_STR;
    }

    public static String getDocPath(String filePath) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + DirectoryHelper.ROOT_DIRECTORY_NAME + "/" + DirectoryHelper.DOC + "/" + getVideoName(filePath);
    }

    /**
     * check file media exits
     *
     * @param filePath path file
     * @return
     */
    public static boolean isMediaExistInLocal(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * delete file
     *
     * @param filePath
     */
    public static void clearFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }


    public static Object cloneObject(Object o) {
        String s = serializeObject(o);
        return unserializeObject(s, o);
    }

    public static String serializeObject(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static Object unserializeObject(String s, Object o) {
        Gson gson = new Gson();
        return gson.fromJson(s, o.getClass());
    }

    @TypeConverter
    public static String convertListToString(List<VideoDetailResponse.VideoDatabaseDetail.Section> list) {
        if (list == null || list.isEmpty()) {
            return AppConstants.EMPTY_STR;
        }
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<VideoDetailResponse.VideoDatabaseDetail.Section> convertStringToList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<VideoDetailResponse.VideoDatabaseDetail.Section>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static void pickFileVideo(Fragment fragment, int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            fragment.startActivityForResult(Intent.createChooser(intent, "Select Video"), requestCode);
        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "No App.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void pickFile(Fragment fragment, int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode);
        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "No App.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getMediaUrl(String url) {
        if (!TextUtils.isEmpty(url) && !url.toLowerCase().contains("http") && !url.startsWith("content:") && !url.startsWith("file://")) {
            if (url.startsWith("/")) {
                return BuildConfig.BASE_URL_MEDIIA.substring(0, BuildConfig.BASE_URL_MEDIIA.length() - 1) + url;
            } else {
                return BuildConfig.BASE_URL_MEDIIA + url;
            }
        }

        return url;
    }

    public static String getParentAvatar(String url) {
        if (url.contains("https") || url.contains("http")) {
        } else {
            url = com.stfalcon.chatkit.BuildConfig.BASE_URL + url.substring(1);
        }
        return url;
    }

    public static void tintBackgroundViewDrawable(int color, View... views) {
        for (View view : views) {
            Drawable dr = view.getBackground();
            if (dr != null) {
                dr.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    /**
     * Returns the specified millisecond time formatted as a string.
     *
     * @param builder   The builder that {@code formatter} will write to.
     * @param formatter The formatter.
     * @param timeMs    The time to format as a string, in milliseconds.
     * @return The time formatted as a string.
     */
    public static String getStringForTime(StringBuilder builder, Formatter formatter, long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        builder.setLength(0);
        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

    public static String getStringForHourMinute(StringBuilder builder, Formatter formatter, long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        builder.setLength(0);
        return formatter.format("%02d:%02d", hours, minutes).toString();
    }

    public static String getStringForTimeHours(Formatter formatter, long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        return hours > 0 ? formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
                : formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    public static String getStringForTimeHours2(long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        double totalSeconds = (timeMs) / 1000d;
        double seconds = totalSeconds % 60d;
        double minutes = (totalSeconds / 60d) % 60d;
        double hours = totalSeconds / 3600d;

        long roundHour = Math.round(hours);
        long roundMinutes = Math.round(minutes);
        long roundSecond = Math.round(seconds);
        Formatter formatter = new Formatter();
        return roundHour > 0 ? formatter.format("%02d:%02d:%02d", roundHour, roundMinutes, roundSecond).toString()
                : formatter.format("%02d:%02d", roundMinutes, roundSecond).toString();
    }

    public static String getStringForTime(long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        new StringBuilder().setLength(0);
        return new Formatter(new StringBuffer(), Locale.getDefault()).format("%02d:%02d", hours, minutes).toString();
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeForDetailMediaPlayer(long timeMs) {
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeForQuiz(long timeMs) {
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60);

        return String.format("%02d:%02d", minutes, seconds);
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeForDetailMediaPlayerV2(long timeMs) {
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (int) (totalSeconds / 60) / 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getImageBas64OrUrl(Context context, String imagePathUri) {
        if (imagePathUri == null || imagePathUri.isEmpty()) return imagePathUri;
        if (imagePathUri.startsWith("http://") || imagePathUri.startsWith("https://")) {
            return imagePathUri;
        }
        String imagePath = RealFilePath.getPath(context, Uri.parse(imagePathUri));
        String imageBase64 = Utils.convertFileToBase64(imagePath);
        return imageBase64 != null ? ApiConstant.BASE64 + imageBase64 : null;
    }

    public static String getTimeFormat(int hours, int minutes) {
        return new Formatter(new StringBuffer(), Locale.getDefault()).format("%02d:%02d", hours, minutes).toString();
    }

    public static String getTimeFormat(int hours, int minutes, int second) {
        return new Formatter(new StringBuffer(), Locale.getDefault()).format("%02d:%02d:%02d", hours, minutes, second).toString();
    }

    public static boolean isSamDayCurrent(String date) {
        return date.equals(DateTimeUtils.convertDateToString(Calendar.getInstance().getTime(), DateTimeUtils.YEAR_MONTH_DAY_FORMAT));
    }

    public static boolean isSamDay(String date, String dateCompare) {
        return DateTimeUtils.compareDate(date, dateCompare, DateTimeUtils.YEAR_MONTH_DAY_FORMAT) == DateTimeUtils.EQUAL_DATE;
    }

    public static Bitmap decodeBitmap(Context context, Uri resource, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try {
            orientation = getOrientation(context.getContentResolver().openInputStream(resource));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        if (reqWidth != -1 && reqHeight != -1) {
            options.inJustDecodeBounds = true;

            getBitmap(context, resource, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, ExifInterface.ORIENTATION_NORMAL);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
        }

        Bitmap bitmap = getBitmap(context, resource, options);

        Matrix matrix = getRotatedMatrix(orientation);

        return rotateBitmapIfNeed(matrix, bitmap);
    }

    private static int getOrientation(InputStream inputStream) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return new ExifInterface(inputStream).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
        }
        return ExifInterface.ORIENTATION_NORMAL;
    }

    private static Bitmap getBitmap(Context context, Uri resource, BitmapFactory.Options options) {
        try {
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(resource), null, options);
        } catch (Exception | OutOfMemoryError e) {
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, int orientation) {
        // Raw height and width of image
        Pair<Integer, Integer> dimension = parseDimensionFromOption(options, orientation);
        int height = dimension.first;
        int width = dimension.second;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            int halfHeight = height / 2;
            int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Pair<Integer, Integer> parseDimensionFromOption(BitmapFactory.Options options, int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_ROTATE_270:
                return Pair.create(options.outWidth, options.outHeight);
            default:
                return Pair.create(options.outHeight, options.outWidth);
        }
    }

    private static Matrix getRotatedMatrix(int orientation) {
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1f, 1f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180f);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL: {
                matrix.setRotate(180f);
                matrix.postScale(-1f, 1f);
            }
            break;
            case ExifInterface.ORIENTATION_TRANSPOSE: {
                matrix.setRotate(90f);
                matrix.postScale(-1f, 1f);
            }
            break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90f);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE: {
                matrix.setRotate(-90f);
                matrix.postScale(-1f, 1f);
            }
            break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90f);
                break;
            default:
                return null;
        }

        return matrix;
    }

    private static Bitmap rotateBitmapIfNeed(Matrix matrix, Bitmap originalBitmap) {
        if (matrix == null || originalBitmap == null) return originalBitmap;

        try {
            Bitmap result = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
            originalBitmap.recycle();
            return result;
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }

        return originalBitmap;
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void openAppGooglePlayStore(Context context, String appPackageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    public static void openSettingApp(Context context) {
        openApp(context, ConstantsService.settingApp);
    }

    public static void openApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            Utils.openAppGooglePlayStore(context, packageName);
        }
    }

    public static String removeSpace(String str) {
        return str.trim().replaceAll("\\s", "");
    }

    public static boolean equals(String s1, String s2, boolean igorneCase) {
        boolean isEmptyS1 = TextUtils.isEmpty(s1);
        boolean isEmptyS2 = TextUtils.isEmpty(s2);
        return (isEmptyS1 && isEmptyS2) || (!isEmptyS1 && !isEmptyS2 && (igorneCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2)));
    }

    public static String readTextFromAsset(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isApplicationOutOfdate(Context context) {
        String serverVersion = LocalDataHelper.getInstance(context).getServerVersion().trim().toLowerCase();
        String currentVersion = BuildConfig.VERSION_NAME.split("-")[0];
        return !TextUtils.isEmpty(serverVersion) && !TextUtils.isEmpty(currentVersion) &&
                Utils.getVersionCodeByVersionName(currentVersion) >= Utils.getVersionCodeByVersionName(serverVersion);
    }


    public static boolean deleteAllFileInFolder(File folder) {
        if (folder != null && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            return true;
        }
        return false;
    }

    public static String getCurrentVersion() {
        return BuildConfig.VERSION_NAME.split("-")[0];
    }

    public static boolean hasInstallPackagePermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.getPackageManager().canRequestPackageInstalls();
    }

    //* eg: 7.8, 7.9.01
    public static float getVersionCodeByVersionName(String versionNameWithoutPrefix) {
        try {
            if (versionNameWithoutPrefix.contains("-")) {
                String optimizeVer = versionNameWithoutPrefix.substring(0, versionNameWithoutPrefix.indexOf("-"));
                return getVersionCodeByVersionName(optimizeVer);
            } else if (versionNameWithoutPrefix.contains(".")) {
                int indexFirst = versionNameWithoutPrefix.indexOf(".");
                String inNum = versionNameWithoutPrefix.substring(0, indexFirst);
                String per = versionNameWithoutPrefix.substring(indexFirst + 1).replace(".", "");
                return Float.parseFloat(inNum + "." + per);
            } else {
                return 0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }

    public static boolean isClassCollection(Class c) {
        return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c);
    }

    public static boolean isClassCollection(Object o, Class c) {
        if (o instanceof List && !((List<?>) o).isEmpty()) {
            return ((List<?>) o).get(0).getClass().getName().equals(c.getName());
        }

        return false;
    }

    public static void keepScreenOn(AppCompatActivity activity, boolean on) {
        if (on) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public static void dismissAllDialog(FragmentManager manager) {
        List<Fragment> fragments = manager.getFragments();

        if (fragments == null)
            return;

        for (Fragment fragment : fragments) {
            if (fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismissAllowingStateLoss();
            }

            FragmentManager childFragmentManager = fragment.getChildFragmentManager();
            if (childFragmentManager != null) {
                dismissAllDialog(childFragmentManager);
            }
        }
    }

    public static Typeface getFontFamilyForQuiz(Context context) {
        String path;
        CategoryDefine categoryDefine = CategoryDefine.findCategory(BookManager.getInstance().getCategoryActive(context).getCategoryKey());
        switch (categoryDefine) {
            case MATH:
            case LITERATURE:
            case ENGLISH:
                path = "fonts/averta.ttf";
                break;

            default:
                path = "fonts/montserrat_normal.ttf";
                break;
        }
        return Typeface.createFromAsset(context.getAssets(), path);
    }

    public static int getFileCapacity(Context context, Uri uri) {
        int size = 0;
        try {
            if (uri.getScheme().equals("file")) {
                File file = new File(uri.getPath());
                size = (int) file.length();
            } else {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) size = inputStream.available();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return size;
    }

    public static boolean uriIsFile(Context context, Uri uri) {
        try {
            return getFileCapacity(context, uri) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static String scriptSelectTextListener() {
        return "<script>" +
                "let selectionDelay = null, selection = '';\n" +
                "\n" +
                "    document.addEventListener('selectionchange', () => {\n" +
                "        const currentSelection = document.getSelection().toString();\n" +
                "        if (currentSelection != selection) {\n" +
                "            nexta.moveSelectTextCursor();" +
                "            selection = currentSelection;\n" +
                "            if (selectionDelay) {\n" +
                "                window.clearTimeout(selectionDelay);\n" +
                "            }\n" +
                "            selectionDelay = window.setTimeout(() => {\n" +
                "                nexta.changeTextSelected(currentSelection);\n" +
                "                selection = '';\n" +
                "                selectionDelay = null;\n" +
                "            }, 500);\n" +
                "        }\n" +
                "    });" +
                "</script>";
    }

    public static String removeHtmlTag(String string) {
        return Html.fromHtml(string).toString();
    }

    public static void killBackgroundProcesses(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(packageName);
    }

    public static Uri parseUri(Context context, File file) {
        try {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void openMedia(Context context, Uri uri, String type) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.open_with)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.error_open_uri), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    public static void cancelNotification(Context context, int notificationId) {
        if (notificationId <= 0) return;
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(notificationId);
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    public static boolean isHomeApp(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        return res != null && res.activityInfo != null && context.getPackageName()
                .equals(res.activityInfo.packageName);
    }

    public static void startHome(Context context) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    public static String convertImageToBase64(Context context, Uri uri) {
        try {
            int reqSize = context.getResources().getDimensionPixelOffset(R.dimen._90sdp);
            Bitmap bitmap = Utils.decodeBitmap(context, uri, reqSize, reqSize);
            File imageFile = new File(context.getCacheDir(), "tempFile.jpg");
            OutputStream os;
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
            return Utils.convertFileToBase64(imageFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("Error", "Error writing bitmap", e);
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        String pkgName = getCurrentAppRunning(context);
        return packageName != null && packageName.equals(pkgName);
    }

    public static String getCurrentAppRunning(Context context) {
        String pkgName = null;
        UsageStatsManager usageStatsManager = (UsageStatsManager) context
                .getSystemService(Context.USAGE_STATS_SERVICE);
        final long timeTnterval = 1000 * 600;
        final long endTime = System.currentTimeMillis();
        final long beginTime = endTime - timeTnterval;
        final UsageEvents myUsageEvents = usageStatsManager.queryEvents(beginTime, endTime);
        while (myUsageEvents.hasNextEvent()) {
            UsageEvents.Event myEvent = new UsageEvents.Event();
            myUsageEvents.getNextEvent(myEvent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                switch (myEvent.getEventType()) {
                    case UsageEvents.Event.ACTIVITY_RESUMED:
                        pkgName = myEvent.getPackageName();
                        break;
                    case UsageEvents.Event.ACTIVITY_PAUSED:
                        if (myEvent.getPackageName().equals(pkgName)) {
                            pkgName = null;
                        }
                }
            } else {
                switch (myEvent.getEventType()) {
                    case UsageEvents.Event.MOVE_TO_FOREGROUND:
                        pkgName = myEvent.getPackageName();
                        break;
                    case UsageEvents.Event.MOVE_TO_BACKGROUND:
                        if (myEvent.getPackageName().equals(pkgName)) {
                            pkgName = null;
                        }
                }
            }
        }
        return pkgName;
    }

    public static void enableComponent(Context context, Class<?> cls) {
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void alphaEnable(View view, boolean enable) {
        view.setAlpha(enable ? 1f : 0.5f);
        view.setEnabled(enable);
    }

    public static int getStatusbarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
