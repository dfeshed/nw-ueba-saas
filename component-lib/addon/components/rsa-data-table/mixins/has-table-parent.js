/**
 * @file Has Table Parent mixin
 * Adds initialization code to find the nearest data table graph component ancestor,
 * and cache reference to it in the `table` attribute.
 * @public
 */
import Ember from 'ember';

const {
  computed,
  Mixin
} = Ember;

export default Mixin.create({

  /**
   * The nearest data table ancestor component, if any.
   * @type Ember.Component
   * @public
   */
  table: computed(function() {
    return this.nearestWithProperty('isDataTable');
  })
});
