import EmberObject from '@ember/object';
import { isPresent, isEmpty } from '@ember/utils';
import { isArray, A } from '@ember/array';
import { resolveSinceWhenStartTime } from 'respond/utils/since-when-types';
import { assert } from 'ember-metal/utils';
import computed, { readOnly } from 'ember-computed-decorators';

/**
 * A class that represents a filter query
 * @class Query
 * @public
 */
const FilterQuery = EmberObject.extend({
  /**
   * Internal representation of the filters array.
   * @property _filters
   * @private
   */
  _filters: null,

  /**
   * The list of filter objects applied in the query. The most basic filter object has a field and value
   * property, but it can (instead of value) also have a values property (array), a range property, or an
   * isNull property
   * @property filters
   * @public
   */
  @readOnly
  @computed('_filters')
  filters(filters) {
    return filters.map((filter) => {
      // if values is only one item, pull out of array (some queries do not support values array property)
      if (filter.values && filter.values.length === 1) {
        filter.value = filter.values[0];
        delete filter.values;
      }
      return filter;
    });
  },

  /**
   * Maximum number of records returned by the filter query
   * @property limit
   * @public
   */
  limit: 1000,

  /**
   * A stream service will return the data in batches/chunks rather than all at once.
   * This property defines the size of the batch.
   * @property batch
   * @public
   */
  batch: 100,

  @computed('limit', 'batch')
  stream(limit, batch) {
    return {
      limit,
      batch
    };
  },

  /*
   * Init() bootstraps the filters property so that it is created instance by instance, and not on the
   * Object Prototype
   */
  init() {
    this._super(...arguments);
    this.set('_filters', A([]));
  },

  /**
   * Adds a filter object directly to the filters array. All convenience methods (addFilter, addRangeFilter, etc)
   * use this to update the filters array, and it provides direct access to consumers for custom filters
   * @param filter object
   * @public
   */
  appendFilter(filter) {
    if (isPresent(filter)) {
      const filters = this.get('_filters');
      this.set('_filters', [...filters, filter]);
    }
    return this;
  },

  /**
   * Provides support for adding a basic (single or multi-value) filter, but not a range or isNull/hasValue filter
   * @method addFilter
   * @public
   * @param field {string} The field name for the filter
   * @param value {string|number|Array} Either a primitive, single value or an array of values.
   */
  addFilter(field, value) {
    if (isEmpty(field) || isEmpty(value)) {
      return this; // noop if either argument is missing
    }
    let values = !isArray(value) ? [value] : value; // just always use values array instead of single value
    values = this._removeEmptyValues(values);

    if (isEmpty(values)) {
      return this; // noop if there are no values to filter by
    }
    const filter = this.getFilter(field);

    if (isPresent(filter)) {
      this._ammendFilter(filter, values);
    } else {
      this.appendFilter({ field, values });
    }

    return this;
  },

  /**
   * Provides support for simultaneously adding multiple, basic field/value filters e.g,
   *    addFilters(['firstName', 'Ignatius'], ['lastName', 'Reilly'], ['hometown', 'New Orleans')
   * @public
   * @returns {Query}
   */
  addFilters() {
    [...arguments].forEach(([field, value]) => {
      this.addFilter(field, value);
    });
    return this;
  },

  /**
   * Removes a filter by the field name. Note: this only removes the first filter found, so if there is more
   * than one filter with the same field name, then only the first retrieved will be removed
   * @method removeFilter
   * @public
   * @param field {string} The name of the field to remove
   */
  removeFilter(field) {
    const filter = this.getFilter(field);
    if (isPresent(filter)) {
      const filters = this.get('_filters');
      this.set('_filters', filters.without(filter));
    }
    return this;
  },

  /**
   * Adds a filter requiring that the results have some value (i.e., not be null) for the given field
   * @method addHasAnyValueFilter
   * @public
   * @param field {string} The name of the field which must have some value
   */
  addHasAnyValueFilter(field) {
    if (isEmpty(field)) {
      return this;
    }
    this.appendFilter({ field, isNull: false });
    return this;
  },

  /**
   * Adds a filter requiring that the results have no value (i.e., must be null) for the given field
   * @method addHasNoValueFilter
   * @public
   * @param field {string} The name of the field which must have no value
   */
  addHasNoValueFilter(field) {
    if (isEmpty(field)) {
      return this;
    }
    this.appendFilter({ field, isNull: true });
    return this;
  },

  /**
   * Provides support for adding a range filter for a given field (e.g., addRangeFilter('age', 18, 45))
   * @method addRangeFilter
   * @public
   * @param field
   * @param from
   * @param to
   * @param type {string} Either 'date' or 'numeric'
   */
  addRangeFilter(field, from, to, type = 'date') {
    if (isEmpty(field) || isEmpty(from)) {
      return this;
    }
    this.removeFilter(field); // remove a pre-existing filter on that field (if it exists)

    this.appendFilter({
      field,
      range: {
        from,
        to,
        type
      }
    });

    return this;
  },

  /**
   * Convenience method for adding a time range filter to the query. The user can supply an enumerated value
   * from SINCE_WHEN_TYPES_BY_NAME (e.g,, SINCE_WHEN_TYPES_BY_NAME.LAST_SEVEN_DAYS), and a range filter will be added
   * to the query representing a start timme of seven days ago
   *
   * Delegates to addRangeFilter()
   *
   * cf. since-when-types.js
   * @method addSinceWhenFilter
   * @public
   * @param field {string} The name of the field
   * @param sinceWhen {Object|string} since-when-type object reference or the string of the since-when object 'name'
   * @param returnTimestamp {boolean} This parameter helps with unit tests that need to retrieve precisely the time
   * (down to the millisecond) that was generated by the method. Default is false, which returns "this" for chaining.
   * @returns the resolved unix start time used in the range filter
   */
  addSinceWhenFilter(field, sinceWhen, returnTimestamp = false) {
    if (isEmpty(field)) {
      return this; // noop if we're missing an expected field
    }

    const startTimeUnix = resolveSinceWhenStartTime(sinceWhen);
    this.addRangeFilter(field, startTimeUnix);
    return returnTimestamp ? startTimeUnix : this;
  },

  /**
   * Returns the first filter found for the specified field name
   * @method getFilter
   * @public
   * @param field {string} The field name used to retrieve the filter
   * @returns {Object}
   */
  getFilter(field) {
    return this.get('_filters').findBy('field', field);
  },

  /**
   * Returns true if a filter exists with the specified field name
   * @method filterExistsForField
   * @public
   * @param field
   * @returns {boolean} True if any filter exists with that field name
   */
  filterExistsForField(field) {
    return isPresent(this.getFilter(field));
  },

  /**
   * Merges a value or set of values into a pre-existing values array
   * @param filter
   * @param newValues
   * @private
   */
  _ammendFilter(filter, newValues) {
    filter.values = [...filter.values, ...newValues];
  },

  /**
   * Removes null or undefined values from an (Ember.MutableArray) array
   * @param values
   * @private
   */
  _removeEmptyValues(values) {
    return values.without(undefined).without(null);
  },

  /**
   * Updates the sort by information using the sort-type. Currently only supports sorting by one field at a time,
   * but will eventually support multiple sort fields.
   * @method sortBy
   * @public
   * @param field
   * @param descending
   */
  addSortBy(field, descending = false) {
    if (isEmpty(field)) {
      return;
    }
    this.set('sort', { field, descending });
    return this;
  },

  /**
   * Constructs the json query object
   * @method toJSON
   * @public
   * @returns {{filters, sort}}
   */
  toJSON() {
    const sort = [this.get('sort')];
    assert('Sort cannot be left undefined', isPresent(sort));
    const filter = this.get('filters');
    const stream = this.get('stream');

    return {
      filter,
      sort,
      stream
    };
  }
});

export default FilterQuery;