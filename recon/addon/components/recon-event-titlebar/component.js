import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';
const { Component } = Ember;

export default Component.extend({
  layout,
  tagName: 'hbox',
  classNameBindings: [':recon-event-titlebar'],

  isExpanded: false,
  toggleHeaderData: null,
  toggleMetaDetails: null,

  /**
   * The title is the string to display as the title of the header
   * It will default to 'Event Reconstruction' if nothing is passed in
   * @type {string} The title to display
   * @public
   */
  @computed('title')
  displayTitle: (title) => title || 'Event Reconstruction',

  @computed('isExpanded')
  arrowDirection: (isExpanded) => (isExpanded) ? 'right' : 'left',

  actions: {
    toggleExpanded() {
      const isExpanded = this.get('isExpanded');
      if (isExpanded) {
        this.sendAction('shrinkRecon');
        // when shrinking recon, need to make sure to hide meta
        this.sendAction('toggleMetaDetails', true);
      } else {
        this.sendAction('expandRecon');
      }
      this.set('isExpanded', !isExpanded);
    },

    toggleMetaDetails() {
      // need to expand recon to have meta open
      if (!this.get('isExpanded')) {
        this.send('toggleExpanded');
      }
      this.sendAction('toggleMetaDetails');
    }
  }

});
