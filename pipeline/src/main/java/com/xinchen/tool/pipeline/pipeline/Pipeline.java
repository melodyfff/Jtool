package com.xinchen.tool.pipeline.pipeline;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/2/1 11:49
 */
public interface Pipeline {
    /** 接收 */
    Pipeline fireTaskReceived();
    /** 过滤 */
    Pipeline fireTaskFiltered();
    /** 执行 */
    Pipeline fireTaskExecuted();
    /** 结束 */
    Pipeline fireAfterCompletion();

    Pipeline addLast(Handler handler);
}
