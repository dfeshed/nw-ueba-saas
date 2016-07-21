/**
 * @file  Base Cube class.
 * A wrapper for Square's crossfilter library. A Cube instance observes a given Array instance. The Cube will populate
 * itself with the Array's data contents, and exposes a 'results' attribute which applies configurable sort &
 * filter to that data.  The sort & filter are configured by using the Cube's public API.  The Cube attaches listeners
 * to the Array, so that it can update its results automatically as the Array's contents change.
 * @public
 */
/* global crossfilter */
import Ember from 'ember';
import ENUM_FIELD_TYPE from './field/enum-type';
import DefaultField from './field/default';
import CsvField from './field/csv';
import ArrayField from './field/array';

const {
  setProperties,
  Object: EmberObject,
  computed
} = Ember;

// Method for finding a record in an array by a property value.
// Ember provides something similar called `Array.findBy()` but it returns the object, not the index.
// We want the index, and we want to find it quickly because the array might be large, so we do it here
// the quick & dirty way without callbacks. It's verbose but it will perform well!
function indexOfBy(key, value) {
  let index = -1,
    len = this.length,
    i;
  if (len) {
    if (this[0].get) {
      for (i = 0; i < len; i++) {
        if (this[i].get(key) === value) {
          index = i;
          break;
        }
      }
    } else {
      for (i = 0; i < len; i++) {
        if (this[i][key] === value) {
          index = i;
          break;
        }
      }
    }
  }
  return index;
}

// Method for editing a record in an array in a cube-friendly way.
// This method "edits" an array item by (1) removing it from the array, (2) applying the requested changes to its
// properties, and then (3) re-inserting the item back into the array at the same index.
// If the record is not found in the array already, does nothing.
// @assumes The `this` context will be the array when this method is called.
// @assumes The array records are javascript objects (either JSON or Ember Objects), not primitive scalars.
// @assumes Only one record will be changed; does not look for duplicates of the record within the array.
// @param {object|String} record Either the array item to be edited, or the value of its id field.
// @param {object} [delta] A hash of key-value pairs to be applied to the record.
// @returns {object} The modified record, if successful.
function edit(record, delta, remove=false) {
  // Find the index for the specified record.
  let index = -1;
  if (record !== null) {
    if (typeof record === 'object') {

      // If an object was given, find it as usual.
      index = this.indexOf(record);
    } else if (this.get('idField')) {

      // If an id was given, find the object with the matching id.
      index = this.indexOfBy(this.get('idField'), record);
      record = (index > -1) ? this[index] : null;
    }
  }

  if (index > -1) {

    // Apply the given changes to the record, if any.
    if (typeof delta === 'object') {
      if (record.setProperties) {
        record.setProperties(delta);
      } else {
        setProperties(record, delta);
      }
    }

    // Remove & reinsert the record using Ember's KVO-compliant MutableArray.replace() method.
    if (remove) {
      this.removeAt(index, 1);
    } else {
      this.replace(index, 1, [record]);
    }

  }
  return record;
}

// Utility that instantiates a new field class instance for a given field config.
function newFieldObject(cube, key, config) {
  let whichClass;
  config = config || {};
  config.propertyName = config.propertyName || key;
  config.cube = cube;
  switch (config && config.type) {
    case ENUM_FIELD_TYPE.ARRAY:
      whichClass = ArrayField;
      break;
    case ENUM_FIELD_TYPE.CSV:
      whichClass = CsvField;
      break;
    default:
      whichClass = DefaultField;
  }
  return whichClass.create(config);
}

export default EmberObject.extend({

  /**
   * Master array of data objects for this crossfilter to filter/aggregate/sort.
   * This cube instance will attach listeners to the array and update itself when the array's contents are
   * modified. A reference to the array should be passed into the constructor of this cube instance, and cannot be
   * subsequently changed to point to a different array.  To cube-ify a different array, instantiate another cube.
   * @readonly
   * @type Object[]
   * @default []
   * @public
   */
  records: computed(function() {
    return this._array;
  }).readOnly(),

  /**
   * Hash of field objects. Each field object represents a field in the data records. A field object is an instance
   * of one of the cube/field/ classes (e.g., DefaultField, CsvField, or ArrayField).  These fields can be
   * used for sorting, filtering and aggregation.  The 'fields' hash is derived at run-time from the 'fieldsConfig'
   * property. For more details, see 'fieldsConfig' docs.
   * @type {}
   * @readonly
   * @public
   */
  fields: computed(function() {
    return this._fields;
  }).readOnly(),

  /**
   * Reference to the native Crossfilter instance wrapped by this cube. This object is instantiated and set once
   * during init, and cannot be swapped out with another Crossfilter instance.
   * @type {}
   * @readonly
   * @public
   */
  crossfilter: computed(function() {
    return this._crossfilter;
  }).readOnly(),

  /**
   * Configurable metadata hash for the fields of 'records'. Used to define dimensions for crossfilter.
   * Each field can represent either an actual property of the objects in 'records' or a computed property of those
   * objects.  (For example, if records have 'createdDate' property, then the computed field 'age' could be defined
   * by using a getter that computes an age number from the 'createdDate' property).
   * The keys of the 'records' hash are names of fields; the hash values are each an Object with the following
   * properties:
   * 'propertyName': {String} optional name of the property that this field maps to; used for non-computed fields.
   * 'getter': (Function) optional function for reading the field value; required if 'key' is not specified.
   * 'type': optional; one of the enumerated values in enum-type. Default type = DEFAULT.
   * @type Object[]
   * @default {}
   * @public
   */
  fieldsConfig: null,

  /**
   * The field which uniquely identifies records.
   * This field is used to find a record during remove/edit operations.
   * @type String
   * @default 'id'
   * @public
   */
  idField: 'id',

  /**
   * The field by which to sort the results array.
   * If the name of a record property is given which is not found in the 'fields' hash, then a field is
   * automatically added for that property.
   * If not provided, the `idField` will be used by default.
   * @type String
   * @public
   */
  sortField: null,

  /**
   * If true, indicates that the sort order is descending. Otherwise it is descending.
   * @type Boolean
   * @default true
   * @public
   */
  sortDesc: true,

  /**
   * Subset of 'records' that matches the current filter criteria, sorted by the current sort configuration attrs.
   * This computed property fetches sorted results from the corresponding dimension object of the current sort field.
   * @type Object[]
   * @public
   */
  results: computed('lastRecalc', function() {

    let sortField = this.get('sortField') || this.get('idField'),
      fieldObject = this.get(`fields.${sortField}`) || this.addField(sortField);
    // The dimension caches sorted results in descending order. Use 'bottom' for ascending.
    return fieldObject.get('dimension')[this.get('sortDesc') ? 'top' : 'bottom'](Infinity);
  }),

  /**
   * An observable timestamp, used to trigger the recalculation of computed properties. See 'recalc()' method.
   * @type Number
   * @default 0
   * @private
   */
  lastRecalc: 0,

  /**
   * Triggers a recalculation of computed properties (e.g., 'results').
   * This is accomplished by simply incrementing the attribute 'lastRecalc'. Computed properties can then observe
   * this attribute and update themselves accordingly. Note: we increment lastRecalc, rather than setting it to the
   * current timestamp, because timestamps lack sufficient precision and could cause recalculations to be skipped
   * during fast operations.
   * @returns {object} This cube instance.
   * @public
   */
  recalc() {
    this.incrementProperty('lastRecalc');
    return this;
  },

  /**
   * Sets the field and order for sorting the 'results' array. Triggers a recalculation of computed properties,
   * such as 'results'.  Subsequently, the 'results' will continue to be automatically sorted, even after records
   * are added or removed. The given field and order are cached in the attributes 'sortField' and 'sortDesc' for
   * future reference.
   * @returns {object} This cube instance.
   * @public
   */
  sort(field, desc) {
    let props = {};
    if (field !== null) {
      props.sortField = field;
    }
    if (desc !== null) {
      props.sortDesc = !!desc;
    }
    this.setProperties(props);
    return this.recalc();
  },

  /**
   * Applies a given filter or set of filters to 1 or more fields.
   * This method accepts two signatures:
   * (1) field, value, options, clearOthers; and
   * (2) filters, clearOthers.
   * When using the first signature, the expected params are as follows:
   * (i) 'field' (String): the identifier of the field to which the filter will be applied;
   * (ii) 'value' (null|Number|String|{from: *, to: *}|Array|Function): the filter value to be applied;
   * (iii) options (Object): optional hash of options, used to specify whether to add to (options = {add: true}),
   * remove from (options = {remove: true}) or reset (default behavior) the current filter value for the given field;
   * (iv) 'clearOthers' (Boolean): if true, indicates that the filters for all other fields should be cleared.
   * When using the 2nd signature, the expected params are as follows:
   * (i) 'filters' (Object[]): an array of objects with the properties 'field', 'value' & 'options', which correspond
   * to the params of the 1st signature described above;
   * (ii) 'clearOthers' (Boolean): see the description of the 'clearOthers' param in the 1st signature above.
   * @returns {object} This cube instance.
   * @public
   */
  filter() {

    // Validate inputs to determine which signature this method was called with.
    let arr, clearOthers;
    if (typeof arguments[0] === 'string') {
      arr = [{ field: arguments[0], value: arguments[1], options: arguments[2] }];
      clearOthers = arguments[3];
    } else {
      arr = arguments[0];
      clearOthers = arguments[1];
    }

    // If we don't have any filter changes to apply, exit.
    if (!arr.length && !clearOthers) {
      return;
    }

    // Apply each given field filter.
    let changed = {},
        me = this;
    arr.forEach(function(item) {

      // Get the dimension object for the field.
      let fieldObject = me.get(`fields.${item.field}`) || me.addField(item.field),
          opts = item.options || {},
          methodName = opts.remove ? 'remove' : (opts.add ? 'add' : 'reset'),
          nativeFilter = fieldObject.get('filter')[methodName](item.value).get('native');

      fieldObject.get('dimension').filter(nativeFilter);
      changed[item.field] = true;
    });

    // Clear the other fields' filters, if requested.
    if (clearOthers) {
      let fields = this.get('fields');
      Object.keys(fields).forEach(function(key) {
        if (!changed[key]) {
          let fieldObject = fields.get(key);
          fieldObject.get('filter').reset(null);
          fieldObject.get('dimension').filter(null);
        }
      });
    }

    // Update computed attributes.
    return this.recalc();
  },

  /**
   * Convenience method for getting all the filters currently applied to this cube.
   * Although the filters can be accessed individually via this cube's 'fields' collection, this method gathers
   * all the field's filters into a single array structure. This is the same array structure that is supported
   * by the .filter() function to set filters.  So this method is a handy way to read the filters from one cube
   * into a format that can then be applied to another cube.
   * @todo Consider implementing this as an Ember.computed(..).readOnly() property.
   * @returns {Object[]} The filters array, possibly empty.
   * @public
   */
  filters() {
    let out = [],
        fields = this.get('fields');
    if (fields) {
      Object.keys(fields).forEach(function(key) {
        let fieldObject = fields.get(key),
          value = fieldObject.get('filter.value');
        if (value !== null) {
          out.push({ field: key, value });
        }
      });
    }
    return out;
  },

  /**
   * Instantiates a new native crossfilter object, a local cache for storing the (unfiltered) data records, and a
   * hash of field objects.
   * @public
   */
  init() {
    this._super(...arguments);

    // Define private vars.
    this._fields = EmberObject.create();
    this._crossfilter = crossfilter();
    this._array = this.get('array') || [];

    // Decorate the source array with methods for updating records.
    this._array.set('idField', this.get('idField'));
    this._array.indexOfBy = indexOfBy;
    this._array.edit = edit;

    // Generate the field objects from the given configs.
    let cfg = this.get('fieldsConfig'),
      me  = this;
    if (cfg) {
      Object.keys(cfg).forEach(function(key) {
        me.addField(key, cfg[key]);
      });
    }

    // Process the existing contents of the array, if any.
    this._arrayDidChange(this._array, 0, 0, this._array.length);

    // Attach listeners to array for future additions/removals.
    this._array.addArrayObserver(this, { willChange: '_arrayWillChange', didChange: '_arrayDidChange' });
  },

  /**
   * Adds a new member to the 'fields' hash under the given hash key, using the given config.
   * @param {String} key Unique identifier for the field; typically the property name that the field corresponds to.
   * @param {Object} [cfg] Optional hash of default properties for the field.
   * @returns {Object} The newly created Object that represents the requested field.
   * @public
   */
  addField(key, cfg) {
    let fieldObject = newFieldObject(this, key, cfg);
    this.set(`fields.${key}`, fieldObject);
    return fieldObject;
  },

  /**
   * Listener for changes in the source array. Notified just before Array contents are about to change.
   * Responsible for removing exiting data from the crossfilter instance.
   * This listener is attached during init() by calling Array.addArrayObserver.
   * For more details, see Ember.Array API docs: http://emberjs.com/api/classes/Ember.Array.html#method_addArrayObserver
   * @public
   */
  _arrayWillChange(observedObj, start, removeCount) {

    // If any data is exiting, find it in the crossfilter instance and remove it. Alas, crossfilter.remove()
    // only removes the records that are currently in its filter. So to remove exiting data, we must temporarily
    // reset the filter to include only the exiting data, call crossfilter.remove(), then revert back to the
    // previous filter (if any).  Don't call recalc() yet; wait until after any entering data has arrived.
    // (See _arrayDidChange handler for that.)
    if (removeCount) {

      const idField = this.get('idField');

      // Remove all the current filters, one dimension at a time, temporarily caching them.
      let xfilter = this.get('crossfilter'),
          fields = this.get('fields'),
          filters = {};

      Object.keys(fields).forEach(function(f) {
        let filter = fields[f].get('filter.native');
        if (filter !== null) {
          filters[f] = filter;
          fields[f].get('dimension').filterAll();
        }
      });

      // Now that all filters are cleared, create a filter that targets only the exiting records.
      // The filter should be an id (for a single record) or an array of ids (for multiple records).
      let ids = observedObj.slice(start, start + removeCount).mapBy(idField);
      ids = (ids.length === 1) ? ids[0] : ids;
      fields[idField].get('dimension').filter(ids);
      xfilter.remove();

      // Now restore all the filters we just removed.
      fields[idField].get('dimension').filterAll();
      Object.keys(filters).forEach(function(f) {
        fields[f].get('dimension').filter(filters[f]);
      });
    }
  },

  /**
   * Listener for changes in the source array. Notified just after Array contents are changed.
   * Responsible for adding entering data to the crossfilter instance.
   * This listener is attached during init() by calling Array.addArrayObserver.
   * For more details, see Ember.Array API docs: http://emberjs.com/api/classes/Ember.Array.html#method_addArrayObserver
   * @public
   */
  _arrayDidChange(observedObj, start, removeCount, addCount) {
    if (addCount) {
      this.get('crossfilter').add(observedObj.slice(start, start + addCount));
    }

    // If array items were added OR removed, recompute crossfilter results.
    if (addCount || removeCount) {
      this.recalc();
    }
  },

  /**
   * Detaches array observer and destroys all the generated field objects.
   * @public
   */
  destroy() {
    this._array.removeArrayObserver(this, { willChange: '_arrayWillChange', didChange: '_arrayDidChange' });
    delete this._array;

    let fields = this.get('fields');
    Object.keys(fields).forEach(function(f) {
      fields[f].destroy();
    });

    this._super();
  }
});
