package com.xinchen.tool.spi.convert.multiple;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * The class to convert {@link String} to {@link TransferQueue}-based value
 *
 */
public class StringToTransferQueueConverter extends StringToIterableConverter<TransferQueue> {

    @Override
    protected TransferQueue createMultiValue(int size, Class<?> multiValueType) {
        return new LinkedTransferQueue();
    }
}
