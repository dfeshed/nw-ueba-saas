import Ember from 'ember';
import layout from './template';

const { Component } = Ember;

export default Component.extend({
  layout,
  classNames: ['rsa-incident-journal-entry'],

  /**
   * The journal entry data.
   * @type { author: String, created: String, notes: String, milestone: String }
   * @public
   */
  entry: null
});
