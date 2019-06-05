import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

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
  @computed('routing.currentRouteName')
  routePath(currentRouteName) {
    const paths = currentRouteName.split('.');
    const path = paths.pop();
    return path === 'index' ? paths.pop() || '' : path;
  },

  @computed()
  isViewSourcesEnabled() {
    return this.get('features').isEnabled('rsa.usm.viewSourcesFeature');
  },

  @computed('routing.currentRouteName')
  isSourcesActive(currentRouteName) {
    let isActive = false;
    if (currentRouteName.indexOf('admin-source-management.sources') !== -1 ||
        currentRouteName.indexOf('admin-source-management.source-wizard') !== -1) {
      isActive = true;
    }
    return isActive;
  },

  @computed('routing.currentRouteName')
  isGroupsActive(currentRouteName) {
    let isActive = false;
    if (currentRouteName.indexOf('admin-source-management.groups') !== -1 ||
        currentRouteName.indexOf('admin-source-management.group-wizard') !== -1) {
      isActive = true;
    }
    return isActive;
  },

  @computed('routing.currentRouteName')
  isPoliciesActive(currentRouteName) {
    let isActive = false;
    if (currentRouteName.indexOf('admin-source-management.policies') !== -1 ||
        currentRouteName.indexOf('admin-source-management.policy-wizard') !== -1) {
      isActive = true;
    }
    return isActive;
  },

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
