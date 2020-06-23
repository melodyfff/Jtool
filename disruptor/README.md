# Disruptor
wiki: https://github.com/LMAX-Exchange/disruptor/wiki/Getting-Started

理解`Disruptor`是什么最好和大家都知道的东西比较。在这种情景下`Disruptor`类似`JAVA BlockingQueue`

像队列一样，`Disruptor`的目的是在同一进程内的线程之间移动数据(e.g. messages or events)

主要区别为：

- Multicast events to consumers, with consumer dependency graph.
- Pre-allocate memory for events.
- Optionally lock-free.


## 拓展阅读
[高性能队列——Disruptor](https://tech.meituan.com/2016/11/18/disruptor.html)

[蚂蚁金服分布式链路跟踪组件 SOFATracer 中 Disruptor 实践（含源码）](https://developer.aliyun.com/article/761347)

[Disruptor的应用示例——大文件拆分](https://www.cnblogs.com/daoqidelv/p/7107474.html)