/**
 *
 */
package org.dol.message;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO.
 *
 * @param <K>
 * @author dolphin
 * @date 2017年5月1日 上午8:46:32
 */
public class TreeNode<K> implements Treeable<K> {

    private K id;
    private String name;

    private K parentId;

    private List<TreeNode<K>> children;

    /*
     * (non-Javadoc)
     * @see org.dol.message.Treeable#addChild(org.dol.message.Treeable)
     */
    @Override
    public void addChild(Treeable<K> child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add((TreeNode<K>) child);
    }

    /**
     * @return the children
     */
    @Override
    public List<TreeNode<K>> getChildren() {
        return children;
    }

    /**
     * TreeNode<K>
     *
     * @param children the children to set
     */
    public void setChildren(List<TreeNode<K>> children) {
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

    public boolean hasChild() {
        return getChildren() != null && !getChildren().isEmpty();
    }
}
