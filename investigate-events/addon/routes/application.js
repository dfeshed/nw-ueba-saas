import Route from 'ember-route';
import service from 'ember-service/inject';

export default Route.extend({
  i18n: service(),

  title() {
    return this.get('i18n').t('pageTitle', {
      section: this.get('i18n').t('investigate.title')
    });
  }
});
