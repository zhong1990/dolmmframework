/**
 * dol-rbac-auth
 * UserIdUserGroupIdToten.java
 * org.dol.wms.security.shiro
 * TODO
 *
 * @author dolphin
 * @date 2016年5月11日 下午2:20:33
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.auth;

import java.util.List;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * ClassName:UserIdUserGroupIdToten <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年5月11日 下午2:20:33 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class UserAuthenticationToken extends UsernamePasswordToken {
    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

    private UserPrincipal userPrincipal;

    public UserAuthenticationToken(
            String userName,
            String password,
            Boolean rememberMe,
            Long userId,
            List<Integer> groupIdList) {
        super(userName, password, rememberMe);
        userPrincipal = new UserPrincipal(userId, groupIdList);
    }

    public UserAuthenticationToken(
            Long userId,
            List<Integer> groupIdList) {
        this(String.valueOf(userId), String.valueOf(userId), false, userId, groupIdList);
    }

    @Override
    public Object getPrincipal() {
        return userPrincipal;
    }

}
