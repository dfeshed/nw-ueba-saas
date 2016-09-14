import Ember from 'ember';
import layout from './template';
const { Component } = Ember;

export default Component.extend({
  layout,
  tagName: 'section',
  classNames: 'rsa-offset-table',
  byteRows: null,
  bytesPerRow: 1,
  digits: 2
});
