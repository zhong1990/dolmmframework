package org.dol.framework.web.model;

import org.dol.framework.util.ListUtil;
import org.dol.message.TreeNode;

import java.util.*;

/**
 * 菜单项对象.
 *
 * @param <T>
 * @author dolphin
 * @Create 2017年2月13日 下午6:12:40
 * @since 1.7
 */
public class WebTreeNode {

    private Integer id;
    private Integer parentId;
    private String name;
    private String title;
    private String icon;
    private boolean selected;
    private boolean checked;
    private String url;
    private boolean active;
    private WebTreeNode parent;
    private List<WebTreeNode> children;

    public static List<WebTreeNode> toTree(List<? extends TreeNode<Integer>> data, int i) {

        Map<Integer, WebTreeNode> id2NodeMap = new HashMap<>(data.size());
        for (TreeNode<Integer> treeNode : data) {
            WebTreeNode webTreeNode = buildNode(treeNode);
            id2NodeMap.put(webTreeNode.getId(), webTreeNode);
        }
        Set<Map.Entry<Integer, WebTreeNode>> entries = id2NodeMap.entrySet();
        List<WebTreeNode> rootNodes = new ArrayList<WebTreeNode>();

        for (Map.Entry<Integer, WebTreeNode> entry : entries) {
            if (entry.getValue().isRoot()) {
                rootNodes.add(entry.getValue());
            } else {
                WebTreeNode parent = id2NodeMap.get(entry.getValue().getParentId());
                if (parent != null) {
                    parent.addChild(entry.getValue());
                }
            }
        }
        return rootNodes;
    }

    private static WebTreeNode buildNode(TreeNode<Integer> treeNode) {
        WebTreeNode webTreeNode = new WebTreeNode();
        webTreeNode.setId(treeNode.getId());
        webTreeNode.setParentId(treeNode.getParentId());
        webTreeNode.setName(treeNode.getName());
        webTreeNode.setTitle(treeNode.getName());
        return webTreeNode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the parent
     */
    public WebTreeNode getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(WebTreeNode parent) {
        this.parent = parent;
    }

    public WebTreeNode firstChild() {
        if (hasChild()) {
            return (WebTreeNode) this.getChildren().get(0);
        }
        return null;
    }

    public boolean hasChild() {
        return ListUtil.isNotNullAndEmpty(getChildren());
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<WebTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<WebTreeNode> children) {
        this.children = children;
    }

    public void addChild(WebTreeNode child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        child.setParent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRoot() {
        return parentId == null || parentId < 1;
    }
}
