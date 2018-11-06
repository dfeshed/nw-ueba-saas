import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({
  accessControl: service(),

  beforeModel() {
    const hasUsmAccess = this.get('accessControl.hasAdminViewUnifiedSourcesAccess');
    if (!hasUsmAccess) {
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
    }
  }
});
