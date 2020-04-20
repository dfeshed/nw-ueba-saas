/**
 * @file Has Table Parent mixin
 * Adds initialization code to find the nearest data table graph component ancestor,
 * and cache reference to it in the `table` attribute.
 * @public
 */
import computed from 'ember-computed';
import Mixin from '@ember/object/mixin';

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
