package com.xinchen.tool.httptrace.framework.common;

import static com.xinchen.tool.httptrace.framework.common.Constants.IP_PORT_SPLIT_CHAR;

/**
 *
 *
 * ex: XID -> ipAddress:port:transId -> 127.0.0.1:8080:-7651081582837237180
 *
 * @date 2021-07-14 11:01
 */
public class XID {
  private static int port;
  private static String ipAddress;

  /**
   * Sets port.
   *
   * @param port the port
   */
  public static void setPort(int port) {
    XID.port = port;
  }

  /**
   * Sets ip address.
   *
   * @param ipAddress the ip address
   */
  public static void setIpAddress(String ipAddress) {
    XID.ipAddress = ipAddress;
  }

  /**
   * Generate xid string.
   *
   * @param tranId the tran id
   * @return the string
   */
  public static String generateXID(long tranId) {
    return new StringBuilder().append(ipAddress).append(IP_PORT_SPLIT_CHAR).append(port).append(IP_PORT_SPLIT_CHAR).append(tranId).toString();
  }

  /**
   * Gets transaction id.
   *
   * @param xid the xid
   * @return the transaction id
   */
  public static long getTransactionId(String xid) {
    if (xid == null) {
      return -1;
    }

    int idx = xid.lastIndexOf(":");
    return Long.parseLong(xid.substring(idx + 1));
  }

  /**
   * Gets port.
   *
   * @return the port
   */
  public static int getPort() {
    return port;
  }

  /**
   * Gets ip address.
   *
   * @return the ip address
   */
  public static String getIpAddress() {
    return ipAddress;
  }

  /**
   * Gets ipAddress:port
   *
   * @return eg: 127.0.0.1:8091
   */
  public static String getIpAddressAndPort() {
    return new StringBuilder().append(ipAddress).append(IP_PORT_SPLIT_CHAR).append(port).toString();
  }
}
