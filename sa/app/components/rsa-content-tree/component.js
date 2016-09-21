import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({
  classNames: ['rsa-content-tree'],

  actions: {
    addAction(child) {
      this.sendAction('tagSelected', child);
    },

    toggleTreeVisibilityAction() {
      this.sendAction('toggleTreeVisibilityAction');
    }
  }
});
