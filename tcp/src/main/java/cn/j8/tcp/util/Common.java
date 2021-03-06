package cn.j8.tcp.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

public class Common {
	
	private static Logger logger = LoggerFactory.getLogger(Common.class);

	/* 跟Long.parseLong一样，但是Long.parseLong在解析如 0xfffffffffffff123 这样的字符串时会抛出异常 */
	public static long parse_long(String x){
		String up = x.substring(0, 8);
		String down = x.substring(8);
		
		long a = Long.parseLong(up, 16) << 32;
		long b = Long.parseLong(down, 16);
		return a+b;
	}
	
	/* 获取 [min, max] 的随机数 */
	private static Random r = new Random(System.currentTimeMillis());
	public static int rand(int min, int max){
		return (min + r.nextInt(max-min+1));
	}
	
	public static String show_time(long mill){
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return f.format(new Date(mill));
	}
	
	public static String join_string(String[] ss, String split){
		if(ss == null || ss.length == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<ss.length; ++i){
			if(i != 0){
				sb.append(split);
			}
			sb.append(ss[i]);
		}
		return sb.toString();
	}
	
	public static long convert2Long(Object o){
		if(o == null)
			return 0;
		if(o instanceof Long) return ((Long)o).longValue();
		if(o instanceof Integer){
			int tmp = ((Integer)o).intValue();
			return (long)tmp;
		}
		
		String s = o.toString();
		boolean flag = false;
		StringBuffer m = new StringBuffer();
		for(int i=0; i<s.length(); ++i){
			char c = s.charAt(i);
			if(c >= '0' && c <= '9'){
				m.append(c);
				flag = true;
			}
			else{
				if(flag) break;
				else continue;
			}
		}
		return Long.parseLong(m.toString());
	}
	
	public static int convert2Int(Object o){
		long d = convert2Long(o);
		return (int)d;
	}
	
	public static void file_put_contents(String file, String content, boolean append){
		try{
			file_put_contents(file, content.getBytes("UTF-8"), append);
		}catch(Exception e){
			logger.error("error", e);
		}
	}
	public static boolean file_put_contents(String file, byte[] bs, boolean append){
		FileOutputStream out = null;
		try{
			out = new FileOutputStream(file, append);
			out.write(bs);
			out.flush();
			return true;
		}catch(Exception e){
			logger.error("error", e);
			return false;
		}finally{
			if(out != null)
				try{out.close();}catch(Exception e){logger.error("error", e);}
		}
	}
	
	public static byte[] file_get_contents(String file){
		File f = new File(file);
		if(!f.exists())
			return null;
		
		int len = (int)f.length();
		byte[] bs = new byte[len];
		
		FileInputStream in = null;
		try{
			in = new FileInputStream(f);
			int n = 0, off = 0;
			while((n = in.read(bs, off, len)) != -1 && len != 0){
				off += n;
				len -= n;
			}
			return bs;
		}catch(Exception e){
			logger.error("error", e);
			return null;
		}finally{
			if(in != null)
				try{in.close();}catch(Exception e){logger.error("error", e);}
		}
	}
	public static String file_get_contents(String file, String encode){
		byte[] bs = file_get_contents(file);
		if(bs == null)
			return null;
		try{
			return new String(bs, encode);
		}catch(Exception e){
			return null;
		}
	}
	
	public static String[] array_filter(String[] arr){
		if(arr == null) return null;
		if(arr.length == 0) return arr;
		
		List<String> _list = new ArrayList<String>();
		for(String s : arr){
			if(StringUtils.isBlank(s) || StringUtils.isWhitespace(s)){
				continue;
			}
			_list.add(s.trim());
		}
		return _list.toArray(new String[0]);
	}
	
	public static String now(String format){
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(now);
	}
	
	public static String now(){
		return now("yyyy-MM-dd HH:mm:ss");
	}
	
	public static String newString(byte[] bs, String enc){
		try{
			return new String(bs, enc);
		}catch(Exception e){
			return null;
		}
	}
	
	public static String url_encode(String s, String enc){
		try{
			return URLEncoder.encode(s, enc);
		}catch(Exception e){
			return null;
		}
	}
	
	public static String url_encode(String s){
		return url_encode(s, "UTF-8");
	}
	
	public static String url_decode(String s, String enc){
		try{
			return URLDecoder.decode(s, enc);
		}catch(Exception e){
			return null;
		}
	}
	
	public static String url_decode(String s){
		return url_decode(s, "UTF-8");
	}
	
	public static String create_imei(){
		String s = "";
		String x = "1234567890";
		for(int i=0; i<15; ++i){
			s += x.charAt(Common.rand(0, x.length()-1));
		}
		return s;
	}
	
	public static byte[] decompress(byte[] value) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
		Inflater decompressor = new Inflater();
		try {
			decompressor.setInput(value);
			final byte[] buf = new byte[1024];
			while (!decompressor.finished()) {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			}
		} finally {
			decompressor.end();
		}

		return bos.toByteArray();
	}

	public static byte[] compress(byte[] value, int offset, int length,
			int compressionLevel) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		Deflater compressor = new Deflater();
		try {
			compressor.setLevel(compressionLevel); // 将当前压缩级别设置为指定值。
			compressor.setInput(value, offset, length);
			compressor.finish(); // 调用时，指示压缩应当以输入缓冲区的当前内容结尾。

			// Compress the data
			final byte[] buf = new byte[1024];
			while (!compressor.finished()) {
				// 如果已到达压缩数据输出流的结尾，则返回 true。
				int count = compressor.deflate(buf);
				// 使用压缩数据填充指定缓冲区。
				bos.write(buf, 0, count);
			}
		} finally {
			compressor.end(); // 关闭解压缩器并放弃所有未处理的输入。
		}

		return bos.toByteArray();
	}

	public static byte[] compress(byte[] value, int offset, int length) {
		// 最佳压缩的压缩级别
		return compress(value, offset, length, Deflater.BEST_COMPRESSION);
	}

	public static byte[] compress(byte[] value) {
		return compress(value, 0, value.length, Deflater.BEST_COMPRESSION);
	}
	
	public static byte[] md5(byte[] bs){
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		md5.update(bs);
		return md5.digest();
	}
	
	public static String md5(String s){
		try{
			byte[] bs = s.getBytes("UTF-8");
			byte[] rtn = md5(bs);
			return ByteHelper.bytesToHexString(rtn);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] gzencode(byte[] bs){
		if(bs == null || bs.length == 0)
			return bs;
		
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
		if(bs == null || bs.length == 0)
			return bs;
		
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
	
    public static byte[] readStream(InputStream is) {
        ByteArrayOutputStream bos = null;
        try {
        	int BUF_LEN = 4*1024*1024; //4M的读
            bos = new ByteArrayOutputStream();
            byte[] buff = new byte[BUF_LEN];
            int count = -1;
            while ((count = is.read(buff, 0, BUF_LEN)) > 0) {
                bos.write(buff, 0, count);
            }
            bos.flush();
            return bos.toByteArray();
        } catch (Exception ignored) {
            //do nothing
        } finally {
            try {if(bos != null) bos.close();} catch(Exception ignored){}
        }
        
        return null;
    }
    

	
	private static boolean sync_write(SocketChannel channel, ByteBuffer buffer){
		if(channel == null || buffer == null)
			return false;
		
		Selector selector = null;
		try{
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_WRITE);
			
			while(selector.select() > 0){
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				if(it.hasNext()){
					SelectionKey readyKey = it.next();
					it.remove();
					
					int size = 0;
					while(buffer.remaining() > 0 && (size = channel.write(buffer)) > 0);
					
					//读满了数据了，这时候就可以不再等待了
					if(buffer.remaining() == 0)
						break;
				}
			}
	
			return buffer.remaining()==0;
		}catch(Exception e){
			logger.error("sync_write exception", e);
			return false;
		}finally{
			try{
				if(selector != null)
					selector.close();
			}catch(Exception e){logger.error("sync_write selector exception", e);}
		}
	}
	
	public static boolean write_channel(SocketChannel channel, byte[] bs){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] len_bytes = ByteHelper.int2ByteArray(bs.length);
			baos.write(len_bytes);
			baos.write(bs);
			bs = baos.toByteArray();
			
			ByteBuffer buf = ByteBuffer.wrap(new byte[bs.length]);
			buf.put(bs);
			buf.flip();
			
			//这里写也有可能返回size=0
			if(!sync_write(channel, buf))
				return false;
			
			return true;
		}catch(Exception e){
			logger.error("write_channel exception", e);
			return false;
		}
	}
	
	//同步读，一直读到len个字节为止
	private static boolean sync_read(SocketChannel channel, ByteBuffer buffer){
		if(channel == null || buffer == null)
			return false;
		
		Selector selector = null;
		try{
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
			
			while(selector.select() > 0){
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				if(it.hasNext()){
					SelectionKey readyKey = it.next();
					it.remove();
					
					int size = 0;
					while(buffer.remaining() > 0 && (size = channel.read(buffer)) > 0);
					
					//读满了数据了，这时候就可以不再等待了
					if(buffer.remaining() == 0)
						break;
	
					//遇到客户端关socket
					if(size == -1){
						return false;
					}
				}
			}
			return true;
		}catch(java.nio.channels.ClosedChannelException e){
        	//这种客户端自己断开连接导致读错误的情况就忽略吧
        	logger.info(e.getMessage());
        	return false;
        }catch(java.io.IOException e){
        	//这种客户端自己断开连接导致读错误的情况就忽略吧
        	logger.info(e.getMessage());
        	return false;
        }catch(Exception e){
			logger.error("sync_read exception", e);
			return false;
		}finally{
			if(selector != null){
				try{
					selector.close();
				}catch(Exception e){
					logger.error("sync_read selector exception", e);}
			}
		}
	}
	
	public static byte[] read_channel(SocketChannel channel, int len){
		ByteBuffer buffer = ByteBuffer.wrap(new byte[len]);
        //一直把buffer读满为止
    	if(!sync_read(channel, buffer))
    		return null;
    	
        //把数据倒出来
        buffer.flip();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        
        return bytes;
	}
	
	public static byte[] read_channel(SocketChannel channel){
		//头4个字节代表长度
		byte[] len_bytes = read_channel(channel, 4);
		if(len_bytes == null)
			return null;
		
		int len = ByteHelper.bytesToInt(len_bytes);
		if(len <= 0)
			return null;
		
		return read_channel(channel, len);
	}
}
