import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';
const {Component} = Ember;

export default Component.extend({
  layout,
  tagName: 'hbox',
  classNameBindings: [':recon-event-titlebar'],
  /**
   * The title is the string to display as the title of the header
   * It will default to 'Event Reconstruction' if nothing is passed in
   * @type {string} The title to display
   * @public
   */
  @computed('title')
  displayTitle(title) {
    return title || 'Event Reconstruction';
  }
});
