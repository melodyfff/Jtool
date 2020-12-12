package com.xinchen.tool.okhttp;

import okhttp3.Dns;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 设置OkHttp的DNS域名解析: https://zybuluo.com/act262/note/798277
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/12/12 17:44
 */
public class DnsFactory {
    public static Dns create(){
        return new DnsResolve();
    }

    static class DnsResolve implements Dns{
        private final static Logger log = LoggerFactory.getLogger(DnsResolve.class);
        @NotNull
        @Override
        public List<InetAddress> lookup(@NotNull String host) throws UnknownHostException {
//            final List<InetAddress> lookup = SYSTEM.lookup(host);
            System.out.println("-----Look UP.");
            final List<InetAddress> lookup = new ArrayList<>();
            // 自定义一个错误的baidu ip地址，
            InetAddress byAddress = InetAddress.getByAddress(host, InetAddress.getByName("192.168.12.33").getAddress());
            InetAddress byAddress2 = InetAddress.getByAddress(host, InetAddress.getByName("14.215.177.38").getAddress());
            lookup.add(byAddress);
            lookup.add(byAddress2);
            return lookup;
//            return SYSTEM.lookup(host);
        }
    }
}
