package com.xinchen.tool.spi.logger.support;

import com.xinchen.tool.spi.Version;
import com.xinchen.tool.spi.logger.Logger;
import com.xinchen.tool.spi.utils.NetUtils;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/10/31 0:14
 */
public class FailsafeLogger implements Logger{
    private Logger logger;

    public FailsafeLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private String appendContextMessage(String msg) {
        return " [App] " + msg + ", app version: " + Version.getVersion() + ", current host: " + NetUtils.getLocalHost();
    }

    @Override
    public void trace(String msg, Throwable e) {
        try {
            logger.trace(appendContextMessage(msg), e);
        } catch (Throwable t) {
        }
    }

    @Override
    public void trace(Throwable e) {
        try {
            logger.trace(e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void trace(String msg) {
        try {
            logger.trace(appendContextMessage(msg));
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void debug(String msg, Throwable e) {
        try {
            logger.debug(appendContextMessage(msg), e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void debug(Throwable e) {
        try {
            logger.debug(e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void debug(String msg) {
        try {
            logger.debug(appendContextMessage(msg));
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void info(String msg, Throwable e) {
        try {
            logger.info(appendContextMessage(msg), e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void info(String msg) {
        try {
            logger.info(appendContextMessage(msg));
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void warn(String msg, Throwable e) {
        try {
            logger.warn(appendContextMessage(msg), e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void warn(String msg) {
        try {
            logger.warn(appendContextMessage(msg));
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void error(String msg, Throwable e) {
        try {
            logger.error(appendContextMessage(msg), e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void error(String msg) {
        try {
            logger.error(appendContextMessage(msg));
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void error(Throwable e) {
        try {
            logger.error(e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void info(Throwable e) {
        try {
            logger.info(e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void warn(Throwable e) {
        try {
            logger.warn(e);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public boolean isTraceEnabled() {
        try {
            return logger.isTraceEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isDebugEnabled() {
        try {
            return logger.isDebugEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isInfoEnabled() {
        try {
            return logger.isInfoEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isWarnEnabled() {
        try {
            return logger.isWarnEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isErrorEnabled() {
        try {
            return logger.isErrorEnabled();
        } catch (Throwable t) {
            return false;
        }
    }
}
