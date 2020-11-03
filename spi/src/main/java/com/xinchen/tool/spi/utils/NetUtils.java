package com.xinchen.tool.spi.utils;

import com.xinchen.tool.spi.constants.CommonConstants;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP and Port Helper for RPC
 */
public class NetUtils {

    public static String getLocalHost(){
        return CommonConstants.LOCALHOST_VALUE;
    }

    /**
     * @param hostName hostName
     * @return ip address or hostName if UnknownHostException
     */
    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }
}
