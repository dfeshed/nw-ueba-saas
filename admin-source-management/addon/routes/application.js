import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { getOwner } from '@ember/application';
import { hasPolicyChanged } from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-selectors';
import { hasGroupChanged } from 'admin-source-management/reducers/usm/group-wizard-selectors';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  eventBus: service(),
  session: service(),

  beforeModel() {
    const hasUsmAccess = this.get('accessControl.hasAdminViewUnifiedSourcesAccess');
    // ASOC-75358 - if it is analyst UI machine (not primary), block access to USM
    const isNwUIPrimary = this.get('session.isNwUIPrimary');
    if (!(hasUsmAccess && isNwUIPrimary)) {
      this.transitionToExternal('protected');
    }
  },

  title(tokens) {
    const i18n = this.get('i18n');
    tokens = (tokens || []).concat([
      i18n.t('adminUsm.title'),
      i18n.t('appTitle')
    ]);
    return tokens.join(' - ');
  },

  actions: {
    navigateToRoute(routeName) {
      this.transitionTo(routeName);
    },
    redirectToUrl(relativeUrl) {
      window.location.href = relativeUrl;
    },
    willTransition(transition) {
      let state = null;
      /* eslint-disable */
      const currentPath = this._router.currentPath;
      /* eslint-enable */
      if (currentPath.includes('admin-source-management.policy-wizard')) {
        state = getOwner(this).lookup('service:redux').getState();
        if (hasPolicyChanged(state)) {
          transition.abort();
          this.get('eventBus').trigger('rsa-application-modal-open-discard-policy-changes');
        }
      } else if (currentPath.includes('admin-source-management.group-wizard')) {
        state = getOwner(this).lookup('service:redux').getState();
        if (hasGroupChanged(state)) {
          transition.abort();
          this.get('eventBus').trigger('rsa-application-modal-open-discard-group-changes');
        }
      }
      return true;
    }
  },
  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.usmModule'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
  }
});
