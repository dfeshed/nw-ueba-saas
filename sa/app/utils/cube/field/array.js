/**
 * @file  Array Field class.
 * Represents a field in a crossfilter of records whose values are of type ARRAY.
 * @public
 */
import Ember from 'ember';
import ENUM_DIM_TYPE from './enum-type';
import DefaultDim from './default';
import Filter from 'sa/utils/cube/filter/array';

const { computed } = Ember;

/**
 * Helper function to be used as a reduceAdd when creating a crossfilter group for a dimension whose values are arrays.
 * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
 * @param {String} propertyName The property name used by the dimension's getter.
 * @private
 */
function _makeReduceAddForArrayDimension(propertyName) {
  return function(p, v) {
    (v[propertyName] || []).forEach(function(val) {
      p[val] = (p[val] || 0) + 1; // increment counts
    });
    return p;
  };
}

/**
 * Helper function to be used as a reduceRemove when creating a crossfilter group for a dimension whose values are arrays.
 * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
 * @param {String} propertyName The property name used by the dimension's getter.
 * @private
 */
function _makeReduceRemoveForArrayDimension(propertyName) {
  return function(p, v) {
    (v[propertyName] || []).forEach(function(val) {
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
  type: ENUM_DIM_TYPE.ARRAY,

  filter: computed(function() {
    return Filter.create({ field: this });
  }),

  grouping: computed('dimension', function() {
    const prop = this.get('propertyName');
    return this.get('dimension')
            .groupAll()
            .reduce(
              _makeReduceAddForArrayDimension(prop),
              _makeReduceRemoveForArrayDimension(prop),
              _reduceInitial
            );

  }),

  /**
   * Generates an array of grouped values and their respective counts for a field whose values are arrays.
   * (See: http://stackoverflow.com/questions/17524627/is-there-a-way-to-tell-crossfilter-to-treat-elements-of-array-as-separate-record)
   * @type Object[]
   * @public
   */
  groups: computed('grouping', 'cube.results', function() {
    const results = this.get('cube.results');
    const totalCount = results && results.length;
    const groups = this.get('grouping').value();

    groups.all = function() {
      const newObject = [];
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

    const all = groups.all();
    const maxCount = all.reduce(function(p, v) {
      return (p > v.value ? p : v.value);
    }, 1);
    const out = all.map(function(group) {
      return {
        key: group.key,
        value: group.value,
        valuePercent: totalCount ? group.value / totalCount : group.value,
        max: maxCount,
        maxPercent: maxCount ? group.value / maxCount : group.value
      };
    });
    const hash = {};

    out.forEach(function(item) {
      hash[item.key] = item;
    });
    out.hash = hash;
    return out;
  })
});
