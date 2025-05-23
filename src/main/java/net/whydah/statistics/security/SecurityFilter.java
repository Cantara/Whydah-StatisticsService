package net.whydah.statistics.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.whydah.sso.commands.appauth.CommandGetApplicationIdFromApplicationTokenId;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.commands.userauth.CommandValidateUserTokenId;
import org.constretto.annotation.Configuration;
import org.constretto.annotation.Configure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Pattern;

/**
 * SecurityFilter is reponsible for verifying applicationTokenId and userTokenId against STS.
 * Verifying application and user roles is the responsibility of UIB.
 *
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-11-22
 */
@Component
public class SecurityFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    private final String stsUri;
    private String stsAppId;
    URI tokenServiceUri;

    @Autowired
    @Configure
    public SecurityFilter(@Configuration("securitytokenservice") String stsUri, @Configuration("securitytokenservice.appid") String stsAppId) {
        this.stsUri = stsUri;
        this.stsAppId = stsAppId;
        if (this.stsAppId == null || this.stsAppId.equals("")) {
            this.stsAppId = "2211";
        }
        tokenServiceUri = URI.create(stsUri);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        Integer statusCode = null; //FIXME BLI original code: authenticateAndAuthorizeRequest(servletRequest.getPathInfo());
        if (statusCode == null) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(statusCode);
        }
    }


    /**
     * @param pathInfo the path to apply the filter to
     * @return HttpServletResponse.SC_UNAUTHORIZED if authentication fails, otherwise null
     */
    Integer authenticateAndAuthorizeRequest(String pathInfo) {
        log.debug("filter path {}", pathInfo);

        //match /
        if (pathInfo == null || pathInfo.equals("/")) {
            return HttpServletResponse.SC_NOT_FOUND;
        }


        String applicationTokenId = findPathElement(pathInfo, 1).substring(1);
        //" we should probably avoid askin sts if we know it is sts asking, but we should ask sts for a valid applicationsession for all other applications"
        String appId = new CommandGetApplicationIdFromApplicationTokenId(URI.create(stsUri), applicationTokenId).execute();
        if (appId == null) {
            return HttpServletResponse.SC_UNAUTHORIZED;
        } else if (!appId.equals(stsAppId)) {
            Boolean applicationTokenIsValid = new CommandValidateApplicationTokenId(stsUri, applicationTokenId).execute();
            if (!applicationTokenIsValid) {
                return HttpServletResponse.SC_UNAUTHORIZED;
            }
        } else {
            return null; //do not check further if STS is requesting
        }


        //paths without userTokenId
        String path = pathInfo.substring(1); //strip leading /
        //strip applicationTokenId from pathInfo
        path = path.substring(path.indexOf("/"));
        /*
        /{applicationTokenId}/auth/password/reset/{usernaem}     //PasswordResource2
        /{applicationTokenId}/user/{uid}/reset_password     //PasswordResource2
        /{applicationTokenId}/user/{uid}/change_password    //PasswordResource2
        /{applicationTokenId}/authenticate/user/*           //UserAuthenticationEndpoint
        /{applicationTokenId}/signup/user                   //UserSignupEndpoint
        */
        String applicationAuthPattern = "/application/auth";
        String userLogonPattern = "/auth/logon/user";           //LogonController, same as authenticate/user in UIB.
        String userAuthPattern = "/authenticate/user(|/.*)";    //This is the pattern used in UIB
        String pwResetAuthPattern = "/auth/password/reset/username";
        String pwPattern = "/user/.+/(reset|change)_password";
        String userSignupPattern = "/signup/user";
        String listApplicationsPattern = "/applications";
        String[] patternsWithoutUserTokenId = {applicationAuthPattern, userLogonPattern, pwPattern, userAuthPattern, userSignupPattern, listApplicationsPattern};
        for (String pattern : patternsWithoutUserTokenId) {
            if (Pattern.compile(pattern).matcher(path).matches()) {
                log.debug("{} was matched to {}. SecurityFilter passed.", path, pattern);
                return null;
            }
        }


        //paths WITH userTokenId verification
        /*
        /{applicationtokenid}/{userTokenId}/application     //ApplicationResource
        /{applicationtokenid}/{userTokenId}/applications    //ApplicationsResource
        /{applicationtokenid}/{userTokenId}/user            //UserResource
        /{applicationtokenid}/{usertokenid}/useraggregate   //UserAggregateResource
        /{applicationtokenid}/{usertokenid}/users           //UsersResource
         */
        String usertokenId = findPathElement(pathInfo, 2).substring(1);

        Boolean userTokenIsValid = new CommandValidateUserTokenId(tokenServiceUri, applicationTokenId, usertokenId).execute();
        if (!userTokenIsValid) {
            return HttpServletResponse.SC_UNAUTHORIZED;
        }
        return null;
    }

    private String findPathElement(String pathInfo, int elementNumber) {
        String pathElement = null;
        if (pathInfo != null) {
            String[] pathElements = pathInfo.split("/");
            if (pathElements.length > elementNumber) {
                pathElement = "/" + pathElements[elementNumber];
            }
        }
        return pathElement;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
