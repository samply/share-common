package de.samply.share.common.model.uiquerybuilder;

import de.samply.common.mdrclient.domain.EnumElementType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for the data elements tree found on the form edition page.
 *
 * @author diogo
 */
public final class MenuItemTreeManager {

  /**
   * Style class name for text of items which is not available in the chosen locale.
   */
  private static final String STYLE_CLASS_NOT_AVAILABLE_IN_LOCALE = "notAvailableInLocale";
  /**
   * Style class name for data element group anchors.
   */
  private static final String A_GROUP = "searchMdrGroup";
  /**
   * Style class names for data element open group folders.
   */
  private static final String I_FOLDER_OPEN = "fa fa-folder-open-o searchMdrGroupIcon";
  /**
   * Style class names for data element closed group folders.
   */
  private static final String I_FOLDER_CLOSED = "fa fa-folder-o searchMdrGroupIcon";
  /**
   * Style class name for data element list items.
   */
  private static final String LI_ACTIVE = "active";

  /**
   * Logger for the menu item tree manager.
   */
  private static Logger logger = LoggerFactory.getLogger(MenuItemTreeManager.class);

  /**
   * Prevent instantiation.
   */
  private MenuItemTreeManager() {
  }

  /**
   * Remove style classes from a Menu Item.
   *
   * @param menuItem the item for which the style classes will be removed.
   */
  public static void cleanMenuItemStyleClass(final MenuItem menuItem) {
    if (menuItem != null) {
      // clean active style classes
      menuItem.setStyleClassLi("");
      menuItem.setStyleClassI(I_FOLDER_CLOSED);
    }
  }

  /**
   * Remove style classes from the Menu Items in the complete tree.
   *
   * @param menuItemList the root element from the tree.
   */
  public static void cleanMenuItemsStyleClass(final List<MenuItem> menuItemList) {
    for (MenuItem i : menuItemList) {
      if (i != null) {
        // clean active style classes
        cleanMenuItemStyleClass(i);
        cleanMenuItemsStyleClass(i.getChildren());
      }
    }
  }

  /**
   * Remove the children from a menu item.
   *
   * @param menuItem a menu item from which children will be removed.
   */
  public static void clearMenuItemChildren(final MenuItem menuItem) {
    if (menuItem != null) {
      menuItem.getChildren().clear();
    }
  }

  /**
   * Add a menu item as child from another menu item.
   *
   * @param menuItem the menu item to add
   * @param parent   the parent menu item
   */
  public static void addMenuItem(final MenuItem menuItem, final MenuItem parent) {
    parent.getChildren().add(menuItem);
  }

  /**
   * Check whether the data element group is open.
   *
   * @param menuItem the menu item representing the data element group to check
   * @return true if the item is open, false otherwise
   */
  public static boolean isItemOpen(final MenuItem menuItem) {
    return menuItem != null && menuItem.getStyleClassI().contains(I_FOLDER_OPEN);
  }

  /**
   * Manage style classes that indicate that a given menu item is open, showing its children.
   *
   * @param menuItem the menu item to edit.
   */
  public static void setItemAndParentsOpen(final MenuItem menuItem) {
    if (menuItem != null) {
      menuItem.setStyleClassLi(LI_ACTIVE);
      menuItem.setStyleClassI(I_FOLDER_OPEN);
      setItemAndParentsOpen(menuItem.getParent());
    }
  }

  /**
   * Creates a menu item.
   *
   * @param mdrId           the data element MDR ID
   * @param enumElementType the data element type
   * @param designation     the designation (name)
   * @param definition      the definition (description)
   * @param children        the children list (if applicable)
   * @param parent          the parent menu item (if applicable)
   * @return a menu item
   */
  public static MenuItem buildMenuItem(final String mdrId, final EnumElementType enumElementType,
      final String designation, final String definition, final ArrayList<MenuItem> children,
      final MenuItem parent) {
    return buildMenuItem(mdrId, enumElementType, designation, definition, children, parent, null);
  }

  /**
   * Creates a menu item.
   *
   * @param mdrId           the data element MDR ID
   * @param enumElementType the data element type
   * @param designation     the designation (name)
   * @param definition      the definition (description)
   * @param children        the children list (if applicable)
   * @param parent          the parent menu item (if applicable)
   * @param searchString    the search string (if applicable)
   * @return a menu item
   */
  public static MenuItem buildMenuItem(final String mdrId, final EnumElementType enumElementType,
      final String designation, final String definition, final ArrayList<MenuItem> children,
      final MenuItem parent, final String searchString) {
    MenuItem menuItem = null;
    switch (enumElementType) {
      case DATAELEMENTGROUP:
        menuItem = new MenuItem(mdrId, designation, definition, enumElementType, children, "",
            A_GROUP,
            I_FOLDER_CLOSED, parent);
        break;
      case DATAELEMENT:
        menuItem = new MenuItem(mdrId, designation, definition, enumElementType, children, "", "",
            "", parent);
        break;
      case RECORD:
        menuItem = new MenuItem(mdrId, designation, definition, enumElementType, children, "", "",
            "", parent);
        break;
      case CATALOGUEGROUP:
        menuItem = new MenuItem(mdrId, designation, definition, enumElementType, children, "",
            A_GROUP,
            I_FOLDER_CLOSED, parent);
        break;
      case CATALOGUEELEMENT:
        menuItem = new MenuItem(mdrId, designation, definition, enumElementType, children, "", "",
            "", parent, searchString);
        break;
      default:
        break;
    }

    return menuItem;
  }

  /**
   * Find a menu item in the tree.
   *
   * @param menuItems     the root of the tree where the item will be searched.
   * @param menuItemMdrId the MDR ID of the item to search.
   * @return the menu item found.
   */
  public static MenuItem getMenuItem(final List<MenuItem> menuItems, final String menuItemMdrId) {
    for (MenuItem i : menuItems) {
      if (i != null) {
        if (i.getMdrId().compareTo(menuItemMdrId) == 0) {
          return i;
        } else {
          MenuItem menuItem = null;
          menuItem = getMenuItem(i.getChildren(), menuItemMdrId); // go deeper
          if (menuItem != null) {
            return menuItem;
          }
        }
      }
    }
    return null;
  }
}
