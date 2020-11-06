
package de.samply.share.common.utils;

import de.samply.share.common.model.uiquerybuilder.EnumConjunction;
import de.samply.share.common.model.uiquerybuilder.EnumOperator;
import de.samply.share.common.model.uiquerybuilder.QueryItem;
import de.samply.share.model.common.And;
import de.samply.share.model.common.Attribute;
import de.samply.share.model.common.Between;
import de.samply.share.model.common.ConditionType;
import de.samply.share.model.common.Eq;
import de.samply.share.model.common.Geq;
import de.samply.share.model.common.Gt;
import de.samply.share.model.common.In;
import de.samply.share.model.common.IsNotNull;
import de.samply.share.model.common.IsNull;
import de.samply.share.model.common.Leq;
import de.samply.share.model.common.Like;
import de.samply.share.model.common.Lt;
import de.samply.share.model.common.MultivalueAttribute;
import de.samply.share.model.common.Neq;
import de.samply.share.model.common.ObjectFactory;
import de.samply.share.model.common.Or;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.RangeAttribute;
import de.samply.share.model.common.Where;
import de.samply.share.utils.QueryConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.omnifaces.model.tree.ListTreeModel;
import org.omnifaces.model.tree.TreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Offer Methods to help with query to tree conversion and vice versa.
 */
public class QueryTreeUtil {

  /**
   * The Constant logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(QueryTreeUtil.class);

  /**
   * The Constant CONJUNCTION_GROUP.
   */
  private static final String CONJUNCTION_GROUP = "conjunctionGroup";

  /**
   * Prohibit class instantiation since it only offers static methods.
   */
  private QueryTreeUtil() {
  }

  /**
   * Take a query criteria string (most likely from the database) and transform it into a tree to
   * show on a jsf page.
   * As of now, this uses the OSSE namespace.
   *
   * @param queryString the serialized query
   * @return the tree model of the query
   */
  public static TreeModel<QueryItem> queryStringToTree(String queryString) {
    Query query = new Query();
    Where where = new Where();
    query.setWhere(where);

    if (queryString == null || queryString.length() < 1) {
      logger.error("Error...empty query String was submitted...");
      return queryToTree(query);
    }
    try {
      query = SamplyShareUtils.unmarshal(queryString,
          JAXBContext.newInstance(Query.class),
          Query.class);
    } catch (JAXBException e) {
      logger.debug("JAXB Error while converting querystring to tree. Returning empty query");
    }
    return queryToTree(query);
  }

  /**
   * Take a query object and transform it into a tree to show on a jsf page.
   * As of now, this uses the OSSE namespace.
   *
   * @param query the query
   * @return the tree model of the query
   */
  public static TreeModel<QueryItem> queryToTree(Query query) {
    TreeModel<QueryItem> criteriaTree = new ListTreeModel<>();

    Where where = query.getWhere();
    if (where != null) {
      for (int i = 0; i < where.getAndOrEqOrLike().size(); ++i) {
        criteriaTree = visitNode(criteriaTree, where.getAndOrEqOrLike().get(i));
      }
    }
    return criteriaTree;
  }

  /**
   * Check if the queryTree has at least one Attribute set.
   *
   * @param node the root node of the query tree
   * @return true if the queryTree contains at least one Attribute, false otherwise
   */
  public static boolean isQueryValid(TreeModel<QueryItem> node) {
    if (node.isLeaf()) {
      String mdrId = node.getData().getMdrId();
      return mdrId != null && mdrId.contains(":dataelement:");
    } else {
      boolean hasValidChild = false;
      for (TreeModel<QueryItem> child : node.getChildren()) {
        if (isQueryValid(child)) {
          hasValidChild = true;
        }
      }
      return hasValidChild;
    }
  }

  /**
   * Transform a query tree to a query object.
   * As of now, this uses the OSSE namespace.
   *
   * @param queryTree the tree model, containing the query
   * @return the query itself
   */
  public static Query treeToQuery(TreeModel<QueryItem> queryTree) {
    Query query = new Query();
    Where where = new Where();

    where = (Where) visitNodeAndAppendToQuery(queryTree, where);
    query.setWhere(where);
    try {
      logger.debug(QueryConverter.queryToXml(query));
    } catch (JAXBException e) {
      logger.warn("Error caught while trying to serialize query. Returning empty query", e);
    }

    return query;
  }

  /**
   * Transform a serialized condition (and/or) object to a treenode.
   *
   * @param conditionTypeString serialized form of the condition type
   * @return the treenode representation of the condition
   */
  public static TreeModel<QueryItem> conditionTypeStringToTreenode(String conditionTypeString) {
    if (conditionTypeString == null || conditionTypeString.length() < 1) {
      logger.warn("Condition Type String is empty or null...bailing out");
      return new ListTreeModel<>();
    }

    try {
      And and = SamplyShareUtils.unmarshal(conditionTypeString,
          JAXBContext.newInstance(And.class),
          And.class);
      return conditionTypeToTreeNode(and);
    } catch (JAXBException e) {
      logger.debug("JAXB Error while trying to unmarshall as AND. Trying OR now...");
      try {
        Or or = SamplyShareUtils.unmarshal(conditionTypeString,
            JAXBContext.newInstance(Or.class),
            Or.class);
        return conditionTypeToTreeNode(or);
      } catch (JAXBException e1) {
        logger
            .error("JAXB Error: Could not convert String to ConditionType: " + conditionTypeString);
      }
    }
    return new ListTreeModel<>();
  }

  /**
   * Transform a condition (and/or) object to a treenode.
   *
   * @param conditionType condition type object
   * @return the treenode representation of the condition
   */
  public static TreeModel<QueryItem> conditionTypeToTreeNode(ConditionType conditionType) {
    EnumConjunction conjunctionType;

    if (conditionType.getClass() == And.class) {
      conjunctionType = EnumConjunction.AND;
    } else if (conditionType.getClass() == Or.class) {
      conjunctionType = EnumConjunction.OR;
    } else {
      // This should never be reached
      conjunctionType = EnumConjunction.AND;
    }

    QueryItem qi = new QueryItem();
    qi.setConjunction(conjunctionType);
    qi.setMdrId("conjunctionGroup");
    qi.setRoot(false);
    TreeModel<QueryItem> treeNode = new ListTreeModel<>();
    TreeModel<QueryItem> newNode = new ListTreeModel<>();
    newNode = treeNode.addChild(qi);
    for (int i = 0; i < conditionType.getAndOrEqOrLike().size(); ++i) {
      newNode = visitNode(newNode, conditionType.getAndOrEqOrLike().get(i));
    }
    return treeNode;
  }

  /**
   * Check a tree node and add its data to the query.
   *
   * @param node   the treenode to visit
   * @param target the condition object where the content of the treenode will be appended
   * @return the condition object with appended new data
   */
  private static Object visitNodeAndAppendToQuery(TreeModel<QueryItem> node, ConditionType target) {
    if (node.isRoot()) {
      logger.trace("Visiting root");
      Where where = (Where) target;
      for (TreeModel<QueryItem> queryItem : node.getChildren()) {
        where = (Where) visitNodeAndAppendToQuery(queryItem, where);
      }
      return target;
    } else {
      logger.trace("visiting " + node.getIndex() + " - target: " + target.getClass());

      if (node.isLeaf()) {

        if (target.getClass() == Where.class) {
          Where where = new Where();
          for (TreeModel<QueryItem> qi : node.getChildren()) {
            where = (Where) visitNodeAndAppendToQuery(qi, where);
          }
          return target;
        }

        QueryItem queryItem = node.getData();
        String operatorString = "";
        if (queryItem.getOperator() == null) {
          logger.trace("operator is null");
        } else {
          operatorString = queryItem.getOperator().toString();
        }
        Attribute attribute = new Attribute();
        attribute.setMdrKey(queryItem.getMdrId());

        String dateString = queryItem.getValue();
        if (dateString != null) {
          try {
            dateString = SamplyShareUtils.convertDateStringToString(queryItem.getValue(),
                new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH),
                new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH));
          } catch (ParseException e) {
            // ParseException can be safely ignored here (meaning it is just not a date value)
          }
        }
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<String> jaxbElement = objectFactory.createValue(dateString);
        attribute.setValue(jaxbElement);

        if (operatorString.equalsIgnoreCase(EnumOperator.EQUAL.toString())) {
          Eq eq = new Eq();
          eq.setAttribute(attribute);
          target.getAndOrEqOrLike().add(eq);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.GREATER.toString())) {
          Gt gt = new Gt();
          gt.setAttribute(attribute);
          target.getAndOrEqOrLike().add(gt);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.GREATER_OR_EQUAL.toString())) {
          Geq geq = new Geq();
          geq.setAttribute(attribute);
          target.getAndOrEqOrLike().add(geq);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.LESS_THEN.toString())) {
          Lt lt = new Lt();
          lt.setAttribute(attribute);
          target.getAndOrEqOrLike().add(lt);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.LESS_OR_EQUAL_THEN.toString())) {
          Leq leq = new Leq();
          leq.setAttribute(attribute);
          target.getAndOrEqOrLike().add(leq);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.LIKE.toString())) {
          // Check if the value is enclosed in wildcard characters. If not, add them.
          // Currently, queries coming from central search do NOT contain wildcards.
          // If they change that, this should handle it.
          String oldValue = attribute.getValue().getValue();
          StringBuilder newValue = new StringBuilder();
          if (!oldValue.startsWith("%")) {
            newValue.append("%");
          }
          newValue.append(oldValue);
          if (!oldValue.endsWith("%")) {
            newValue.append("%");
          }
          attribute.setValue(objectFactory.createValue(newValue.toString()));
          Like like = new Like();
          like.setAttribute(attribute);
          target.getAndOrEqOrLike().add(like);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.NOT_EQUAL_TO.toString())) {
          Neq neq = new Neq();
          neq.setAttribute(attribute);
          target.getAndOrEqOrLike().add(neq);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.IS_NOT_NULL.toString())) {
          IsNotNull inn = new IsNotNull();
          inn.setMdrKey(attribute.getMdrKey());
          target.getAndOrEqOrLike().add(inn);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.IS_NULL.toString())) {
          IsNull in = new IsNull();
          in.setMdrKey(attribute.getMdrKey());
          target.getAndOrEqOrLike().add(in);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.BETWEEN.toString())) {
          RangeAttribute rangeAttr = new RangeAttribute();
          rangeAttr.setMdrKey(queryItem.getMdrId());

          String lowerBoundString = queryItem.getLowerBound();
          try {
            lowerBoundString = SamplyShareUtils.convertDateStringToString(queryItem.getLowerBound(),
                new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH),
                new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH));
          } catch (ParseException e) {
            // Parse Exception can be safely ignored here
          }

          String upperBoundString = queryItem.getUpperBound();
          try {
            upperBoundString = SamplyShareUtils.convertDateStringToString(queryItem.getUpperBound(),
                new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH),
                new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH));
          } catch (ParseException e) {
            // Parse Exception can be safely ignored here
          }
          rangeAttr.setLowerBound(lowerBoundString);
          rangeAttr.setUpperBound(upperBoundString);
          Between between = new Between();
          between.setRangeAttribute(rangeAttr);

          target.getAndOrEqOrLike().add(between);
        } else if (operatorString.equalsIgnoreCase(EnumOperator.IN.toString())) {
          In in = new In();
          MultivalueAttribute mvAttr = new MultivalueAttribute();
          mvAttr.setMdrKey(queryItem.getMdrId());

          for (String value : queryItem.getValues()) {
            jaxbElement = objectFactory.createValue(value);
            mvAttr.getValue().add(jaxbElement);
          }

          in.setMultivalueAttribute(mvAttr);
          target.getAndOrEqOrLike().add(in);
        }

        return target;
      } else if (node.getData().getMdrId().equalsIgnoreCase(CONJUNCTION_GROUP)) {
        QueryItem queryItem = node.getData();

        if (queryItem.getConjunction().equals(EnumConjunction.OR)) {
          Or or = new Or();
          for (TreeModel<QueryItem> qi : node.getChildren()) {
            or = (Or) visitNodeAndAppendToQuery(qi, or);
          }
          target.getAndOrEqOrLike().add(or);
        } else {
          // Use AND as default as well
          And and = new And();
          for (TreeModel<QueryItem> qi : node.getChildren()) {
            and = (And) visitNodeAndAppendToQuery(qi, and);
          }
          target.getAndOrEqOrLike().add(and);
        }

        return target;
      }
    }
    logger.error("This should never be reached");
    return null;
  }

  /**
   * Todo.
   * @param parentNode Todo.
   * @param obj Todo.
   * @return Todo.
   */
  private static TreeModel<QueryItem> visitNode(TreeModel<QueryItem> parentNode, Object obj) {

    QueryItem qi = new QueryItem();
    TreeModel<QueryItem> newNode = new ListTreeModel<>();

    if (obj.getClass() == And.class) {
      qi.setConjunction(EnumConjunction.AND);
      qi.setMdrId("conjunctionGroup");
      qi.setRoot(parentNode.isRoot());
      newNode = parentNode.addChild(qi);
      for (int j = 0; j < ((And) obj).getAndOrEqOrLike().size(); ++j) {
        newNode = visitNode(newNode, ((And) obj).getAndOrEqOrLike().get(j));
      }
    } else if (obj.getClass() == Or.class) {
      qi.setConjunction(EnumConjunction.OR);
      qi.setMdrId("conjunctionGroup");
      qi.setRoot(parentNode.isRoot());
      newNode = parentNode.addChild(qi);
      for (int k = 0; k < ((Or) obj).getAndOrEqOrLike().size(); ++k) {
        newNode = visitNode(newNode, ((Or) obj).getAndOrEqOrLike().get(k));
      }
    } else if (obj.getClass() == Eq.class) {
      Eq eq = (Eq) obj;
      qi.setMdrId(eq.getAttribute().getMdrKey());
      qi.setValue(eq.getAttribute().getValue().getValue());
      qi.setOperator(EnumOperator.EQUAL);
      parentNode.addChild(qi);

    } else if (obj.getClass() == Geq.class) {
      Geq geq = (Geq) obj;
      qi.setMdrId(geq.getAttribute().getMdrKey());
      qi.setValue(geq.getAttribute().getValue().getValue());
      qi.setOperator(EnumOperator.GREATER_OR_EQUAL);
      parentNode.addChild(qi);

    } else if (obj.getClass() == Leq.class) {
      Leq leq = (Leq) obj;
      qi.setMdrId(leq.getAttribute().getMdrKey());
      qi.setValue(leq.getAttribute().getValue().getValue());
      qi.setOperator(EnumOperator.LESS_OR_EQUAL_THEN);
      parentNode.addChild(qi);

    } else if (obj.getClass() == Gt.class) {
      Gt gt = (Gt) obj;
      qi.setMdrId(gt.getAttribute().getMdrKey());
      qi.setValue(gt.getAttribute().getValue().getValue());
      qi.setOperator(EnumOperator.GREATER);
      parentNode.addChild(qi);

    } else if (obj.getClass() == Lt.class) {
      Lt lt = (Lt) obj;
      qi.setMdrId(lt.getAttribute().getMdrKey());
      qi.setValue(lt.getAttribute().getValue().getValue());
      qi.setOperator(EnumOperator.LESS_THEN);
      parentNode.addChild(qi);

    } else if (obj.getClass() == Like.class) {
      Like like = (Like) obj;
      qi.setMdrId(like.getAttribute().getMdrKey());
      String val = like.getAttribute().getValue().getValue();
      if (val != null && val.length() > 2 && val.startsWith("%") && val.endsWith("%")) {
        val = val.substring(1, val.length() - 1);
      }
      qi.setValue(val);
      qi.setOperator(EnumOperator.LIKE);
      parentNode.addChild(qi);

    } else if (obj.getClass() == Neq.class) {
      Neq neq = (Neq) obj;
      qi.setMdrId(neq.getAttribute().getMdrKey());
      qi.setValue(neq.getAttribute().getValue().getValue());
      qi.setOperator(EnumOperator.NOT_EQUAL_TO);
      parentNode.addChild(qi);

    } else if (obj.getClass() == IsNull.class) {
      IsNull isNull = (IsNull) obj;
      qi.setMdrId(isNull.getMdrKey());
      qi.setOperator(EnumOperator.IS_NULL);
      parentNode.addChild(qi);

    } else if (obj.getClass() == IsNotNull.class) {
      IsNotNull isNotNull = (IsNotNull) obj;
      qi.setMdrId(isNotNull.getMdrKey());
      qi.setOperator(EnumOperator.IS_NOT_NULL);
      parentNode.addChild(qi);

    } else if (obj.getClass() == Between.class) {
      Between between = (Between) obj;
      qi.setMdrId(between.getRangeAttribute().getMdrKey());
      qi.setLowerBound(between.getRangeAttribute().getLowerBound());
      qi.setUpperBound(between.getRangeAttribute().getUpperBound());
      qi.setOperator(EnumOperator.BETWEEN);
      parentNode.addChild(qi);

    } else if (obj.getClass() == In.class) {
      In in = (In) obj;
      qi.setMdrId(in.getMultivalueAttribute().getMdrKey());
      qi.setIn(in.getMultivalueAttribute());
      qi.setOperator(EnumOperator.IN);
      parentNode.addChild(qi);

    }
    return parentNode;
  }


}
