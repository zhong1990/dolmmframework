/**
 * dol-rbac-auth
 * Security.java
 * org.dol.wms.security.shiro
 * TODO
 *
 * @author dolphin
 * @date 2016年5月11日 下午2:42:33
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.auth;

import java.util.Collection;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.dol.framework.util.ListUtil;

/**
 * ClassName:Security <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年5月11日 下午2:42:33 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class SecurityUtil {

    private static final String DELIMETER = "\\,";

    public static void login(final Long userId, final List<Integer> groupIdList) {
        AuthenticationToken token = new UserAuthenticationToken(userId, groupIdList);
        SecurityUtils.getSubject().login(token);
    }

    public static void logout() {
        SecurityUtils.getSubject().logout();
    }

    /**
     * 验证是否为已认证通过的用户，不包含已记住的用户，这是与 isUser 标签方法的区别所在。
     *
     * @return 用户是否已通过认证
     */
    public static boolean isAuthenticated() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isAuthenticated();
    }

    public static Object getSessionValue(Object key) {
        Subject subject = SecurityUtils.getSubject();
        return subject.getSession().getAttribute(key);
    }

    public static void setSessionValue(Object key, Object value) {
        Subject subject = SecurityUtils.getSubject();
        subject.getSession(true).setAttribute(key, value);
    }

    /**
     * 验证是否为未认证通过用户，与 isAuthenticated 标签相对应，与 isGuest 标签的区别是，该标签包含已记住用户。
     *
     * @return 用户是否未通过认证
     */
    public static boolean isNotAuthenticated() {
        Subject subject = SecurityUtils.getSubject();
        return subject == null || subject.isAuthenticated() == false;
    }

    /**
     * 验证用户是否为 <访客>，即未认证（包含未记住）的用户。
     *
     * @return 用户是否为 <访客>
     */
    public static boolean isGuest() {
        Subject subject = SecurityUtils.getSubject();
        return subject == null || subject.getPrincipal() == null;
    }

    /**
     * 验证用户是否认证通过或已记住的用户。
     *
     * @return 用户是否认证通过或已记住的用户
     */
    public static boolean isUser() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.getPrincipal() != null;
    }

    /**
     * 获取登录用户的编号
     *
     * @return Long
     * @author dolphin
     * @since JDK 1.7
     * @date 2016年5月12日 上午11:12:41
     */
    public static Long getUserId() {
        UserPrincipal userPrincipal = getUserPrincipal();
        return userPrincipal == null ? null : userPrincipal.getUserId();
    }

    /**
     * 获取登录用户所属的组编号列表
     *
     * @return List<Integer>
     * @author dolphin
     * @since JDK 1.7
     * @date 2016年5月12日 上午11:13:02
     */
    public static List<Integer> getUserGroupIdList() {
        UserPrincipal userPrincipal = getUserPrincipal();
        return userPrincipal == null ? null : userPrincipal.getUserGroupIdList();
    }

    /**
     * 参照方法名.
     *
     * @return
     */
    public static UserPrincipal getUserPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null || subject.getPrincipal() == null || !(subject.getPrincipal() instanceof UserPrincipal)) {
            return null;
        }
        return (UserPrincipal) subject.getPrincipal();
    }

    /**
     * 验证用户是否具备某角色。
     *
     * @param role
     *            角色名称
     * @return 用户是否具备某角色
     */
    public static boolean hasRole(String role) {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.hasRole(role);
    }

    /**
     * 验证用户是否不具备某角色，与 hasRole 逻辑相反。
     *
     * @param role
     *            角色名称
     * @return 用户是否不具备某角色
     */
    public static boolean lacksRole(String role) {
        return !hasRole(role);
    }

    /**
     * 验证用户是否具有以下任意一个角色。
     *
     * @param roleNames
     *            以 delimeter 为分隔符的角色列表
     * @param delimeter
     *            角色列表分隔符
     * @return 用户是否具有以下任意一个角色
     */
    public static boolean hasAnyRoles(String roleNames, String delimeter) {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            if (delimeter == null || delimeter.length() == 0) {
                delimeter = DELIMETER;
            }

            for (String role : roleNames.split(delimeter)) {
                if (subject.hasRole(role.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 验证用户是否具有以下任意一个角色。
     *
     * @param roleNames
     *            以 DELIMETER 为分隔符的角色列表
     * @return 用户是否具有以下任意一个角色
     */
    public static boolean hasAnyRoles(String roleNames) {
        return hasAnyRoles(roleNames, DELIMETER);
    }

    /**
     * 验证用户是否具有以下任意一个角色。
     *
     * @param roleNames
     *            角色列表
     * @return 用户是否具有以下任意一个角色
     */
    public static boolean hasAnyRoles(Collection<String> roleNames) {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && roleNames != null) {
            for (String role : roleNames) {
                if (role != null && subject.hasRole(role.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 验证用户是否具有以下任意一个角色。
     *
     * @param roleNames
     *            角色列表
     * @return 用户是否具有以下任意一个角色
     */
    public static boolean hasAnyRoles(String... roleNames) {
        Subject subject = SecurityUtils.getSubject();

        if (subject != null && roleNames != null) {
            for (int i = 0; i < roleNames.length; i++) {
                String role = roleNames[i];
                if (role != null && subject.hasRole(role.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 验证用户是否具备某权限。
     *
     * @param permission
     *            权限名称
     * @return 用户是否具备某权限
     */
    public static boolean hasPermission(String permission) {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isPermitted(permission);
    }

    /**
     * 验证用户是否不具备某权限，与 hasPermission 逻辑相反。
     *
     * @param permission
     *            权限名称
     * @return 用户是否不具备某权限
     */
    public static boolean lacksPermission(String permission) {
        return !hasPermission(permission);
    }

    /**
     * 验证用户是否具有以下任意一个权限。
     *
     * @param permissions
     *            以 delimeter 为分隔符的权限列表
     * @param delimeter
     *            权限列表分隔符
     * @return 用户是否具有以下任意一个权限
     */
    public static boolean hasAnyPermissions(String permissions, String delimeter) {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            List<String> permissionList = ListUtil.fromString(permissions, (delimeter == null ? DELIMETER : delimeter));
            for (String permission : permissionList) {
                if (subject.isPermitted(permission.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 验证用户是否具有以下任意一个权限。
     *
     * @param permissions
     *            以 PERMISSION_NAMES_DELIMETER 为分隔符的权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public static boolean hasAnyPermissions(String permissions) {
        return hasAnyPermissions(permissions, DELIMETER);
    }

    /**
     * 验证用户是否具有以下任意一个权限。
     *
     * @param permissions
     *            权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public static boolean hasAnyPermissions(Collection<String> permissions) {
        Subject subject = SecurityUtils.getSubject();

        if (subject != null && permissions != null) {
            for (String permission : permissions) {
                if (permission != null && subject.isPermitted(permission.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 验证用户是否具有以下任意一个权限。
     *
     * @param permissions
     *            权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public static boolean hasAnyPermissions(String... permissions) {
        Subject subject = SecurityUtils.getSubject();

        if (subject != null && permissions != null) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (permission != null && subject.isPermitted(permission.trim())) {
                    return true;
                }
            }
        }

        return false;
    }
}
