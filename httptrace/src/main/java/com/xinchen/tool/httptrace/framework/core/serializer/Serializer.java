package com.xinchen.tool.httptrace.framework.core.serializer;

/**
 * @date 2021-07-14 11:36
 */
public interface Serializer {
  /**
   * Encode object to byte[].
   *
   * @param <T> the type parameter
   * @param t   the t
   * @return the byte [ ]
   */
  <T> byte[] serialize(T t);

  /**
   * Decode t from byte[].
   *
   * @param <T>   the type parameter
   * @param bytes the bytes
   * @return the t
   */
  <T> T deserialize(byte[] bytes);
}
