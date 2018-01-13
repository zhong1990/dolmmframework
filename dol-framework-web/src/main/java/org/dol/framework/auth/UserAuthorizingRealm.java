package org.dol.framework.auth;

import java.util.Iterator;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.dol.framework.util.ListUtil;
import org.dol.framework.util.StringUtil;

public abstract class UserAuthorizingRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {

        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection 不能为空");
        }
        UserPrincipal userPrincipal = (UserPrincipal) getAvailablePrincipal(principals);
        try {

            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            Set<String> permissions = loadPermissions(userPrincipal);
            if (ListUtil.isNotNullAndEmpty(permissions)) {
                removeEmptyString(permissions);
                info.setStringPermissions(permissions);
            }
            Set<String> roles = loadRoles(userPrincipal);
            if (ListUtil.isNotNullAndEmpty(roles)) {
                removeEmptyString(permissions);
                info.setRoles(roles);
            }
            return info;
        } catch (Exception e) {
            if (e instanceof AuthorizationException) {
                throw (AuthorizationException) e;
            } else {
                throw new AuthorizationException("根据用户获取权限和角色失败", e);
            }
        }
    }

    /**
     * 参照方法名.
     *
     * @param permissions
     */
    private void removeEmptyString(Set<String> permissions) {
        Iterator<String> iterator = permissions.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (StringUtil.isBlank(value)) {
                iterator.remove();
            }
        }
    }

    /**
     * 参照方法名.
     *
     * @return
     */
    protected abstract Set<String> loadRoles(UserPrincipal userPrincipal);

    /**
     * 参照方法名.
     *
     * @return
     */
    protected abstract Set<String> loadPermissions(UserPrincipal userPrincipal);

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {

        UserAuthenticationToken userIdUserGroupIdToten = (UserAuthenticationToken) token;
        return new SimpleAuthenticationInfo(userIdUserGroupIdToten.getPrincipal(), userIdUserGroupIdToten.getCredentials(), getName());

    }

}
