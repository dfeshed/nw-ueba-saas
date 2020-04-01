import Route from '@ember/routing/route';


export const DEFAULT_THEME = 'dark';
export const DEFAULT_LOCALE = { id: 'en_US', key: 'en-us', label: 'english' };
export const DEFAULT_LOCALES = [DEFAULT_LOCALE];

export default Route.extend({
  model() {
    this.transitionTo('investigate-users');
  }
});
