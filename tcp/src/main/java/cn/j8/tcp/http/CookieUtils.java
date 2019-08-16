package cn.j8.tcp.http;

import cn.j8.tcp.util.Common;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpCookie;

class CookieUtils {
	public static String cookie2string(HttpCookie cookie){
		StringBuffer sb = new StringBuffer();
		sb.append(cookie.getName()).append("=").append(Common.url_encode(cookie.getValue()));
		if(StringUtils.isNotBlank(cookie.getPath())){
			sb.append("; Path=").append(cookie.getPath());
		}
		if(StringUtils.isNotBlank(cookie.getDomain())){
			sb.append("; Domain=").append(cookie.getDomain());
		}
		if(cookie.getMaxAge() >= 0){
			sb.append("; Max-Age=").append(cookie.getMaxAge());
		}
		if(cookie.getSecure()){
			sb.append("; Secure");
		}
		if(cookie.isHttpOnly()){
			sb.append("; HttpOnly");
		}
		if(cookie.getDiscard()){
			sb.append("; Discard");
		}
		return sb.toString();
	}
}
