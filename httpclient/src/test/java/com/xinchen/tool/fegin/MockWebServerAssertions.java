package com.xinchen.tool.fegin;

import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;

/**
 * @author xinchen
 * @version 1.0
 * @date 26/08/2020 15:38
 */
public class MockWebServerAssertions extends Assertions {

    public static RecordedRequestAssert assertThat(RecordedRequest actual) {
        return new RecordedRequestAssert(actual);
    }
}
