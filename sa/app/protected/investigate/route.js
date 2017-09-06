import Route from 'ember-route';

export default Route.extend({
  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigate.title') });
  }
});
