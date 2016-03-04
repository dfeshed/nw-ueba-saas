import Ember from 'ember';
import layout from '../templates/components/rsa-action-select-theme';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-action-select-theme'],

  actions: {
    /**
    * Changes the current theme selection. Assumes this.theme is the theme service.
    * @param theme
    * @public
    */
    changeTheme(theme) {
      this.set('theme.selected', theme);
      localStorage.setItem('rsa-theme-default-selected', theme);
    }
  }

});
