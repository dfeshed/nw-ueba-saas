import Ember from 'ember';
import layout from './template';

const { Component } = Ember;

export default Component.extend({
  layout,
  classNames: ['rsa-journal-entry-editor', 'rsa-journal-entry', 'editor']
});
