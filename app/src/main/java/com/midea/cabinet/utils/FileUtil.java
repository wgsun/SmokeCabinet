package com.midea.cabinet.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by chenpu on 18-5-30.
 */

public class FileUtil {

    private static final int FILE_NUM_SUPER_MAX = 50;
    private static ExecutorService sExecutor;

    public static boolean string2File(final String input, final String filePath) {
        if (!FileUtils.createOrExistsFile(filePath)) {
            LogUtils.e("string to " + filePath + " failed!");
            return false;
        }
        if (sExecutor == null) {
            sExecutor = Executors.newSingleThreadExecutor();
        }
        Future<Boolean> submit = sExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(filePath));
                    bw.write(input);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            return submit.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean bytes2File(final byte[] input, final String filePath) {
        if (!FileUtils.createOrExistsFile(filePath)) {
            LogUtils.e("bytes to " + filePath + " failed!");
            return false;
        }
        if (sExecutor == null) {
            sExecutor = Executors.newSingleThreadExecutor();
        }
        Future<Boolean> submit = sExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(filePath);
                    fos.write(input);
                    fos.flush();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            return submit.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean bytesAppend2File(final byte[] input, final String filePath) {
        if (!FileUtils.createOrExistsFile(filePath)) {
            LogUtils.e("bytes to " + filePath + " failed!");
            return false;
        }
        if (sExecutor == null) {
            sExecutor = Executors.newSingleThreadExecutor();
        }
        Future<Boolean> submit = sExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(filePath,true);
                    fos.write(input);
                    fos.flush();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            return submit.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean bytes2FileSync(final byte[] input, final String filePath) {
        if (!FileUtils.createOrExistsFile(filePath)) {
            LogUtils.e("bytes to " + filePath + " failed!");
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(input);
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static long getTotalExternalMemorySize() {
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            //??????SDCard?????????
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long size = stat.getBlockSizeLong() * stat.getBlockCountLong();
//            LogUtils.v("getTotalExternalMemorySize??? " + size);
            return size;
        } else {
            return -1;
        }
    }

    /**
     * ??????????????????????????????
     * @return ????????????
     */
    public static final String writeBytesToFile(byte[] bytes, String cameraName) {
        // ??????????????????????????????????????????
        if(calculateFileNum(getImageFileDir()) >= FILE_NUM_SUPER_MAX) {
            deleteFolderFile(getImageFileDir(), false);
        }

        String filePath = getImageFilePath(cameraName);
        if(fileIsExists(filePath)) {
            deleteFile(filePath);
        }

        try {
            //?????????????????????File??????????????????????????????
            LogUtils.d( "filePath = "+filePath);
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            file.createNewFile();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return "";
        }

        return filePath;
    }

    public static String getImageFilePath(String cameraName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory()).append("/midea/image/").append(cameraName).append(".jpg");

        return stringBuilder.toString();
    }

    public static String getImageFileDir() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory()).append("/midea/image/");
        return stringBuilder.toString();
    }


    public static String getImageZipPath(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory()).append("/midea/image.zip");
        return stringBuilder.toString();
    }

    /** ?????????????????????????????? */
    public static int calculateFileNum(String path) {
        int count = 0;
        File dir = new File(path);
        if(dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(File file:files) {
                if(!file.isDirectory()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * ????????????????????????????????????
     * @param filePath
     * @param deleteThisPath
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// ????????????
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// ????????????????????????
                        file.delete();
                    } else {// ??????
                        if (file.listFiles().length == 0) {// ??????????????????????????????????????????
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????????????????
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * ??????????????????
     * @param filePath
     */
    public static void deleteFile(String filePath){
        File file = new File(filePath);
        file.delete();
    }

    public static void copyFile(String filename) {
        InputStream in = null;
        FileOutputStream out = null;
        // path???????????????
        String path = "/data/camera/"+filename; // data/data??????
        File file = new File(path);
        if (!file.exists()) {
            try {
                in = Utils.getApp().getAssets().open(filename); // ???assets???????????????
                out = new FileOutputStream(file);
                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public static void copyFile(String filename,String resFile) {
            InputStream in = null;
            FileOutputStream out = null;
            // path???????????????
            String path = "/data/camera/"+filename; // data/data??????
            File file = new File(path);
            if (!file.exists()) {
                try {
                    in = Utils.getApp().getAssets().open(resFile); // ???assets???????????????
                    out = new FileOutputStream(file);
                    int length = -1;
                    byte[] buf = new byte[1024];
                    while ((length = in.read(buf)) != -1) {
                        out.write(buf, 0, length);
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
    }
}