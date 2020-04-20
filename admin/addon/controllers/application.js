import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import { computed } from '@ember/object';

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

  hasAdminViewUnifiedSourcesAccess: computed(function() {
    const hasUsmAccess = this.get('accessControl.hasAdminViewUnifiedSourcesAccess');
    return hasUsmAccess;
  }),

  isUnifiedSourcesActive: computed('routing.currentRouteName', function() {
    let isActive = false;
    if (this.routing?.currentRouteName.indexOf('admin-source-management') !== -1) {
      isActive = true;
    }
    return isActive;
  }),

  actions: {
    // let router handle this
    controllerNavigateToRoute(routeName) {
      this.send('navigateToRoute', routeName);
    },
    // let router handle this
    controllerRedirectToUrl(relativeUrl) {
      this.send('redirectToUrl', relativeUrl);
    }
  }
});
