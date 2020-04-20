/**
 * @file Has Chart Parent mixin
 * Adds initialization code to find the nearest chart component ancestor,
 * and cache reference to it in the `chart` attribute.
 * @public
 */
import Mixin from '@ember/object/mixin';

import { computed } from '@ember/object';

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