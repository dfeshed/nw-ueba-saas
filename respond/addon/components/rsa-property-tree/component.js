import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { isEmpty, typeOf } from '@ember/utils';

/**
 * @file Property Tree Component
 * Renders the key-value pairs found in a given object (hash). Supports nested objects.
 *
 * This component mimics the display used in SA 10.x to display event details. It is not particularly
 * pretty and will hopefully eventually be replaced by something more sophisticated.
 *
 * @public
 */
export default Component.extend({
  classNames: ['rsa-property-tree'],

  /**
   * The data structure whose properties are to be displayed. Either an Object or an Array.
   * @type {Object|Array}
   * @public
   */
  model: null,

  /**
   * The property path of `model`. Used only when rendering nested models.
   * @type {String}
   * @public
   */
  modelPath: '',

  /**
   * Configurable optional ordered list of property names to be displayed.
   * If not specified, the ordering is not guaranteed; we will just whatever order `Object.keys(model)` returns.
   * Accepts either an array of strings or a single comma-delim string.
   * @type {String[]|String}
   * @public
   */
  order: null,

  /**
   * Configurable optional list of property names to be excluded from the display.
   * If given, any property names found in this array will be excluded from DOM, even if they are included in `order`.
   * Accepts either an array of strings or a single comma-delim string.
   * @type {String[]|String}
   * @public
   */
  hidden: null,

  /**
   * If truthy, any property names that are not in `order` will be excluded from the display.
   * Only used if `order` is not empty.
   * @type {Boolean}
   * @public
   */
  skipUnknowns: false,

  /**
   * Configurable optional formatter for property names. Can be used to translate property names to user-friendly
   * display names (such as i18n strings). If not specified, the raw property names will shown in the UI.
   * @param {String} name The leaf name of the property.
   * @param {String} fullPath The full path of the property, dot-delimited.
   * @type {Function}
   * @returns {String}
   * @public
   */
  propertyNameFormatter: null,

  /**
   * The data type of `model`.
   * @type {string}
   * @private
   */
  @computed('model')
  modelType(model) {
    return typeOf(model);
  },

  /**
   * Computes an array of property names to be displayed from `model`, ordered according to `order`, filtered by `hidden`,
   * and possibly including extra ("unknown") properties depending upon `skipUnknowns`.
   * @private
   */
  @computed('order', 'model', 'modelType', 'hidden', 'skipUnknowns')
  resolvedOrder(order, model, modelType, hidden, skipUnknowns) {
    const modelKeys = (modelType === 'object') ? Object.keys(model) : [];

    let result = order;
    if (typeOf(order) === 'string') {
      result = order.split(',').map((str) => str.trim());
    }
    if (isEmpty(result)) {

      // `order` is unspecified, so start with all the keys from model.
      result = modelKeys;
    } else if (!skipUnknowns) {

      // `order` is specified, append other found keys.
      modelKeys.forEach((key) => {
        if (!result.includes(key)) {
          result.pushObject(key);
        }
      });
    }

    // Filter out the hidden keys.
    if (!isEmpty(hidden)) {
      if (typeOf(hidden) === 'string') {
        hidden = hidden.split(',').map((str) => str.trim());
      }
      result = result.reject((key) => hidden.includes(key));
    }
    return result;
  },

  /**
   * An array of information about the properties of an object-type `model` and their respective values & data types.
   * The array will be sorted & filtered according to `resolvedOrder`.
   * @type {{key: String, value: *, type: String, isNestedValue: Boolean}[]}
   * @private
   */
  @computed('model', 'resolvedOrder', 'modelPath')
  keys(model, resolvedOrder, modelPath) {
    if (!model) {
      return [];
    }

    const propertyNameFormatter = this.get('propertyNameFormatter');
    return resolvedOrder
      .filter((name) => !isEmpty(model[name]))
      .map((name) => {
        const value = model[name];
        const type = typeOf(value);
        const fullPath = modelPath ? `${modelPath}.${name}` : name;
        return {
          name,
          nameFormatted: propertyNameFormatter ? propertyNameFormatter(name, fullPath) : name,
          fullPath,
          value,
          type,
          isNestedValue: (type === 'object') || (type === 'array')
        };
      });
  },

  /**
   * An array of information about the members of an array-type `model`, i.e. their respective values & data types.
   * This array's order will match order of the original array members.
   * @type {value: *, type: String, isNestedValue: Boolean}[]}
   * @private
   */
  @computed('model', 'modelType')
  members(model, modelType) {
    if (modelType === 'array') {
      return model.map((value) => {
        const type = typeOf(value);
        return {
          value,
          type,
          isNestedValue: (type === 'object') || (type === 'array')
        };
      });
    } else {
      return [];
    }
  },

  /**
   * Required configurable name of the Ember Component class to be used for rendering property values that
   * are NOT of type 'object' or 'array' (in other words, scalars such as numbers & text).
   * @type {string}
   * @public
   */
  scalarValueComponentClass: 'respond-common/stub',

  /**
   * Required configurable name of the Ember Component class to be used for rendering property values that
   * are of type 'object' or 'array'.
   * @type {string}
   * @public
   */
  nestedValueComponentClass: 'rsa-property-tree'
});
