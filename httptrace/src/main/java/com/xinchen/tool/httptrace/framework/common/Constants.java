package com.xinchen.tool.httptrace.framework.common;

import java.nio.charset.Charset;

/**
 * @date 2021-07-13 15:26
 */
public interface Constants {
  /**
   * The constant IP_PORT_SPLIT_CHAR.
   */
  String IP_PORT_SPLIT_CHAR = ":";
  /**
   * The constant CLIENT_ID_SPLIT_CHAR.
   */
  String CLIENT_ID_SPLIT_CHAR = ":";
  /**
   * The constant ENDPOINT_BEGIN_CHAR.
   */
  String ENDPOINT_BEGIN_CHAR = "/";
  /**
   * The constant DBKEYS_SPLIT_CHAR.
   */
  String DBKEYS_SPLIT_CHAR = ",";

  /**
   * The constant ROW_LOCK_KEY_SPLIT_CHAR.
   */
  String ROW_LOCK_KEY_SPLIT_CHAR = ";";
  /**
   * default charset name
   */
  String DEFAULT_CHARSET_NAME = "UTF-8";
  /**
   * default charset is utf-8
   */
  Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);
}
