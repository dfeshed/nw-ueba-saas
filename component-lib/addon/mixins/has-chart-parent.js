/**
 * @file Has Chart Parent mixin
 * Adds initialization code to find the nearest chart component ancestor,
 * and cache reference to it in the `chart` attribute.
 * @public
 */
import Ember from 'ember';

const {
  Mixin,
  computed
} = Ember;

export default Mixin.create({

  /**
   * The nearest chart ancestor component, if any.
   * @type Ember.Component
   * @public
   */
  chart: computed(function() {
    return this.nearestWithProperty('isChartParent');
  })
});