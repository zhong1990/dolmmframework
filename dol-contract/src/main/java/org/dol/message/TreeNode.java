/**
 *
 */
package org.dol.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO.
 *
 * @param <K>
 * @author dolphin
 * @date 2017年5月1日 上午8:46:32
 */
public class TreeNode<K> implements Treeable<K>, Serializable {

    private static final long serialVersionUID = 1L;

    private K id;
    private String name;

    private K parentId;

    private List<Treeable<K>> children;

    /*
     * (non-Javadoc)
     * @see org.dol.message.Treeable#addChildren(org.dol.message.Treeable)
     */
    @Override
    public void addChildren(Treeable<K> child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);

    }

    /**
     * @return the children
     */
    @Override
    public List<Treeable<K>> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<Treeable<K>> children) {
        this.children = children;
    }

    /**
     * @return the id
     */
    @Override
    public K getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(K id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the parentId
     */
    @Override
    public K getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(K parentId) {
        this.parentId = parentId;
    }

}
