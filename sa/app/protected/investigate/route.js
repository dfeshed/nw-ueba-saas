import Route from 'ember-route';

export default Route.extend({
  afterModel(model, transition) {
    if (transition.targetName === 'protected.investigate.index') {
      this.transitionTo('protected.investigate.investigate-events');
    }
  },

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigate.title') });
  }
});
