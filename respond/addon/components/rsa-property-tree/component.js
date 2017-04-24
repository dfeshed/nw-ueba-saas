import Ember from 'ember';
import Component from 'ember-component';
import computed from 'ember-computed-decorators';

const { typeOf } = Ember;

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
   * The data type of `model`.
   * @type {string}
   * @private
   */
  @computed('model')
  modelType(model) {
    return typeOf(model);
  },

  /**
   * An array of information about the properties of an object-type `model` and their respective values & data types.
   * The array will be sorted by property name, in ascending order.
   * @type {{key: String, value: *, type: String, isNestedValue: Boolean}[]}
   * @private
   */
  @computed('model', 'modelType')
  keys(model, modelType) {
    if (modelType === 'object') {
      return Object.keys(model)
        .sort()
        .map((name) => {
          const value = model[name];
          const type = typeOf(value);
          return {
            name,
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
   * are of type 'object' or 'array'.
   * @type {string}
   * @public
   */
  nestedValueComponentClass: 'rsa-property-tree'
});