import Route from 'ember-route';

export default Route.extend({

  beforeModel() {
    // default landing page for /investigate is events
    this.transitionTo('protected.investigate.investigate-events');
  },

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigate.title') });
  }
});
