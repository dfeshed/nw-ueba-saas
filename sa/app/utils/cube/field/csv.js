/**
 * @file  Comma-Separated-Values Field class.
 * Represents a field in a crossfilter of records whose values are of type CSV.
 * @public
 */
import Ember from 'ember';
import ENUM_DIM_TYPE from './enum-type';
import DefaultDim from './default';
import Filter from 'sa/utils/cube/filter/csv';

const { computed } = Ember;

// Regular expression used to parse CSV strings into arrays.
const _REGEX = /\s*,\s*/;

/**
 * Helper function to be used as a reduceAdd when creating a crossfilter group for a dimension whose values are
 * comma-separate value strings.
 * @param {String} propertyName The property name used by the dimension's getter.
 * @private
 */
function _makeReduceAddForCSVDimension(propertyName) {
  return function(p, v) {
    (v[propertyName] || '').split(_REGEX).forEach(function(val) {
      p[val] = (p[val] || 0) + 1; // increment counts
    });
    return p;
  };
}

/**
 * Helper function to be used as a reduceRemove when creating a crossfilter group for a dimension whose values are
 * comma-separate value strings.
 * @param {String} propertyName The property name used by the dimension's getter.
 * @private
 */
function _makeReduceRemoveForCSVDimension(propertyName) {
  return function(p, v) {
    (v[propertyName] || '').split(_REGEX).forEach(function(val) {
      p[val] = (p[val] || 0) - 1; // decrement counts
    });
    return p;
  };
}

/**
 * Helper function to be used as a reduceInitial when creating a crossfilter group for a dimension whose values are arrays.
 * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
 * @private
 */
function _reduceInitial() {
  return {};
}

export default DefaultDim.extend({
  type: ENUM_DIM_TYPE.CSV,

  filter: computed(function() {
    return Filter.create({ field: this });
  }),

  grouping: computed('dimension', function() {
    let prop = this.get('propertyName');
    return this.get('dimension')
      .groupAll()
      .reduce(
        _makeReduceAddForCSVDimension(prop),
        _makeReduceRemoveForCSVDimension(prop),
        _reduceInitial
      );
  }),

  /**
   * Generates an array of grouped values and their respective counts for a field whose values are comma-sep strings.
   * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
   * @type Object[]
   * @public
   */
  groups: computed('dimension', 'cube.results', function() {
    let results = this.get('cube.results');
    let totalCount = results && results.length;
    let groups = this.get('grouping').value();

    groups.all = function() {
      let newObject = [];
      let key;

      for (key in this) {
        if (this.hasOwnProperty(key) && key !== 'all') {
          newObject.push({
            key,
            value: this[key]
          });
        }
      }
      return newObject;
    };

    let all = groups.all();
    let maxCount = all.reduce(function(p, v) {
      return (p > v.value ? p : v.value);
    }, 1);
    let out = all.map(function(group) {
      return {
        key: group.key,
        value: group.value,
        valuePercent: group.value / totalCount,
        max: maxCount,
        maxPercent: group.value / maxCount
      };
    });
    let hash = {};

    out.forEach(function(item) {
      hash[item.key] = item;
    });
    out.hash = hash;
    return out;
  })
});
