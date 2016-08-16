import Ember from 'ember';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  layoutService: service('layout'),

  tagName: 'vbox',

  actions: {
    journalAction() {
      this.get('layoutService').toggleJournal();
    },

    toggleFullWidthPanel(panel) {
      this.get('layoutService').toggleFullWidthPanel(panel);
    }

  }
});
