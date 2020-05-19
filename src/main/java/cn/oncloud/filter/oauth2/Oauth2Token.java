package cn.oncloud.filter.oauth2;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author YYY
 * 功能描述：有关于token
 */
public class Oauth2Token implements AuthenticationToken {

    private String token;

    public Oauth2Token(String token){
        this.token = token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
