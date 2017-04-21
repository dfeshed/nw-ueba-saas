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
  model: null,

  /**
   * An array of information about the properties of `model` and their respective values & data types.
   * The array will be sorted by property name, in ascending order.
   * @type {{key: String, value: *, type: String}[]}
   * @private
   */
  @computed('model')
  keys(model) {
    model = model || {};
    return Object.keys(model)
      .sort()
      .map((name) => {
        const value = model[name];
        const type = typeOf(value);
        return {
          name,
          value,
          type
        };
      });
  },

  /**
   * Configurable name of the Ember Component class to be used for rendering property values that
   * are of type 'object'. If missing, native rendering will be used, which typically results in '[object Object]'
   * (unless the object has defined a custom `.toString()` method).
   * @type {string}
   * @public
   */
  objectValueComponentClass: 'rsa-property-tree'
});