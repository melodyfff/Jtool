package com.xinchen.tool.httptrace.framework.common.rpc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @date 2021-07-14 08:33
 */
public class RpcStatus {
  private static final ConcurrentMap<String, RpcStatus> SERVICE_STATUS_MAP = new ConcurrentHashMap<>();
  private final AtomicLong active = new AtomicLong();
  private final LongAdder total = new LongAdder();

  private RpcStatus() {
  }

  /**
   * get the RpcStatus of this service
   *
   * @param service the service
   * @return RpcStatus
   */
  public static RpcStatus getStatus(String service) {
    return SERVICE_STATUS_MAP.computeIfAbsent(service, key -> new RpcStatus());
  }

  /**
   * remove the RpcStatus of this service
   *
   * @param service the service
   */
  public static void removeStatus(String service) {
    SERVICE_STATUS_MAP.remove(service);
  }

  /**
   * begin count
   *
   * @param service the service
   */
  public static void beginCount(String service) {
    getStatus(service).active.incrementAndGet();
  }

  /**
   * end count
   *
   * @param service the service
   */
  public static void endCount(String service) {
    RpcStatus rpcStatus = getStatus(service);
    rpcStatus.active.decrementAndGet();
    rpcStatus.total.increment();
  }

  /**
   * get active.
   *
   * @return active
   */
  public long getActive() {
    return active.get();
  }

  /**
   * get total.
   *
   * @return total
   */
  public long getTotal() {
    return total.longValue();
  }
}
