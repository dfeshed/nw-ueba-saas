/**
 * @file  Default Field class.
 * Represents a field in a cube of records whose values are of type DEFAULT.
 * @public
 */
import ENUM_DIM_TYPE from './enum-type';
import Ember from 'ember';
import Filter from 'sa/utils/cube/filter/default';

const {
  Object: EmberObject,
  computed
} = Ember;

/**
 * Generates a default getter for a given property name.  The getter takes as input a data record,
 * and returns the record's value under the given property name.
 * @param {String} propertyName The name of the property.
 * @returns {Function} The getter
 * @private
 */
function _defaultGetter(propertyName) {
  return function(d) {
    return d[propertyName];
  };
}

export default EmberObject.extend({
  type: ENUM_DIM_TYPE.DEFAULT,

  /**
   * The cube wrapper object which owns this field.
   * @type Object
   * @public
   */
  cube: null,

  /**
   * Name of the record property that this field corresponds to.
   * @type String
   * @public
   */
  propertyName: null,

  /**
   * Getter function that retrieves the corresponding value for this dimension from a data record.
   * If not specified, a default getter function is assumed; it will simply read a record's 'propertyName' property.
   * @type Function
   * @public
   */
  getter: null,

  /**
   * A native crossfilter dimension object that represents this field. The dimension can be used for filtering,
   * aggregation & sorting.
   * @readonly
   * @type Object
   * @default null
   * @public
   */
  dimension: computed('cube.crossfilter', 'getter', function() {
    const xfilter = this.get('cube.crossfilter'),
        getter = this.get('getter');
    return xfilter && getter && xfilter.dimension(getter);
  }),

  /**
   * A native crossfilter group object that represents the grouped values in this field. This object is used
   * to obtain counts of values for this field.
   * @readonly
   * @type Object
   * @public
   */
  grouping: computed('dimension', function() {
    return this.get('dimension').group();
  }),

  /**
   * Array of grouped values in this field and their counts. Each Array item is an Object with properties:
   * 'key' (String|Number): a field value found in the cube's records;
   * 'value (Number): the count of filtered records in the cube who have the field value specified by 'key';
   * 'valuePercent' (Number): the 'value' count divided by the count of filtered records in cube;
   * 'max' (String|Number): the highest count for any field value in the filtered records;
   * 'maxPercent' (Number): the 'value' count divided by the 'max' count.
   * @type Object[]
   * @default []
   * @public
   */
  groups: computed('grouping', 'cube.results', function() {
    let results = this.get('cube.results'),
        totalCount = results && results.length,
        all = this.get('grouping').all(),
            maxCount = all.reduce(function(p, v) {
              return (p > v.value ? p : v.value);
            }, 1),
        out = all.map(function(group) {
          return {
            key: group.key,
            value: group.value,
            valuePercent: totalCount ? group.value / totalCount : group.value,
            max: maxCount,
            maxPercent: maxCount ? group.value / maxCount : group.value
          };
        }),
        hash = {};
    out.forEach(function(item) {
      hash[item.key] = item;
    });
    out.hash = hash;
    return out;
  }),

  /**
   * Represents the filter currently applied to this field. An instance of sa/utils/cube/filter.
   * @type Object
   * @default null
   * @public
   */
  filter: computed(function() {
    return Filter.create({ field: this });
  }),

  /**
   * Applies defaults and instantiates the dimension object.
   * @public
   */
  init() {
    this._super(arguments);

    // Apply default getter, if needed.
    if (!this.get('getter')) {
      this.set('getter', _defaultGetter(this.get('propertyName')));
    }
  },

  /**
   * Disposes the dimension object to free up memory.
   * Wraps the crossfilter's dimension.dispose() call.
   * @public
   */
  destroy() {
    if (this.dimensionWasMade) {
      this.get('dimension').dispose();
      this.set('dimension', null);
    }
    this._super();
  }
});
