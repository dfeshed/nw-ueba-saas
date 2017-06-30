import Route from 'ember-route';
import service from 'ember-service/inject';

export default Route.extend({

  contextualHelp: service(),
  i18n: service(),

  title(tokens) {
    const i18n = this.get('i18n');
    tokens = (tokens || []).concat([
      i18n.t('respond.title'),
      i18n.t('appTitle')
    ]);
    return tokens.join(' - ');
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.respondModule'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
  }

});
