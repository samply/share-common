package de.samply.share.common.control.uiquerybuilder;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.common.model.uiquerybuilder.EnumConjunction;
import de.samply.share.common.model.uiquerybuilder.EnumOperator;
import de.samply.share.common.model.uiquerybuilder.QueryItem;
import de.samply.share.common.utils.MdrDatatype;
import de.samply.share.common.utils.QueryTreeUtil;
import de.samply.share.model.common.Query;
import de.samply.share.utils.QueryConverter;
import de.samply.web.mdrfaces.MdrContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBException;
import org.omnifaces.model.tree.ListTreeModel;
import org.omnifaces.model.tree.TreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSearchController implements Serializable {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 585856018106743936L;
  /**
   * The Constant CONJUNCTION_GROUP.
   */
  private static final String CONJUNCTION_GROUP_ID = "conjunctionGroup";
  /**
   * The operator list.
   */
  private static final List<String> operatorList;
  /**
   * The list of available conjunctions.
   */
  private static List<String> conjunctionList;

  static {
    conjunctionList = new ArrayList<>();
    conjunctionList.add(EnumConjunction.AND.toString());
    conjunctionList.add(EnumConjunction.OR.toString());
  }

  static {
    operatorList = new ArrayList<>();
    operatorList.add(EnumOperator.EQUAL.toString());
    operatorList.add(EnumOperator.GREATER.toString());
    operatorList.add(EnumOperator.GREATER_OR_EQUAL.toString());
    operatorList.add(EnumOperator.IS_NOT_NULL.toString());
    operatorList.add(EnumOperator.IS_NULL.toString());
    operatorList.add(EnumOperator.LESS_OR_EQUAL_THEN.toString());
    operatorList.add(EnumOperator.LESS_THEN.toString());
    operatorList.add(EnumOperator.LIKE.toString());
    operatorList.add(EnumOperator.NOT_EQUAL_TO.toString());
    operatorList.add(EnumOperator.BETWEEN.toString());
    // operatorList.add(EnumOperator.IN.toString());
  }

  /**
   * The query tree.
   */
  protected TreeModel<QueryItem> queryTree;
  /**
   * The logger.
   */
  private transient Logger logger = LoggerFactory.getLogger(this.getClass().getName());
  /**
   * The mdr client.
   */
  private transient MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();
  /**
   * The sortable item panel.
   */
  private transient HtmlPanelGroup sortableItemPanel;

  /**
   * Instantiates a new search controller. Adds the initial conjunction group.
   */
  public AbstractSearchController() {
    queryTree = new ListTreeModel<>();

    QueryItem rootGroup = new QueryItem();
    rootGroup.setConjunction(EnumConjunction.AND);
    rootGroup.setMdrId(CONJUNCTION_GROUP_ID);
    rootGroup.setRoot(true);

    queryTree.addChild(rootGroup);

  }

  /**
   * Restores query tree if the serialized query is not empty.
   */
  public void init() {
    String serializedQuery = getSerializedQuery();
    if (getSerializedQuery() != null && !serializedQuery.isEmpty()) {
      restoreQuery();
    }
  }

  /**
   * Gets the conjunction list.
   *
   * @return the conjunction list
   */
  public List<String> getConjunctionList() {
    return conjunctionList;
  }

  /**
   * Must be overridden to provide the serialized query as string.
   *
   * @return the Query, serialized as XML string
   */
  public abstract String getSerializedQuery();

  /**
   * Must be overridden to set the query as string.
   *
   * @param serializedQuery the Query, serialized as XML string
   */
  public abstract void setSerializedQuery(String serializedQuery);

  /**
   * Gets the query tree.
   *
   * @return the query tree
   */
  public TreeModel<QueryItem> getQueryTree() {
    return queryTree;
  }

  /**
   * Sets the query tree.
   *
   * @param queryTree the new query tree
   */
  public void setQueryTree(TreeModel<QueryItem> queryTree) {
    this.queryTree = queryTree;
  }

  /**
   * Adds the current mdr item to query root. The mdr id of said element is stored in the request
   * parameter map during the drop event from jquery ui.
   */
  public void addItemToQueryRoot() {
    String mdrId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
        .get("mdrId");
    String searchString = FacesContext.getCurrentInstance().getExternalContext()
        .getRequestParameterMap().get("searchString");

    TreeModel<QueryItem> node = queryTree;

    if (searchString != null && searchString.length() > 0) {
      // If searchString is set, we have an element from an entity catalogue.
      TreeModel<QueryItem> catalogueEntry = QueryTreeUtil
          .conditionTypeStringToTreenode(searchString);
      for (TreeModel<QueryItem> child : catalogueEntry.getChildren()) {
        node.addChildNode(child);
      }
    } else {
      QueryItem queryItem = new QueryItem();
      queryItem.setMdrId(mdrId);
      node.addChild(queryItem);
    }
  }

  /**
   * Adds the current mdr item to the conjunction group it was dropped on. The mdr id of said
   * element as well as the group id are stored in the request parameter map during the drop event
   * from jquery ui.
   */
  public void addItemToQueryGroup() {
    String mdrId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
        .get("mdrId");
    String tmpId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
        .get("tmpId");

    TreeModel<QueryItem> node = null;

    node = getNodeByTmpId(queryTree, tmpId);

    if (node == null) {
      logger.debug("No matching id found " + tmpId + " ...adding to root");
      node = queryTree;
    }

    // go back through tree to check for conj group or use root
    node = getParentConjunctionGroup(node);

    String searchString = FacesContext.getCurrentInstance().getExternalContext()
        .getRequestParameterMap().get("searchString");
    if (searchString != null && searchString.length() > 0) {
      // If searchString is set, we have an element from an entity catalogue.
      TreeModel<QueryItem> catalogueEntry = QueryTreeUtil
          .conditionTypeStringToTreenode(searchString);
      for (TreeModel<QueryItem> child : catalogueEntry.getChildren()) {
        node.addChildNode(child);
      }
    } else {
      QueryItem queryItem = new QueryItem();
      queryItem.setMdrId(mdrId);
      node.addChild(queryItem);
    }
  }

  /**
   * Gets the treenode in the queryTree for the given tmp id.
   *
   * @param node  the (relative) rootnode from where the search is started
   * @param tmpId the tmp id of the element to be looked for
   * @return the matching node
   */
  private TreeModel<QueryItem> getNodeByTmpId(TreeModel<QueryItem> node, String tmpId) {

    if (!node.isRoot() && node.getData().getTempId().equalsIgnoreCase(tmpId)) {
      return node;
    }

    TreeModel<QueryItem> foundNode = null;
    List<TreeModel<QueryItem>> childNodes = node.getChildren();

    for (int i = 0; foundNode == null && i < childNodes.size(); ++i) {
      foundNode = getNodeByTmpId(childNodes.get(i), tmpId);
    }

    return foundNode;
  }

  /**
   * Gets the parent conjunction group for a given treenode by traversing the tree in bottom-up
   * direction.
   *
   * @param node the node to check
   * @return the first ancestor that is a conjunction group
   */
  private TreeModel<QueryItem> getParentConjunctionGroup(TreeModel<QueryItem> node) {
    if (node.isRoot()) {
      return node;
    }

    if (node.getData().getMdrId() != null && node.getData().getMdrId()
        .equalsIgnoreCase(CONJUNCTION_GROUP_ID)) {
      return node;
    }
    return getParentConjunctionGroup(node.getParent());
  }

  /**
   * Adds a child node to the query tree.
   *
   * @param node  the node to which the new node will be attached
   * @param mdrId the mdr id of the new node
   */
  public void addChild(TreeModel<QueryItem> node, String mdrId) {
    QueryItem queryItem = new QueryItem();
    queryItem.setMdrId(mdrId);
    node.addChild(queryItem);
  }

  /**
   * Clones the node from the query tree as a sibling.
   *
   * @param node the node
   */
  public void clone(TreeModel<QueryItem> node) {
    QueryItem original = node.getData();
    QueryItem clone = new QueryItem();
    clone.setMdrId(original.getMdrId());
    clone.setOperator(original.getOperator());
    node.getParent().addChild(clone);
  }

  /**
   * Removes the node from the query tree.
   *
   * @param node the node
   */
  public void remove(TreeModel<QueryItem> node) {
    node.remove();
  }

  /**
   * On submit.
   *
   * @return the string
   */
  public abstract String onStoreAndRelease();

  /**
   * On submit.
   *
   * @return the string
   */
  public abstract String onSubmit();

  /**
   * Gets the sortable item panel.
   *
   * @return the sortable item panel
   */
  public HtmlPanelGroup getSortableItemPanel() {
    return sortableItemPanel;
  }

  /**
   * Sets the sortable item panel.
   *
   * @param sortableItemPanel the new sortable item panel
   */
  public void setSortableItemPanel(HtmlPanelGroup sortableItemPanel) {
    this.sortableItemPanel = sortableItemPanel;
  }

  /**
   * Gets the operator list.
   *
   * @return the operator list
   */
  public List<String> getOperatorList() {
    return operatorList;
  }

  /**
   * Generate the operator list based on the validation type of the given mdrkey.
   *
   * @param mdrId the mdr id
   * @return the list of suitable operators for that element
   */
  public List<String> getOperatorList(String mdrId) {
    Validations validations;
    MdrDatatype dataType;
    try {
      validations = mdrClient.getDataElementValidations(mdrId, "en");
      dataType = MdrDatatype.get(validations.getDatatype());
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      e.printStackTrace();
      dataType = null;
    }
    List<String> ops = new ArrayList<>();

    // Equal is available for each type
    ops.add(EnumOperator.EQUAL.toString());

    if (dataType == null) {
      ops.add(EnumOperator.NOT_EQUAL_TO.toString());
      ops.add(EnumOperator.GREATER.toString());
      ops.add(EnumOperator.GREATER_OR_EQUAL.toString());
      ops.add(EnumOperator.LESS_OR_EQUAL_THEN.toString());
      ops.add(EnumOperator.LESS_THEN.toString());
      ops.add(EnumOperator.BETWEEN.toString());
      ops.add(EnumOperator.LIKE.toString());
      ops.add(EnumOperator.IS_NOT_NULL.toString());
      ops.add(EnumOperator.IS_NULL.toString());
    } else {
      switch (dataType) {
        case FLOAT:
        case INTEGER:
        case DATE:
        case TIME:
        case DATETIME:
          ops.add(EnumOperator.NOT_EQUAL_TO.toString());
          ops.add(EnumOperator.GREATER.toString());
          ops.add(EnumOperator.GREATER_OR_EQUAL.toString());
          ops.add(EnumOperator.LESS_OR_EQUAL_THEN.toString());
          ops.add(EnumOperator.LESS_THEN.toString());
          ops.add(EnumOperator.BETWEEN.toString());
          break;
        case STRING:
          ops.add(EnumOperator.NOT_EQUAL_TO.toString());
          ops.add(EnumOperator.LIKE.toString());
          break;
        case BOOLEAN:
        default:
          break;
      }

      // TODO: EnumOperator.IN

      // Do not add is (not) null for booleans to avoid confusion
      if (dataType != MdrDatatype.BOOLEAN) {
        ops.add(EnumOperator.IS_NOT_NULL.toString());
        ops.add(EnumOperator.IS_NULL.toString());
      }

    }
    return ops;
  }

  /**
   * Todo.
   * @return true if the query tree contains an actual query; false otherwise
   */
  public boolean isQueryTreeNonTrivial() {
    return queryTree.getChildCount() > 0
        && queryTree.getChildren().get(0) != null
        && queryTree.getChildren().get(0).getChildCount() > 0;
  }

  /**
   * Performs serialization of the Query.
   */
  public void serializeQuery() {
    Query query;
    if (isQueryTreeNonTrivial()) {
      query = QueryTreeUtil.treeToQuery(queryTree);
    } else {
      query = new Query();
    }

    String queryAsXml = "";
    try {
      queryAsXml = QueryConverter.queryToXml(query);
    } catch (JAXBException e) {
      logger.error("JAXB Exception while trying to convert query to xml.");
      return;
    }
    setSerializedQuery(queryAsXml);
  }

  /**
   * Restores the Query using the serialized string.
   */
  public void restoreQuery() {
    String serializedQuery = getSerializedQuery();
    if (serializedQuery != null && serializedQuery.length() > 0 && !serializedQuery
        .equalsIgnoreCase("null")) {
      logger.debug("restoring query and refreshing page. Serialized query is " + serializedQuery);
      queryTree = QueryTreeUtil.queryStringToTree(serializedQuery);
      if (queryTree.getChildCount() == 0) {
        logger.debug("Query Tree was empty. Adding Default Group.");
        QueryItem queryItem = new QueryItem();
        queryItem.setMdrId(CONJUNCTION_GROUP_ID);
        queryItem.setRoot(true);
        queryTree.addChild(queryItem);
      }
    }
  }

}
