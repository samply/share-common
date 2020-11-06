package de.samply.share.common.control.uiquerybuilder;

import com.sun.jersey.api.client.Client;
import de.samply.auth.client.jwt.JwtException;
import de.samply.auth.rest.AccessTokenDto;
import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.DataElement;
import de.samply.common.mdrclient.domain.Designation;
import de.samply.common.mdrclient.domain.EnumElementType;
import de.samply.common.mdrclient.domain.EnumIdentificationStatus;
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.ResultList;
import de.samply.jsf.JsfUtils;
import de.samply.jsf.MdrUtils;
import de.samply.share.common.model.uiquerybuilder.MenuItem;
import de.samply.share.common.model.uiquerybuilder.MenuItemTreeManager;
import de.samply.share.common.utils.ProjectInfo;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.common.utils.oauth2.OAuthUtils;
import de.samply.web.mdrfaces.MdrContext;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlPanelGroup;
import org.omnifaces.util.Ajax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract MDR item search panel controller.
 */
public abstract class AbstractItemSearchController implements Serializable {

  private static final Logger logger
      = LoggerFactory.getLogger(AbstractItemSearchController.class);
  protected String language;
  /**
   * Menu items.
   */
  protected transient List<MenuItem> menuItems = new ArrayList<>();
  /**
   * MDR Client instance.
   */
  protected transient MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();
  /**
   * Available search keywords.
   */
  private transient List<String> keywords = new ArrayList<>();
  /**
   * list of search menu items.
   */
  private transient List<MenuItem> searchedMenuItems = new ArrayList<>();
  /**
   * Selected search keywords.
   */
  private List<String> selectedKeywords = new ArrayList<>();
  /**
   * Search text.
   */
  private String searchText;

  /*
   * Panels binding
   */
  /**
   * The panel that includes both the items navigation panel and filtered items panel.
   */
  private transient HtmlPanelGroup searchPanel = new HtmlPanelGroup();
  /**
   * The panel where users can navigate through the items.
   */
  private transient HtmlPanelGroup itemNavigationPanel = new HtmlPanelGroup();
  /**
   * The panel where the search/filter results are presented.
   */
  private transient HtmlPanelGroup itemFilterPanel = new HtmlPanelGroup();

  /**
   * The refresh button (to contact again the MDR).
   */
  private transient HtmlCommandLink refreshButton = new HtmlCommandLink();

  /**
   * Access token for MDR connection.
   */
  private AccessTokenDto accessToken;

  private List<String> searchNamespaces = new ArrayList<>();

  private List<Result> additionalSearchItems = new ArrayList<>();

  public AbstractItemSearchController() {
    language = "de"; // default setting
  }

  public AbstractItemSearchController(String language) {
    this.language = language;
  }

  /**
   * Triggered when the filter button is pressed. Check what items are related to the search terms,
   * hide the navigation panel and show the filtered results panel.
   */
  public final void onSearch() throws InvalidKeyException, NoSuchAlgorithmException,
      SignatureException, JwtException, IOException {
    logger.debug("Searching items...");
    String projectName = ProjectInfo.INSTANCE.getProjectName();
    //  String language = "de";

    if (projectName.equalsIgnoreCase("osse")) {
      language = "en";
    }

    searchedMenuItems = new ArrayList<>();

    if (searchText != null && !searchText.isEmpty()) {

      List<Result> resultList;

      if (projectName.equalsIgnoreCase("osse")) {
        resultList = searchItemsLocal(searchText, language);
      } else {
        // First, search in the default namespace
        // TODO: maybe remove this and have the calling application specify ALL namespaces
        //  explicitly?
        // XXX: if we bypass authentication, there is no default namespace - so this is moot anyways
        //  resultList = searchItems(searchText, language, null);
        resultList = new ArrayList<>();

        for (String namespace : searchNamespaces) {
          List<Result> resultsToAdd = searchItems(searchText, language, namespace);

          for (Result result : resultsToAdd) {
            if (result.getIdentification().getStatus() == EnumIdentificationStatus.RELEASED) {
              resultList.add(result);
            }
          }
        }

        List<Result> fromAdditionalSearchItems = searchInAddtionalItems(searchText);
        for (Result result : fromAdditionalSearchItems) {
          resultList.add(result);
        }
      }

      if (resultList == null || resultList.isEmpty()) {
        logger.debug("No results found for search query: " + searchText + " in language " + language
            + " in any namespaces");
      } else {
        for (Result r : resultList) {
          if (r.getType().compareTo(EnumElementType.DATAELEMENT.name()) == 0
              || r.getType().compareTo(EnumElementType.RECORD.name()) == 0) {
            MenuItem menuItem = MenuItemTreeManager.buildMenuItem(r.getId(),
                EnumElementType.valueOf(r.getType()),
                MdrUtils.getDesignation(r.getDesignations()),
                MdrUtils.getDefinition(r.getDesignations()), new ArrayList<MenuItem>(), null);
            searchedMenuItems.add(menuItem);
          }
        }
      }
    }

    itemNavigationPanel.setStyleClass("hidden");
    refreshButton.setStyleClass(refreshButton.getStyleClass() + " disabled");
    itemFilterPanel.setStyleClass("visible");
    Ajax.update(searchPanel.getClientId(), refreshButton.getClientId());
  }

  /**
   * Check the list of additional items for matches.
   *
   * @param searchText the text to search for
   * @return a list of found results
   */
  private List<Result> searchInAddtionalItems(String searchText) {
    List<Result> resultList = new ArrayList<>();

    for (Result item : additionalSearchItems) {
      for (Designation designation : item.getDesignations()) {
        if (designation.getDefinition().toLowerCase().contains(searchText.toLowerCase())
            || designation.getDesignation().toLowerCase().contains(searchText.toLowerCase())) {
          resultList.add(item);
        }
      }
    }

    return resultList;
  }

  /**
   * Searches items in all MDR namespaces (used for osse).
   *
   * @param searchText the searched mdr
   * @return the list of matching results
   */
  protected List<Result> searchItemsLocal(String searchText, String language) {
    try {
      return mdrClient.searchLocal(searchText, language, null);
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException ex) {
      logger.error("Error while searching MDR", ex);
      return Collections.emptyList();
    }
  }

  /**
   * Searches items in the MDR.
   *
   * @param searchText the searched mdr
   * @return the list of matching results
   */
  protected List<Result> searchItems(String searchText, String language, String namespace) {
    try {
      if (namespace != null && namespace.length() > 0) {
        return mdrClient.searchInNamespace(searchText, language,
            getAccessToken(), namespace);
      } else {
        return mdrClient.search(searchText, language,
            getAccessToken());
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException ex) {
      logger.error("Error while searching MDR", ex);
      return Collections.emptyList();
    }
  }

  /**
   * Searches items in the MDR.
   *
   * @param searchText the searched mdr
   * @return the list of matching results
   */
  protected List<Result> searchItems(String searchText) {
    return searchItems(searchText, JsfUtils.getLocaleLanguage(), null);
  }

  /**
   * Triggered when the search clear button is pressed.
   */
  public final void onClearSearch() {
    selectedKeywords = new ArrayList<>();
    searchText = "";

    refreshButton.setStyleClass(refreshButton.getStyleClass().replace("disabled", ""));
    itemNavigationPanel.setStyleClass("visible");
    itemFilterPanel.setStyleClass("hidden");
    Ajax.update(searchPanel.getParent().getClientId(), refreshButton.getClientId());
  }

  /**
   * Get the item navigation panel.
   *
   * @return the item navigation panel
   */
  public final HtmlPanelGroup getItemNavigationPanel() {
    return itemNavigationPanel;
  }

  /**
   * Set the item navigation panel.
   *
   * @param itemNavigationPanel the item navigation panel
   */
  public final void setItemNavigationPanel(final HtmlPanelGroup itemNavigationPanel) {
    this.itemNavigationPanel = itemNavigationPanel;
  }

  /**
   * Get the item filter panel.
   *
   * @return the item filter panel
   */
  public final HtmlPanelGroup getItemFilterPanel() {
    return itemFilterPanel;
  }

  /**
   * Set the item filter panel.
   *
   * @param itemFilterPanel the item filter panel
   */
  public final void setItemFilterPanel(final HtmlPanelGroup itemFilterPanel) {
    this.itemFilterPanel = itemFilterPanel;
  }

  /**
   * Get the selected keywords.
   *
   * @return the selected keywords
   */
  public final List<String> getSelectedKeywords() {
    return selectedKeywords;
  }

  /**
   * Set the selected keywords.
   *
   * @param selectedKeywords the selected keywords
   */
  public final void setSelectedKeywords(final List<String> selectedKeywords) {
    this.selectedKeywords = selectedKeywords;
  }

  /**
   * Get the searched menu items (search result).
   *
   * @return the searched menu items
   */
  public final List<MenuItem> getSearchedMenuItems() {
    return searchedMenuItems;
  }

  /**
   * Set the searched menu items.
   *
   * @param searchedMenuItems the searched menu items
   */
  public final void setSearchedMenuItems(final List<MenuItem> searchedMenuItems) {
    this.searchedMenuItems = searchedMenuItems;
  }

  /**
   * Get the search keywords.
   *
   * @return the keywords
   */
  public final List<String> getKeywords() {
    logger.debug("Resolving list of item keywords to help on the item search...");
    HashSet<String> keysHashSet = new HashSet<>();

    // TODO the auto complete search has to be done through the MDR - first the MDR has to support
    //  this
    List<Result> items = new ArrayList<>();
    // List<Result> items = getDataElements();
    String[] designationKeywords;
    for (Result r : items) {
      designationKeywords = r.getDesignations().get(0).getDesignation().split(" ");
      for (String k : designationKeywords) {
        keysHashSet.add(k);
      }
    }
    keywords = new ArrayList<>(keysHashSet);
    return keywords;
  }

  /**
   * Set the keywords.
   *
   * @param keywords the keywords
   */
  public final void setKeywords(final List<String> keywords) {
    this.keywords = keywords;
  }

  /**
   * Get the search panel.
   *
   * @return the search panel
   */
  public final HtmlPanelGroup getSearchPanel() {
    return searchPanel;
  }

  /**
   * Set the search panel.
   *
   * @param searchPanel the search panel
   */
  public final void setSearchPanel(final HtmlPanelGroup searchPanel) {
    this.searchPanel = searchPanel;
  }

  /**
   * Get the refresh button.
   *
   * @return the refresh button
   */
  public final HtmlCommandLink getRefreshButton() {
    return refreshButton;
  }

  /**
   * Set the refresh button.
   *
   * @param refreshButton the refresh button
   */
  public final void setRefreshButton(final HtmlCommandLink refreshButton) {
    this.refreshButton = refreshButton;
  }

  /**
   * Get the menu items.
   *
   * @return the menu items
   */
  public final List<MenuItem> getMenuItems() {
    return menuItems;
  }

  /**
   * Set the menu items.
   *
   * @param menuItems the menu items
   */
  public final void setMenuItems(final List<MenuItem> menuItems) {
    this.menuItems = menuItems;
  }

  /**
   * Get the search text.
   *
   * @return the search text
   */
  public final String getSearchText() {
    return searchText;
  }

  /**
   * Set the search text.
   *
   * @param searchText the search text
   */
  public final void setSearchText(final String searchText) {
    this.searchText = searchText;
  }

  /**
   * Todo.
   * @return the currently used namespace to use for item search in MDR; if null, use the user's
   *        root namespace
   */
  protected List<String> getSearchNamespaces() {
    return searchNamespaces;
  }

  /**
   * Todo.
   * @param namespaces namespaces to use for item search in MDR
   */
  protected void setSearchNamespaces(List<String> namespaces) {
    searchNamespaces = namespaces;
  }

  protected void addToSearchNamespaces(String namespace) {
    if (searchNamespaces == null) {
      searchNamespaces = new ArrayList<>();
    }
    searchNamespaces.add(namespace);
  }

  public List<Result> getAdditionalSearchItems() {
    return additionalSearchItems;
  }

  public void setAdditionalSearchItems(List<Result> additionalSearchItems) {
    this.additionalSearchItems = additionalSearchItems;
  }

  /**
   * Get the private key to login at the MDR via Auth.
   * @return the private key to login at the MDR via Auth
   */
  public abstract String getPrivateKey();

  /**
   * Get the key ID of the registry as user on the Auth server that is used by the MDR.
   * @return the key ID of the registry as user on the Auth server that is used by the MDR
   */
  public abstract String getMdrAuthKeyId();

  /**
   * Get the URL of the Auth server that is used by the MDR.
   * @return the URL of the Auth server that is used by the MDR
   */
  public abstract String getMdrAuthUrl();

  /**
   * Gets an access token for connecting to the MDR. Only if it has not been requested before, it
   * makes a new request to the OAuth to obtain an access token.
   *
   * @return accessToken
   */
  protected String getAccessToken() {
    if (accessToken != null) {
      // TODO refresh access token (if expired)
      return accessToken.getAccessToken();
    } else {
      try {
        accessToken = OAuthUtils
            .getAccessToken(Client.create(), getMdrAuthUrl(), getMdrAuthKeyId(), getPrivateKey());
        if (accessToken != null) {
          return accessToken.getAccessToken();
        } else {
          logger.error("Error obtaining OAuth access token");
          return null;
        }
      } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
        throw new RuntimeException("Error obtaining OAuth access token", ex);
      }
    }
  }

  /**
   * Gets the root elements of the user from the MDR client.
   *
   * @return the root elements of the user
   */
  protected ResultList getMdrRootElements() {
    try {
      return mdrClient
          .getUserRootElements(JsfUtils.getLocaleLanguage(), getAccessToken(), getMdrAuthKeyId());
    } catch (ExecutionException ex) {
      throw new RuntimeException("Error loading MDR root elements", ex);
    }
  }

  /**
   * Gets the root elements of a given namespace from the MDR client.
   *
   * @param namespace a namespace
   * @return the root elements of the given namespace
   */
  protected ResultList getMdrRootElements(String namespace) {
    if (namespace.equalsIgnoreCase("dktk")) {
      return getMdrRootElements();
    }
    try {
      ResultList resultList = new ResultList();
      List<Result> results = mdrClient
          .getNamespaceMembers("en", getAccessToken(), getMdrAuthKeyId(), namespace);
      resultList.setResults(results);
      return resultList;
    } catch (ExecutionException ex) {
      throw new RuntimeException("Error loading MDR root elements", ex);
    }
  }

  /**
   * Gets the root elements of a given namespace from the MDR client.
   *
   * @param namespace   a namespace
   * @param accessToken the access token of the logged in user
   * @param authId      the auth id of the user
   * @return the root elements of the given namespace
   */
  protected ResultList getMdrRootElements(String namespace, String accessToken, String authId) {
    if (namespace.equalsIgnoreCase("dktk")) {
      return getMdrRootElements();
    }
    try {
      ResultList resultList = new ResultList();
      List<Result> results = mdrClient.getNamespaceMembers("en", accessToken, authId, namespace);
      resultList.setResults(results);
      return resultList;
    } catch (ExecutionException ex) {
      throw new RuntimeException("Error loading MDR root elements", ex);
    }
  }

  /**
   * Gets the root elements of a given namespace from the MDR client. Explicitly skip
   * authentication.
   *
   * @param namespace a namespace
   * @param language  language code
   * @return the root elements of the given namespace
   */
  protected ResultList getNamespaceMembersAnonymous(String namespace, String language) {
    if (language == null || language.length() < 2) {
      language = "en";
    }
    try {
      ResultList resultList = new ResultList();
      List<Result> results = mdrClient.getNamespaceMembers(language, namespace);
      resultList.setResults(results);
      return resultList;
    } catch (ExecutionException ex) {
      throw new RuntimeException("Error loading MDR root elements", ex);
    }
  }

  /**
   * Called when a group of elements must be loaded.
   *
   * @param mdrId the mdrId
   * @return list of group elements
   */
  protected List<Result> getGroupMembers(String mdrId) {
    try {
      return mdrClient.getMembers(mdrId, JsfUtils.getLocaleLanguage(),
          getAccessToken(), getMdrAuthKeyId());
    } catch (MdrConnectionException | ExecutionException ex) {
      throw new RuntimeException("There was an error "
          + " while trying to load MDR item members of element with id " + mdrId + ".", ex);
    }
  }

  /**
   * Gets the MDR elements for a list of MDR Keys from the MDR client.
   *
   * @param keys the list of keys to get
   * @return the elements belonging to the given keys
   */
  protected ResultList getMdrElements(List<String> keys) {
    if (SamplyShareUtils.isNullOrEmpty(keys)) {
      return new ResultList();
    }
    try {
      ResultList resultList = new ResultList();
      List<Result> results = new ArrayList<>();
      for (String key : keys) {
        DataElement dataElement = mdrClient.getDataElement(key, "de");
        results.add(SamplyShareUtils.dataElementToResult(dataElement));
      }
      resultList.setResults(results);
      return resultList;
    } catch (ExecutionException | MdrConnectionException | MdrInvalidResponseException ex) {
      throw new RuntimeException("Error loading MDR keys", ex);
    }
  }

  /**
   * Must be overridden to set and reset the menu items.
   */
  public abstract void resetMenuItems();

  /**
   * Event called when the user clicks on a data element group. Load a menu item children list.
   *
   * @param mdrId the MDR ID of the parent data element group
   */
  public void onDataElementGroupClick(final String mdrId) {
    logger.debug("Loading menu item children...");

    MenuItem parent = MenuItemTreeManager.getMenuItem(menuItems, mdrId);

    if (MenuItemTreeManager.isItemOpen(parent)) { // just let javascript close the drawer
      MenuItemTreeManager.cleanMenuItemStyleClass(parent);
    } else {
      MenuItemTreeManager.cleanMenuItemsStyleClass(menuItems);
      MenuItemTreeManager.clearMenuItemChildren(parent);

      if (parent != null) {
        logger.debug("Menu " + parent.getDesignation() + ", " + parent.getMdrId() + " clicked.");
      }

      for (Result r : getGroupMembers(mdrId)) {
        MenuItem menuItem = MenuItemTreeManager
            .buildMenuItem(r.getId(), EnumElementType.valueOf(r.getType()),
                MdrUtils.getDesignation(r.getDesignations()),
                MdrUtils.getDefinition(r.getDesignations()), new ArrayList<MenuItem>(), parent);
        MenuItemTreeManager.addMenuItem(menuItem, parent);
      }
      MenuItemTreeManager.setItemAndParentsOpen(parent);
      Ajax.update(itemNavigationPanel.getClientId());
    }

  }

  /**
   * Clears the MdrClient cache and refresh the MDR items list.
   */
  public void onMdrItemsRefresh() {
    logger.debug("Clear MDR cache...");
    mdrClient.cleanCache();
  }

}
