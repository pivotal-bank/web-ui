package io.pivotal.web.config;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthorityAssigningOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");
        OidcUserInfo userInfo = null;
        OidcUser user;

        Set<GrantedAuthority> authorities = mapAuthorities(userRequest.getAccessToken().getScopes());
        if (CollectionUtils.isEmpty(authorities)) {
            authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_PIVOTAL_BANK"));
        }
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            user = new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        } else {
            user = new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
        }

        return user;
    }

    private Set<GrantedAuthority> mapAuthorities(Set<String> scopes) {
        return scopes.stream().filter(scope -> !scope.equals("openid"))
                .map(scope -> new SimpleGrantedAuthority("ROLE_" + scope.toUpperCase().replaceAll("\\.", "_")))
                .collect(Collectors.toSet());
    }
}
