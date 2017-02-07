import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({

  setupController(controller, model) {
    this._super(controller, model);
    controller.set('isNavigationCollapsed', false);
  },

  actions: {
    collapseNavigation() {
      this.controller.set('isNavigationCollapsed', true);
    },
    expandNavigation() {
      this.controller.set('isNavigationCollapsed', false);
    }
  }
});