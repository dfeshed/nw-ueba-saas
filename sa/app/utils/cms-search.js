import Ember from 'ember';
import computed from 'ember-computed-decorators';

const { Object: EmberObject, typeOf, isEmpty, isPresent, assert } = Ember;
const OPERATORS = { AND: 'AND', OR: 'OR' };
const ASSIGNMENT_TYPES = {
  EQUAL: 'EQUAL',
  IN: 'IN',
  BETWEEN: 'BETWEEN',
  GREATER_THAN: 'GREATER_THAN',
  GREATER_THAN_OR_EQUAL_TO: 'GREATER_THAN_OR_EQUAL_TO',
  IS_NOT_NULL: 'IS_NOT_NULL',
  IS_NULL: 'IS_NULL',
  LESS_THAN: 'LESS_THAN',
  LESS_THAN_OR_EQUAL_TO: 'LESS_THAN_OR_EQUAL_TO',
  LIKE: 'LIKE',
  NOT_BETWEEN: 'NOT_BETWEEN',
  NOT_EQUAL: 'NOT_EQUAL',
  NOT_IN: 'NOT_IN',
  NOT_LIKE: 'NOT_LIKE'
};

/**
 * A utility for constructing Live Search (CMS Search Service) request payload JSON objects
 * @public
 */
export default EmberObject.extend({
  operatorType: OPERATORS.AND,
  pageNumber: 0,
  pageSize: 50,
  sort: null,

  /**
   * Cf _ensureLogicalAndForText()
   * @property useImplicitAnd
   * @public
   */
  useImplicitAnd: true,

  init() {
    this._super(...arguments);
    this.set('expressions', []);
  },

  @computed('operatorType', 'expressions', 'textSearch')
  criteria(operatorType, expressions, textSearch) {
    const criteria = {
      operatorType
    };
    if (expressions.length) {
      criteria.expressions = expressions;
    }
    if (isPresent(textSearch)) {
      criteria.textSearch = textSearch;
    }

    return criteria;
  },

  /**
   * One of the problems with MongoDB is that it uses an implicit OR between free-text search terms. I.e., a search
   * for:
   *    content delivery networks
   * actually produces a search for the terms: content OR delivery OR networks. Adding more terms to a search,
   * then, has the impact of increasing the number of search results rather than reducing it.
   *
   * By wrapping single terms with double-quotes, we can ensure a logical AND between terms, for example:
   *    "content" "delivery" "networks"
   * produces a search for the terms: content AND delivery AND networks. Which is more in line with what the search
   * user expects.
   *
   * This method automatically adds these double quotes around terms, but not for any terms already wrapped in double-
   * quotes.
   * @method _ensureLogicalAndForText
   * @param text
   * @returns {*}
   * @private
   */
  _ensureLogicalAndForText(text) {
    if (isPresent(text) && this.get('useImplicitAnd') === true) {
      let adjustedQuery = '';

      // Split the query first by quotes then by spaces
      let tokens = text.split('"').map((value, index) => {
        return index % 2 ? value : value.split(' ');
      });

      // flatten the resulting array of tokens
      tokens = [].concat([], ...tokens).filter(Boolean);

      // trim and add quotes back in
      tokens.forEach((term) => {
        if (!isEmpty(term.trim())) {
          adjustedQuery += `"${term.trim()}" `;
        }
      });
      text = adjustedQuery;
    }
    return text;
  },

  /**
   *
   * @method setSearchCriteria
   * @public
   * @param criteria
   * @returns {Array}
   */
  setSearchCriteria(criteria = {}) {
    // TextSearch gets processed differently than other search criteria. Handle and delete so it's not created as an
    // expression.
    if (isPresent(criteria.textSearch)) {
      this.setTextSearch(criteria.textSearch);
      delete criteria.textSearch;
    }

    Object.keys(criteria).forEach((propertyName)=>{
      const value = criteria[propertyName];
      if (isPresent(value)) {
        const propertyValues = this._createPropertyValues(value);
        const assignmentType = propertyValues.length < 2 ? ASSIGNMENT_TYPES.EQUAL : ASSIGNMENT_TYPES.IN;
        this.addExpression(propertyName, propertyValues, assignmentType);
      }
    });
  },

  setUseImplicitAnd(useImplicitAnd = true) {
    this.set('useImplicitAnd', useImplicitAnd);
  },

  /**
   * Sets the free-text search for the search request.
   * @method setTextSearch
   * @public
   * @param textSearch
   */
  setTextSearch(textSearch) {
    this.set('textSearch', this._ensureLogicalAndForText(textSearch));
  },

  /**
   * Creates an array of property value objects from a string or array
   * @param value
   * @returns {Array}
   * @private
   * TODO: Account for numbers/dates
   */
  _createPropertyValues(values) {
    const propertyValues = [];
    if (typeOf(values) !== 'array') {
      values = [values];
    }
    values.forEach((item) => {
      propertyValues.push({
        valueType: 'STRING',
        value: item
      });
    });

    return propertyValues;
  },

  /**
   * Adds an expression to the search criteria. This can be done manually by calling this method, or automatically
   * by passing a hash to setSearchCriteria()
   * @method addExpression
   * @public
   * @param propertyName
   * @param propertyValues
   * @param assignmentType
   * @param caseInsensitive
   */
  addExpression(propertyName, propertyValues, assignmentType = 'EQUAL', caseInsensitive = true) {
    this.set('expressions', [...this.get('expressions'), {
      propertyName,
      propertyValues,
      assignmentType,
      caseInsensitive
    }]);
  },

  /**
   * Sets the page number requested for the search. The page number property is a zero-based index. Page one of a search
   * result is actually pageNumber === 0.
   * @method setPageNumber
   * @public
   * @param pageNumber number
   */
  setPageNumber(pageNumber = 0) {
    pageNumber = parseInt(pageNumber, 10);
    assert('setPageNumber() must be passed a numerical value', typeOf(pageNumber) === 'number');
    this.set('pageNumber', pageNumber);
  },

  /**
   * Sets the number of search results that are returned for a given page.
   * @method setPageSize
   * @public
   * @param pageSize number
   */
  setPageSize(pageSize = 50) {
    pageSize = parseInt(pageSize, 10);
    assert('setPageSize() must be passed a numerical value', typeOf(pageSize) === 'number');
    this.set('pageSize', pageSize);
  },

  /**
   * Sets the sorting instructions indicating which fields should used to sort the result set and which direction
   * the sort should use. The search result can be sorted by a maximum of two fields simultaneously.
   * @public
   * @param firstSort
   * @param secondSort
   */
  setSort(firstSort, secondSort) {
    let sortFields = [];

    [firstSort, secondSort].forEach((sortField) => {
      if (typeOf(sortField) === 'string') {
        const [ key, sortDirection = 'asc' ] = sortField.split('|'); // expected format columnName|sortdir (e.g., title|desc)

        if (isPresent(key)) {
          const [ fieldName ] = key.split('.'); // for columns with dots/notation, just grab the primary property name
          sortFields.push({ key: fieldName, descending: sortDirection !== 'asc' });
        }
      }
    });

    if (isEmpty(sortFields)) {
      sortFields = null;
    }
    this.set('sort', sortFields);
  },

  /**
   * Returns a JSON object that can be used as the payload in a CMS Search request
   * @method toJSON
   * @public
   * @returns {{pageNumber: *, pageSize: *, sort: *}}
   */
  toJSON() {
    const { criteria, ...searchRequest } = this.getProperties('pageNumber', 'pageSize', 'criteria', 'sort');

    // Only add the search criteria to the request if there is a text search or any other search expression involved
    // If no search criteria is added, the result will be a search of ALL results
    if (criteria.expressions || criteria.textSearch) {
      searchRequest.criteria = criteria;
    }

    return searchRequest;
  }
});