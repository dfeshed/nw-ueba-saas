import Ember from 'ember';
import layout from './template';
const { Component } = Ember;

export default Component.extend({
  layout,
  classNameBindings: [':recon-event-header'],
  tagName: 'container',
  showHeaderData: true,
  actions: {
    toggleHeaderData() {
      this.toggleProperty('showHeaderData');
    }
  }
});
