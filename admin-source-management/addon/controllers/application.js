import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import { computed } from '@ember/object';

export default Controller.extend({
  accessControl: service(),
  routing: service('-routing'),
  features: service(),

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

  isViewSourcesEnabled: computed(function() {
    return this.get('features').isEnabled('rsa.usm.viewSourcesFeature');
  }),

  isSourcesActive: computed('routing.currentRouteName', function() {
    let isActive = false;
    if (this.routing?.currentRouteName.indexOf('admin-source-management.sources') !== -1 ||
        this.routing?.currentRouteName.indexOf('admin-source-management.source-wizard') !== -1) {
      isActive = true;
    }
    return isActive;
  }),

  isGroupsActive: computed('routing.currentRouteName', function() {
    let isActive = false;
    if (this.routing?.currentRouteName.indexOf('admin-source-management.groups') !== -1 ||
        this.routing?.currentRouteName.indexOf('admin-source-management.group-wizard') !== -1 ||
        this.routing?.currentRouteName.indexOf('admin-source-management.group-ranking') !== -1) {
      isActive = true;
    }
    return isActive;
  }),

  isPoliciesActive: computed('routing.currentRouteName', function() {
    let isActive = false;
    if (this.routing?.currentRouteName.indexOf('admin-source-management.policies') !== -1 ||
        this.routing?.currentRouteName.indexOf('admin-source-management.policy-wizard') !== -1) {
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
