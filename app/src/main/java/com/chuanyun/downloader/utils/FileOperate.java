package com.chuanyun.downloader.utils;

import android.content.Intent;
import android.net.Uri;

import com.chuanyun.downloader.app.App;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

//文件操作
public class FileOperate {
    static BufferedReader br = null;
    static BufferedWriter bw = null;
    static FileInputStream fin = null;
    static FileOutputStream fout = null;
    static InputStreamReader isr = null;
    static String line = "";
    static OutputStreamWriter osw;

    public static String 子文本替换(String str, String str2, String str3) {
        if ("".equals(str2) || "".equals(str)) {
            return "";
        }
        return str.replaceAll("\\Q" + str2 + "\\E", str3);
    }

    //修改文件名称
    public static boolean changeTheFileName(String str, String str2) {
        if (str2.equals(str)) {
            return true;
        }
        File file = new File(str);
        if (!file.exists()) {
            return false;
        }
        File file2 = new File(str2);
        return !file2.exists() && file.renameTo(file2);
    }

    //删除文件
    public static boolean deleteFile(String str) {
        File file = new File(str);
        return file.exists() && !file.isDirectory() && file.delete();
    }

    //创建目录
    public static boolean createDirectory(String str) {
        String[] split = str.split("/");
        String str2 = split[0];
        if (split.length <= 0) {
            return false;
        }
        String str3 = str2;
        boolean z = true;
        for (int i = 1; i < split.length; i++) {
            str3 = str3 + "/" + split[i];
            File file = new File(str3);
            if (!file.exists()) {
                z = file.mkdir();
            }
        }
        return z;
    }

    //删除目录
    public static boolean deleteDirectory(String str) {
        File file = new File(str);
        if (file.exists()) {
            return deleteDir(file);
        }
        return false;
    }

    private static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            for (String str : file.list()) {
                if (!deleteDir(new File(file, str))) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    //是否为目录
    public static boolean isDirectory (String str) {
        File file = new File(str);
        return file.exists() && file.isDirectory();
    }

    //是否为隐藏文件
    public static boolean isHiddenFile (String str) {
        File file = new File(str);
        if (file.exists()) {
            return file.isHidden();
        }
        return false;
    }

    //文件是否存在
    public static boolean isExistFile (String str) {
        return new File(str).exists();
    }

    //取文件编码
    public static String getFileEncoded(String str) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(str)));
            bufferedInputStream.mark(4);
            byte[] bArr = new byte[3];
            bufferedInputStream.read(bArr);
            bufferedInputStream.reset();
            byte b = bArr[0];
            if (b == -17 && bArr[1] == -69 && bArr[2] == -65) {
                return "utf-8";
            }
            if (b == -1 && bArr[1] == -2) {
                return "unicode";
            }
            if (b == -2 && bArr[1] == -1) {
                return "utf-16be";
            }
            if (b == -1) {
                if (bArr[1] == -1) {
                    return "utf-16le";
                }
            }
            return "GBK";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    //读入文本文件
    public static String readTxtFile (String str, String str2) {
        FileInputStream fileInputStream = null;
        String str3 = "";
        String str4 = "";
        if (!new File(str).exists()) {
            return "";
        }
        try {
            fileInputStream = new FileInputStream(str);
            int available = fileInputStream.available();
            byte[] bArr = new byte[available];
            fileInputStream.read(bArr);
            str3 = new String(bArr, 0, available, str2);
        } catch (Exception e) {
            e = e;
        }
        try {
            fileInputStream.close();
            return str3;
        } catch (Exception e2) {
            str4 = str3;
            e2.printStackTrace();
            return str4;
        }
    }

    //写出文本文件
    public static boolean writeTxtFile (String str, String str2, String str3) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str);
            fileOutputStream.write(str2.getBytes(str3));
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //读入字节文件
    public static byte[] readByteFile (String str) {
        byte[] bArr = null;
        if (!new File(str).exists()) {
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr);
            fileInputStream.close();
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return bArr;
        }
    }

    //写出字节文件
    public static boolean writeByteFile (String str, byte[] bArr) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str);
            fileOutputStream.write(bArr);
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //取文件尺寸
    public static long getFileSize (String str) {
        long fileSize;
        File file = new File(str);
        try {
            if (file.isDirectory()) {
                fileSize = getFileSizes(file);
            } else {
                fileSize = getFileSize(file);
            }
            return fileSize;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private static long getFileSize(File file) throws Exception {
        if (file.exists()) {
            return new FileInputStream(file).available();
        }
        file.createNewFile();
        return 0L;
    }

    private static long getFileSizes(File file) throws Exception {
        long fileSize;
        File[] listFiles = file.listFiles();
        long j = 0;
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                fileSize = getFileSizes(listFiles[i]);
            } else {
                fileSize = getFileSize(listFiles[i]);
            }
            j += fileSize;
        }
        return j;
    }

    //读入资源文件
    public static String readAssetsFile(String str, String str2) {
        try {
            InputStream open = App.getApp().getAssets().open(str);
            if (open == null) {
                return "";
            }
            int available = open.available();
            byte[] bArr = new byte[available];
            open.read(bArr);
            String str3 = new String(bArr, 0, available, str2);
            open.close();
            return str3;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //读入资源文件2
    public static byte[] readAssetsByteFile(String str) {
        try {
            InputStream open = App.getApp().getAssets().open(str);
            if (open == null) {
                return null;
            }
            byte[] bArr = new byte[open.available()];
            open.read(bArr);
            open.close();
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //写出资源文件
    public static boolean writeAssetsFile (String str, String str2) {
        try {
            InputStream open = App.getApp().getAssets().open(str);
            if (open == null) {
                return false;
            }
            return writeStreamToFile(open, new File(str2));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean writeStreamToFile(InputStream inputStream, File file) {
        FileOutputStream fileOutputStream = null;
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileOutputStream.flush();
                    try {
                        fileOutputStream.close();
                        inputStream.close();
                        return true;
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        return false;
                    }
                }
            }
        } catch (Exception e3) {
            fileOutputStream2 = fileOutputStream;
            e3.printStackTrace();
            try {
                fileOutputStream2.close();
                inputStream.close();
                return false;
            } catch (IOException e4) {
                e4.printStackTrace();
                return false;
            }
        } catch (Throwable th2) {
            fileOutputStream2 = fileOutputStream;
            try {
                fileOutputStream2.close();
                inputStream.close();
                throw th2;
            } catch (IOException e5) {
                e5.printStackTrace();
                return false;
            }
        }
    }

    //寻找文件关键词
    public static String searchFileKeywords(String str, String str2) {
        String str3 = "";
        for (File file : new File(str).listFiles()) {
            if (file.getName().indexOf(str2) >= 0) {
                str3 = file.getPath() + "\n" + str3;
            }
        }
        return str3;
    }

    //寻找文件后缀名
    public static String searchFileSuffix (String str, String str2) {
        String str3 = "";
        for (File file : new File(str).listFiles()) {
            if (file.getPath().substring(file.getPath().length() - str2.length()).equals(str2) && !file.isDirectory()) {
                str3 = file.getPath() + "\n" + str3;
            }
        }
        return str3;
    }

    //复制文件
    public static boolean copyFile (String str, String str2) {
        if (new File(str).exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(str);
                FileOutputStream fileOutputStream = new FileOutputStream(str2);
                byte[] bArr = new byte[1444];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileInputStream.close();
                        fileOutputStream.close();
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //创建文件
    public static boolean createFile (String str) {
        File file = new File(str);
        if (file.exists()) {
            return true;
        }
        try {
            return file.createNewFile();
        } catch (IOException unused) {
            return false;
        }
    }

    //打开文本文件_读
    public static boolean openTxtFileRead(String str, String str2) {
        if (new File(str).exists()) {
            try {
                fin = new FileInputStream(str);
                isr = new InputStreamReader(fin, str2);
                br = new BufferedReader(isr);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //
    public static boolean closeRead() {
        try {
            br.close();
            fin.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //读一行
    public static String readOnlyLine() {
        try {
            String readLine = br.readLine();
            line = readLine;
            return readLine != null ? readLine : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //打开文本文件_写
    public static boolean openTxtFileWrite(String str, String str2) {
        if (new File(str).exists()) {
            try {
                fout = new FileOutputStream(str);
                osw = new OutputStreamWriter(fout, str2);
                bw = new BufferedWriter(osw);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //关闭写
    public static boolean closeWrite() {
        try {
            bw.close();
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //写一行
    public static boolean writeOnlyLine(String str) {
        try {
            bw.newLine();
            bw.write(str);
            bw.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //取子目录
    public static String getSubDirectories (String str) {
        File[] listFiles = new File(str).listFiles();
        String str2 = "";
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                str2 = str2 + listFiles[i].getAbsolutePath() + "|";
            }
        }
        return str2;
    }

    //取文件修改时间
    public static String getFileChangeTime (String str) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(new File(str).lastModified()));
    }


    //打开文件
    private static Intent openFile(String str) {
        File file = new File(str);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        String lowerCase = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();
        if (lowerCase.equals("m4a") || lowerCase.equals("mp3") || lowerCase.equals("mid") || lowerCase.equals("xmf") || lowerCase.equals("ogg") || lowerCase.equals("wav")) {
            return getAudioFileIntent(str);
        }
        if (lowerCase.equals("3gp") || lowerCase.equals("mp4")) {
            return getVideoFileIntent(str);
        }
        if (lowerCase.equals("jpg") || lowerCase.equals("gif") || lowerCase.equals("png") || lowerCase.equals("jpeg") || lowerCase.equals("bmp")) {
            return getImageFileIntent(str);
        }
        if (lowerCase.equals("apk")) {
            return getApkFileIntent(str);
        }
        if (lowerCase.equals("ppt")) {
            return getPptFileIntent(str);
        }
        if (lowerCase.equals("xls")) {
            return getExcelFileIntent(str);
        }
        if (lowerCase.equals("doc")) {
            return getWordFileIntent(str);
        }
        if (lowerCase.equals("pdf")) {
            return getPdfFileIntent(str);
        }
        if (lowerCase.equals("chm")) {
            return getChmFileIntent(str);
        }
        if (lowerCase.equals("txt")) {
            return getTextFileIntent(str, false);
        }
        return getAllIntent(str);
    }

    private static Intent getAllIntent(String str) {
        Intent intent = new Intent();
        intent.addFlags(0x10000000);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(new File(str)), "*/*");
        return intent;
    }

    private static Intent getApkFileIntent(String str) {
        Intent intent = new Intent();
        intent.addFlags(0x10000000);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.android.package-archive");
        return intent;
    }

    private static Intent getVideoFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(0x04000000);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        intent.setDataAndType(Uri.fromFile(new File(str)), "video/*");
        return intent;
    }

    private static Intent getAudioFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(0x04000000);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        intent.setDataAndType(Uri.fromFile(new File(str)), "audio/*");
        return intent;
    }

    private static Intent getHtmlFileIntent(String str) {
        Uri build = Uri.parse(str).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(str).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(build, "text/html");
        return intent;
    }

    private static Intent getImageFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(0x10000000);
        intent.setDataAndType(Uri.fromFile(new File(str)), "image/*");
        return intent;
    }

    private static Intent getPptFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(0x10000000);
        intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.ms-powerpoint");
        return intent;
    }

    private static Intent getExcelFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(0x10000000);
        intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.ms-excel");
        return intent;
    }

    private static Intent getWordFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(0x10000000);
        intent.setDataAndType(Uri.fromFile(new File(str)), "application/msword");
        return intent;
    }

    private static Intent getChmFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(0x10000000);
        intent.setDataAndType(Uri.fromFile(new File(str)), "application/x-chm");
        return intent;
    }

    private static Intent getTextFileIntent(String str, boolean z) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(0x10000000);
        if (z) {
            intent.setDataAndType(Uri.parse(str), "text/plain");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(str)), "text/plain");
        }
        return intent;
    }

    private static Intent getPdfFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(0x10000000);
        intent.setDataAndType(Uri.fromFile(new File(str)), "application/pdf");
        return intent;
    }
}
