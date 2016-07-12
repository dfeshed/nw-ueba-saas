/**
 * @file Filter wrapper object for an Array dimension.
 * Represents a filter that can be applied to a Crossfilter dimension whose values are of type Array.
 * @public
 */
import Ember from 'ember';
import DefaultFilter from './default';

// Enumeration of filter types.
// @todo Don't duplicate this; instead, import it from './default'
const ENUM_TYPE = {
  EMPTY: 'EMPTY',
  EXACT: 'EXACT',
  LIST: 'LIST',
  RANGE: 'RANGE',
  FUNC: 'FUNC'
};

// Generates a function that will return true only if it is passed an array that contains at least one value from
// a given array.
function _makeArrayItemFinder(arr) {
  let hash = {};
  if (arr) {
    arr.forEach(function(item) {
      hash[item] = true;
    });
  }
  return function(val) {
    if (val && val.some) {
      return val.some(function(d) {
        return !!hash[d];
      });
    }
    return false;
  };
}

// Generates a function that will return true only if it is passed an array that contains a value within a given range.
function _makeArrayRangeFinder(fromVal, toVal) {
  return function(val) {
    if (val && val.some) {
      return val.some(function(d) {
        return (d >= fromVal) && (d < toVal);
      });
    }
    return false;
  };
}

// Generates a function that will return true only if it is passed an array that will cause a given function to return
// to when that given function is invoked with that array.
function _makeArrayFunctionFinder(filterFn) {
  return function(val) {
    if (val && val.some) {
      return val.some(filterFn);
    }
    return false;
  };
}

export default DefaultFilter.extend({

  /**
   * The representation of the 'value' attribute which the Crossfilter library will natively understand.
   * Either:
   * if value is null: null;
   * if value is a String, or a Number: a Function that returns true if passed an array that contains that value;
   * if value is an enumerated list: a Function that returns true when passed an array that contains any of the
   * values in the list;
   * if value is a range: a Function that returns true if passed an array that contains a value in that range;
   * if value is a function: the Function itself.
   * @type null | primitive | Array | Function
   * @public
   */
  native: Ember.computed('value', 'type', function() {
    let value = this.get('value');
    switch (this.get('type')) {
      case ENUM_TYPE.EMPTY:
        return null;
      case ENUM_TYPE.EXACT:
        return _makeArrayItemFinder([value]);
      case ENUM_TYPE.LIST:
        return _makeArrayItemFinder(value);
      case ENUM_TYPE.RANGE:
        return _makeArrayRangeFinder(value.from, value.to);
      case ENUM_TYPE.FUNC:
        return _makeArrayFunctionFinder(value);
    }
  }),

  includes(val) {
    switch (this.get('type')) {
      case ENUM_TYPE.EMPTY:
        return true;
      case ENUM_TYPE.EXACT:
      case ENUM_TYPE.LIST:
      case ENUM_TYPE.RANGE:
      case ENUM_TYPE.FUNC:
        return this.get('native')([val]);
    }
  }
});
