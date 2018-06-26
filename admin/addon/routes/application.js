import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import RSVP from 'rsvp';

export default Route.extend({
  features: service(),
  router: service(),

  model() {
    return this.waitForSourceManagementFeatures();
  },

  waitForSourceManagementFeatures(numRetries = 19) {
    return new RSVP.Promise((resolve, reject) => {
      const areFeaturesLoaded = (maxTrys) => {
        if (this.get('features').hasFeatureFlag('rsa.usm')) {
          resolve();
        } else if (maxTrys > 0) {
          setTimeout(areFeaturesLoaded, 500, maxTrys - 1);
        } else {
          reject();
        }
      };
      areFeaturesLoaded(numRetries);
    });
  },

  title(tokens) {
    const i18n = this.get('i18n');
    tokens = (tokens || []).concat([
      i18n.t('admin.title'),
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
