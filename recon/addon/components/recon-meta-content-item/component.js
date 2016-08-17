import Ember from 'ember';
import layout from './template';
import { alias } from 'ember-computed-decorators';
const { Component } = Ember;

export default Component.extend({
  layout,
  tagName: 'hbox',
  classNameBindings: [':recon-meta-content-item'],
  @alias('item.0') name: null,
  @alias('item.1') value: null
});
