package com.xinchen.security.filter;

import java.util.logging.Logger;

/**
 *
 * Make it easily to inject the default security headers to assist in protecting the application.
 *
 * The default for is to include the following headers:
 *
 * <pre>
 *     Cache-Control: no-cache, no-store, max-age=0, must-revalidate
 *     Pragma: no-cache
 *     Expires: 0
 *     X-Content-Type-Options: nosniff
 *     Strict-Transport-Security: max-age=15768000 ; includeSubDomains
 *     X-Frame-Options: DENY
 *     X-XSS-Protection: 1; mode=block
 * </pre>
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/3/25 23:28
 */
public class ResponseHeadersEnforcementFilter  {

    private static final Logger LOGGER = Logger.getLogger(ResponseHeadersEnforcementFilter.class.getName());

    private static final String INIT_PARAM_ENABLE_CACHE_CONTROL = "enableCacheControl";
    private static final String INIT_PARAM_ENABLE_XCONTENT_OPTIONS = "enableXContentTypeOptions";
    private static final String INIT_PARAM_ENABLE_STRICT_TRANSPORT_SECURITY = "enableStrictTransportSecurity";

    private static final String INIT_PARAM_ENABLE_STRICT_XFRAME_OPTIONS = "enableXFrameOptions";
    private static final String INIT_PARAM_STRICT_XFRAME_OPTIONS = "XFrameOptions";

    private static final String INIT_PARAM_ENABLE_XSS_PROTECTION = "enableXSSProtection";
    private static final String INIT_PARAM_XSS_PROTECTION = "XSSProtection";

    private static final String INIT_PARAM_CONTENT_SECURITY_POLICY = "contentSecurityPolicy";


    private boolean enableCacheControl;
    private String cacheControlHeader = "no-cache, no-store, max-age=0, must-revalidate";


    private boolean enableXContentTypeOptions;
    private String xContentTypeOptionsHeader = "nosniff";


    /** allow for 6 months; value is in seconds */
    private boolean enableStrictTransportSecurity;
    private String strictTransportSecurityHeader = "max-age=15768000 ; includeSubDomains";


    private boolean enableXFrameOptions;
    private String XFrameOptions = "DENY";


    private boolean enableXSSProtection;
    private String XSSProtection = "1; mode=block";

    private String contentSecurityPolicy;


    public void setXFrameOptions(final String XFrameOptions) {
        this.XFrameOptions = XFrameOptions;
    }

    public void setXSSProtection(final String XSSProtection) {
        this.XSSProtection = XSSProtection;
    }

    public void setContentSecurityPolicy(final String contentSecurityPolicy) {
        this.contentSecurityPolicy = contentSecurityPolicy;
    }

    public void setEnableCacheControl(final boolean enableCacheControl) {
        this.enableCacheControl = enableCacheControl;
    }

    public void setEnableXContentTypeOptions(final boolean enableXContentTypeOptions) {
        this.enableXContentTypeOptions = enableXContentTypeOptions;
    }

    public void setEnableStrictTransportSecurity(final boolean enableStrictTransportSecurity) {
        this.enableStrictTransportSecurity = enableStrictTransportSecurity;
    }

    public void setEnableXFrameOptions(final boolean enableXFrameOptions) {
        this.enableXFrameOptions = enableXFrameOptions;
    }

    public void setEnableXSSProtection(final boolean enableXSSProtection) {
        this.enableXSSProtection = enableXSSProtection;
    }
}
