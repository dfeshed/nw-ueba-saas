/**
 * @file Filter wrapper object.
 * Represents a filter that can be applied to a Crossfilter dimension.
 * @public
 */
import Ember from 'ember';

// Enumeration of filter types.
const ENUM_TYPE = {
  EMPTY: 'EMPTY',
  EXACT: 'EXACT',
  LIST: 'LIST',
  RANGE: 'RANGE',
  FUNC: 'FUNC'
};

// Determines the filter type of a given filter value.
function _typeOfFilterValue(val) {
  if (val === null) {
    return ENUM_TYPE.EMPTY;
  }
  let t = (typeof val);
  if (t === 'function') {
    return ENUM_TYPE.FUNC;
  } else if ((t === 'object') && val.hasOwnProperty('from') && val.hasOwnProperty('to')) {
    return ENUM_TYPE.RANGE;
  } else if ((t === 'object') && val.hasOwnProperty('length')) {
    return ENUM_TYPE.LIST;
  } else {
    return ENUM_TYPE.EXACT;
  }
}

// Generates a function that will return true only if it is passed a value from a given array.
function _makeArrayItemFinder(arr) {
  let hash = {};
  if (arr) {
    arr.forEach(function(item) {
      hash[item] = true;
    });
  }
  return function(val) {
    return !!hash[val];
  };
}

export default Ember.Object.extend({

  /**
   * The cube field object that owns this filter instance.
   * An instance of a sa/utils/cube/field/* class.
   * @type Object
   * @public
   */
  field: null,

  /**
   * Indicates the value of the current filter. Either:
   * null: indicates an empty filter (i.e., all values are included in the filtered results);
   * String or Number primitive: indicates an exact value filter;
   * {from: *, to: *}: indicates a range of primitive values;
   * Array: indicates an enumerated list of primitive values;
   * Function: indicates a function which takes a field value and returns true if it passes the filter.
   * @type null | primitive | {from: *, to: *} | Array | Function
   * @public
   */
  value: null,

  /**
   * The flavor of the current filter 'value' attribute.  (Not to be confused with javascript's 'typeof'.)
   * @type String ('EMPTY' | 'EXACT' | 'LIST' | 'RANGE' | 'FUNC')
   * @public
   */
  type: Ember.computed('value', function() {
    return _typeOfFilterValue(this.get('value'));
  }),

  /**
   * The representation of the 'value' attribute which the Crossfilter library will natively understand.
   * Either:
   * if value is null, a String, or a Number: the value itself;
   * if value is a range: an Array of the form [from, to];
   * if value is an enumerated list: a Function that returns true when passed in any of the values in the list;
   * if value is a function: the Function itself.
   * @type null | primitive | Array | Function
   * @public
   */
  native: Ember.computed('value', 'type', function() {
    let value = this.get('value');
    switch (this.get('type')) {
      case ENUM_TYPE.EMPTY:
      case ENUM_TYPE.EXACT:
      case ENUM_TYPE.FUNC:
        return value;
      case ENUM_TYPE.RANGE:
        return [value.from, value.to];
      case ENUM_TYPE.LIST:
        return _makeArrayItemFinder(value);
    }
  }),

  /**
   * Resets the filter value to a given value.
   * @param {*} val The new filter value.
   * @returns {Object} this instance, for chaining.
   * @public
   */
  reset(val) {
    if (typeof val === 'undefined') {
      val = null;
    }
    this.set('value', val);
    return this;
  },

  /**
   * Tries to add a given value to the existing filter. This is only supported in cases when the given value
   * and the existing filter value are of compatible types. (For example, EXACT or LIST values can be added to EXACT
   * or LIST values, but FUNC values cannot be added together.) For the unsupported cases, this method will simply
   * reset the filter to the given value.
   * @param {*} val The filter value to be added.
   * @returns {Object} this instance, for chaining.
   * @public
   */
  add(val) {
    if (val == null) {
      return this;
    }

    let addType = _typeOfFilterValue(val),
        curr = this.get('value');
    switch (this.get('type')) {
      case ENUM_TYPE.EXACT:
        if (val !== curr) {
          return this.reset([curr, val]);
        }
        break;
      case ENUM_TYPE.EMPTY:
      case ENUM_TYPE.FUNC:
        // Adding to functions is not supported yet; treat as a reset.
        return this.reset(val);
      case ENUM_TYPE.LIST:
        switch (addType) {
          case ENUM_TYPE.EXACT:
            val = [val];
            /* jshint -W086 */
            // fall through to LIST case
          case ENUM_TYPE.LIST:
            /* jshint +W086 */
            let arr = [].concat(curr),
              changed = false;
            val.forEach(function(item) {
              if (arr.indexOf(item) === -1) {
                arr.push(item);
                changed = true;
              }
            });
            if (changed) {
              return this.reset(arr);
            }
        }
        break;
      case ENUM_TYPE.RANGE:
        if (addType === ENUM_TYPE.RANGE) {
          if (val.from === curr.to) {
            return this.reset({ from: curr.from, to: val.to });
          } else if (val.to === curr.from) {
            return this.reset({ from: val.from, to: curr.to });
          } else {
            return this.reset(val);     // disjointed ranges are not supported yet
          }
        } else {
          return this.reset(val); // adding range + non-range is not supported yet
        }
        break;
    }
    return this;
  },

  /**
   * Tries to remove a given value to the existing filter. This is only supported in cases when the given value
   * and the existing filter value are of compatible types. (For example, EXACT values can be removed from EXACT
   * or LIST values, but FUNC values cannot be removed from each other.) For the unsupported cases, this method will
   * simply reset the filter to the given value.
   * @param {*} val The filter value to be removed.
   * @returns {Object} this instance, for chaining.
   * @public
   */
  remove(val) {
    if (val === null) {
      return this;
    }

    let removeType = _typeOfFilterValue(val),
        curr = this.get('value');
    switch (this.get('type')) {
      case ENUM_TYPE.EMPTY:
        return this;
      case ENUM_TYPE.EXACT:
        if (val === curr) {
          return this.reset(null);
        }
        break;
      case ENUM_TYPE.LIST:
        switch (removeType) {
          case ENUM_TYPE.EXACT:
            val = [val];
            /* jshint -W086 */
            // fall through to LIST case
          case ENUM_TYPE.LIST:
            /* jshint +W086 */
            let arr = [].concat(curr);
            val.forEach(function(item) {
              let index = arr.indexOf(item);
              if (index > -1) {
                arr.splice(index, 1);
              }
            });
            switch (arr.length) {
              case 0:
                return this.reset(null);
              case 1:
                return this.reset(arr[0]);
              default:
                return this.reset(arr);
            }
            break;
          default:
            return this.reset(null);    // removing ranges or functions from lists isn't supported yet
        }
        break;
      default:
        return this.reset(null);    // removing from ranges or functions isn't supported yet
    }
    return this;
  },

  /**
   * Returns true if a given field value is included in the current filter.
   * @param {*} val The field value to be tested.
   * @returns {Boolean} True if the value is included, false otherwise.
   * @public
   */
  includes(val) {
    let value = this.get('value');
    switch (this.get('type')) {
      case ENUM_TYPE.EMPTY:
        return true;
      case ENUM_TYPE.EXACT:
        return val === value;
      case ENUM_TYPE.LIST:
        return value.indexOf(val) > -1;
      case ENUM_TYPE.RANGE:
        return (val >= value.from) && (val < value.to);
      case ENUM_TYPE.FUNC:
        return !!value(val);
    }
  }
});
