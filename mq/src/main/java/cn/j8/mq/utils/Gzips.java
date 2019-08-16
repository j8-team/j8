package cn.j8.mq.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Gzips {
    public static byte[] gzencode(byte[] bs){
        try{
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            GZIPOutputStream out = new GZIPOutputStream(bout);
            out.write(bs);
            out.finish();
            byte[] rtn = bout.toByteArray();

            out.close();
            return rtn;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] gzdecode(byte[] bs){
        try {
            GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(bs));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            byte[] rtn = out.toByteArray();

            in.close();
            out.close();
            return rtn;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
