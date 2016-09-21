import Ember from 'ember';
import layout from './template';
const { Component } = Ember;

export default Component.extend({
  layout,
  classNameBindings: [':recon-event-header'],
  tagName: 'container',

  // INPUTS
  headerItems: null,
  index: null,
  reconstructionType: null,
  showMetaDetails: null,
  total: null,

  // Actions
  closeRecon: null,
  expandRecon: null,
  shrinkRecon: null,
  toggleMetaDetails: null,
  updateReconstructionView: null,
  // END INPUTS

  showHeaderData: true,

  actions: {
    toggleHeaderData() {
      this.toggleProperty('showHeaderData');
    }
  }
});
