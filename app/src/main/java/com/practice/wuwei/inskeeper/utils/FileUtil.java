package com.practice.wuwei.inskeeper.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author by wuwei
 * @date on 2018/2/9 下午10:14
 */

public class FileUtil {
    public static void saveFile(Context context, int notify_id, byte[] bytes, String fileName, boolean isVideo) {
        try {
            String pathAdditional = "/InsKeeper/InsImage/";
            if (isVideo) {
                pathAdditional = "/InsKeeper/InsVideo/";
            }
            File dirFile = new File(Environment.getExternalStorageDirectory().getPath() + pathAdditional);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File myCaptureFile = new File(dirFile.getAbsolutePath() +"/"+ fileName);


//        fileName = UUID.randomUUID().toString()+".jpg";
//        File myCaptureFile = new File(Environment.getExternalStorageDirectory().getPath() +"/DCIM/Camera/"+ fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bos.write(bytes);        //bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();

            //把图片保存后声明这个广播事件通知系统相册有新图片到来
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(myCaptureFile);
            intent.setData(uri);
            context.sendBroadcast(intent);
            NotificationUtil.sendFinishNotification(context,notify_id,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            NotificationUtil.sendErrorNotification(context, notify_id, "资源保存失败");
        }
    }
}
