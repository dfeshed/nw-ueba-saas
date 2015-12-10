import Ember from 'ember';

/**
 * A sample component to use a guide for creating new components.
 * @public
 */
export default Ember.Component.extend({
  count: 0,

  actions: {
    incrementCount() {
      this.incrementProperty('count');
    }
  }
});
