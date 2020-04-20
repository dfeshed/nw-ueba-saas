import { computed } from '@ember/object';
import Controller from '@ember/controller';
import { inject as service } from '@ember/service';

export default Controller.extend({
  accessControl: service(),
  routing: service('-routing'),

  /**
   * Returns the leaf route name for the current fully qualified route
   * @property routePath
   * @param currentRouteName
   * @public
   * @returns {*}
   */
  routePath: computed('routing.currentRouteName', function() {
    const paths = this.routing?.currentRouteName.split('.');
    const path = paths.pop();
    return path === 'index' ? paths.pop() || '' : path;
  }),

  actions: {
    navigateTo(routeName) {
      this.transitionToRoute(routeName);
    }
  }
});
