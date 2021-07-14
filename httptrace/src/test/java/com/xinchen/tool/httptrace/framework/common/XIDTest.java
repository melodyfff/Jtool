package com.xinchen.tool.httptrace.framework.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @date 2021-07-14 11:02
 */
class XIDTest {
  /**
   * Test set ip address.
   */
  @Test
  public void testSetIpAddress() {
    XID.setIpAddress("127.0.0.1");
    assertThat(XID.getIpAddress()).isEqualTo("127.0.0.1");
  }

  /**
   * Test set port.
   */
  @Test
  public void testSetPort() {
    XID.setPort(8080);
    assertThat(XID.getPort()).isEqualTo(8080);
  }

  /**
   * Test generate xid.
   */
  @Test
  public void testGenerateXID() {
    long tranId = new Random().nextLong();
    XID.setPort(8080);
    XID.setIpAddress("127.0.0.1");
    assertThat(XID.generateXID(tranId)).isEqualTo(XID.getIpAddress() + ":" + XID.getPort() + ":" + tranId);
  }

  /**
   * Test get transaction id.
   */
  @Test
  public void testGetTransactionId() {
    assertThat(XID.getTransactionId(null)).isEqualTo(-1);
    assertThat(XID.getTransactionId("127.0.0.1:8080:8577662204289747564")).isEqualTo(8577662204289747564L);
  }

  /**
   * Test get ipAddress:port
   */
  @Test
  public void testGetIpAddressAndPort() {
    XID.setPort(8080);
    XID.setIpAddress("127.0.0.1");
    Assertions.assertEquals("127.0.0.1:8080",XID.getIpAddressAndPort());
  }
}