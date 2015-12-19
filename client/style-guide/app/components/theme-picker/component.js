import Ember from 'ember';

export default Ember.Component.extend({
  tagName: 'section',
  classNames: 'theme-picker',
  themer: Ember.inject.service(),
  actions: {
    setTheme(id) {
      this.get('themer').set('selected', id);
    }
  }
});
