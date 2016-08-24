import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({
  classNames: ['rsa-content-tree'],

  actions: {
    addAction(parentNode, childNode) {
      this.sendAction('addAction', parentNode, childNode);
    },

    toggleTreeVisibilityAction() {
      this.sendAction('toggleTreeVisibilityAction');
    }
  }
});
