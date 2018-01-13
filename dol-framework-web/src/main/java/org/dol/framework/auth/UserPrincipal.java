/**
 * dol-rbac-auth
 * UserPrincipal.java
 * org.dol.wms.security.shiro
 * TODO
 *
 * @author dolphin
 * @date 2016年5月11日 下午2:27:43
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.auth;

import java.security.Principal;
import java.util.List;

/**
 * ClassName:UserPrincipal <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年5月11日 下午2:27:43 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class UserPrincipal implements Principal {
    private Long userId;
    private List<Integer> userGroupIdList;

    public UserPrincipal(Long userId2, List<Integer> groupIdList) {
        this.setUserId(userId2);
        this.setUserGroupIdList(userGroupIdList);
    }

    /**
     * userId.
     *
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * userId.
     *
     * @param userId
     *            the userId to set
     */
    private void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * userGroupIdList.
     *
     * @return the userGroupIdList
     */
    public List<Integer> getUserGroupIdList() {
        return userGroupIdList;
    }

    /**
     * userGroupIdList.
     *
     * @param userGroupIdList
     *            the userGroupIdList to set
     */
    private void setUserGroupIdList(List<Integer> userGroupIdList) {
        this.userGroupIdList = userGroupIdList;
    }

    @Override
    public String getName() {

        return userId.toString();
    }
}
