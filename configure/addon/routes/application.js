import Route from '@ember/routing/route';
import { inject } from '@ember/service';

export default Route.extend({
  router: inject(),

  title(tokens) {
    const i18n = this.get('i18n');
    tokens = (tokens || []).concat([
      i18n.t('configure.title'),
      i18n.t('appTitle')
    ]);
    return tokens.join(' - ');
  },

  actions: {
    navigateTo(route) {
      this.transitionTo(route);
    }
  }
});
