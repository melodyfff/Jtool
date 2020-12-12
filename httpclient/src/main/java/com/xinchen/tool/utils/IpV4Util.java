package com.xinchen.tool.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/12/12 18:28
 */
public class IpV4Util {
    public static int ipToInt(String ip) throws UnknownHostException {
        byte[] addr = ipToBytes(ip);
        //reference  java.net.Inet4Address.Inet4Address
        int address  = addr[3] & 0xFF;
        address |= ((addr[2] << 8) & 0xFF00);
        address |= ((addr[1] << 16) & 0xFF0000);
        address |= ((addr[0] << 24) & 0xFF000000);
        return address;
    }

    public static String intToIp(int ip) {
        byte[] addr = new byte[4];
        addr[0] = (byte) ((ip >>> 24) & 0xFF);
        addr[1] = (byte) ((ip >>> 16) & 0xFF);
        addr[2] = (byte) ((ip >>> 8) & 0xFF);
        addr[3] = (byte) (ip & 0xFF);
        return bytesToIp(addr);
    }

    public static String bytesToIp(byte[] src) {
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff)
                + "." + (src[3] & 0xff);
    }

    public static byte[] ipToBytes(String ip) throws UnknownHostException {
        return InetAddress.getByName(ip).getAddress();
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress.getByName("14.215.177.39").getAddress();
    }
}
