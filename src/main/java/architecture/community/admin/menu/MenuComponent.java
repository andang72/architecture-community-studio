package architecture.community.admin.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * This class extends {@link MenuBase} and basically contains helper methods
 * for adding and fetching children and parents.
 */
public class MenuComponent extends MenuBase implements Serializable {
	
	
    //~ Static fields/initializers =============================================
    protected static MenuComponent[] _menuComponent = new MenuComponent[0];

    //~ Instance fields ========================================================

    protected List<MenuComponent> menuComponents = Collections.synchronizedList(new ArrayList<MenuComponent>());
    protected MenuComponent parentMenu;
    private boolean last;
    private String breadCrumb;

    //~ Methods ================================================================

    public void addMenuComponent(MenuComponent menuComponent) {
        if ((menuComponent.getName() == null) || (menuComponent.getName().equals(""))) {
            menuComponent.setName(this.name + menuComponents.size());
        }
        
        if (!menuComponents.contains(menuComponent)) {
            menuComponents.add(menuComponent);
            menuComponent.setParent(this);
        }
    }

    public MenuComponent[] getMenuComponents() {
        return (MenuComponent[]) menuComponents.toArray(_menuComponent);
    }

    public void setMenuComponents(MenuComponent[] menuComponents) {
        for (int i = 0; i < menuComponents.length; i++) {
            MenuComponent component = menuComponents[i];
            this.menuComponents.add(component);
        }
    }

    public void setParent(MenuComponent parentMenu) {
        if (parentMenu != null) {
            // look up the parent and make sure that it has this menu as a child
            if (!parentMenu.getComponents().contains(this)) {
                parentMenu.addMenuComponent(this);
            }
        }
        this.parentMenu = parentMenu;
    }

    public MenuComponent getParent() {
        return parentMenu;
    }

    /**
     * Convenience method for Velocity templates
     * @return menuComponents as a java.util.List
     */
    public List<MenuComponent> getComponents() {
        return menuComponents;
    }

    /**
     * This method compares all attributes, except for parent and children
     *
     * @param o the object to compare to
     */
    public boolean equals(Object o) {
        if (!(o instanceof MenuComponent)) {
            return false;
        }
        MenuComponent m = (MenuComponent) o;
        // Compare using StringUtils to avoid NullPointerExceptions
        return StringUtils.equals(m.getAction(), this.action) &&
                StringUtils.equals(m.getAlign(), this.align) &&
                StringUtils.equals(m.getAltImage(), this.altImage) &&
                StringUtils.equals(m.getDescription(), this.description) &&
                StringUtils.equals(m.getForward(), this.forward) &&
                StringUtils.equals(m.getHeight(), this.height) &&
                StringUtils.equals(m.getImage(), this.image) &&
                StringUtils.equals(m.getLocation(), this.location) &&
                StringUtils.equals(m.getName(), this.name) &&
                StringUtils.equals(m.getOnclick(), this.onclick) &&
                StringUtils.equals(m.getOndblclick(), this.ondblclick) &&
                StringUtils.equals(m.getOnmouseout(), this.onmouseout) &&
                StringUtils.equals(m.getOnmouseover(), this.onmouseover) &&
                StringUtils.equals(m.getOnContextMenu(), this.onContextMenu) &&
                StringUtils.equals(m.getPage(), this.page) &&
                StringUtils.equals(m.getRoles(), this.roles) &&
                StringUtils.equals(m.getTarget(), this.target) &&
                StringUtils.equals(m.getTitle(), this.title) &&
                StringUtils.equals(m.getToolTip(), this.toolTip) &&
                StringUtils.equals(m.getWidth(), this.width) &&
                StringUtils.equals(m.getModule(), this.module);
    }

    /**
     * Get the depth of the menu
     *
     * @return Depth of menu
     */
    public int getMenuDepth() {
        return getMenuDepth(this, 0);
    }

    private int getMenuDepth(MenuComponent menu, int currentDepth) {
        int depth = currentDepth + 1;

        MenuComponent[] subMenus = menu.getMenuComponents();
        if (subMenus != null) {
            for (int a = 0; a < subMenus.length; a++) {
                int depthx = getMenuDepth(subMenus[a], currentDepth + 1);
                if (depth < depthx)
                    depth = depthx;
            }
        }

        return depth;
    }

    /**
     * Returns the last.
     *
     * @return boolean
     */
    public boolean isLast() {
        return last;
    }

    /**
     * Sets the last.
     *
     * @param last The last to set
     */
    public void setLast(boolean last) {
        this.last = last;
    }

    /**
     * Remove all children from a parent menu item
     */
    public void removeChildren() {
        for (Iterator<MenuComponent> iterator = this.getComponents().iterator(); iterator.hasNext();) {
            MenuComponent child = iterator.next();
            child.setParent(null);
            iterator.remove();
        }
    }

    public String getBreadCrumb() {
        return breadCrumb;
    }

    /**
     * Build the breadcrumb trail leading to this menuComponent
     *
     * @param delimiter type of separator
     */
    protected void setBreadCrumb(String delimiter) {
        if (getParent() == null) {
            breadCrumb = name;
            setChildBreadCrumb(delimiter);
        } else {
            MenuComponent parent = getParent();
            breadCrumb = parent.getBreadCrumb() + delimiter + name;
            setChildBreadCrumb(delimiter);
        }
    }

    private void setChildBreadCrumb(String delimiter) {
        List<MenuComponent> children = this.getComponents();
        for (Iterator<MenuComponent> iterator = children.iterator(); iterator.hasNext();) {
            MenuComponent child =  iterator.next();
            child.setBreadCrumb(delimiter);
        }
    }

    public String toString() {
        return "name: " + this.name;
    }
}
