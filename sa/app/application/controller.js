import $ from 'jquery';
import computed from 'ember-computed';
import Controller from 'ember-controller';
import service from 'ember-service/inject';

export default Controller.extend({
  redux: service(),

  fatalErrors: service(),

  session: service(),

  accessControl: service(),

  authenticatedAndPageFound: computed('session.isAuthenticated', 'currentPath', function() {
    const path = this.get('currentPath');

    if (!this.get('session.isAuthenticated') || path === 'not-found' || path === 'internal-error') {
      return false;
    } else {
      return true;
    }
  }),

  _updateBodyClass(themeName) {
    $('body').removeClass((index, bodyClasses) => {
      const names = bodyClasses || '';
      const classNames = names.split(' ');
      return classNames.find((name) => {
        const match = name.match(/.*-theme/);
        return match && match[0];
      });
    });
    $('body').addClass(`${themeName}-theme`);
  },

  init() {
    this._super(...arguments);

    const redux = this.get('redux');

    this.themeName = () => {
      const state = redux.getState();
      const { global } = state;
      return global && global.preferences && global.preferences.theme && global.preferences.theme.toLowerCase();
    };

    let activeTheme;
    redux.subscribe(() => {
      const themeName = this.themeName();
      if (themeName !== activeTheme) {
        activeTheme = themeName;
        this._updateBodyClass(themeName);
      }
    });
  }
});
