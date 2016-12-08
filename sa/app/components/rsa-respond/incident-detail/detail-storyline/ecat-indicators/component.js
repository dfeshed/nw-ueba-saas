import Ember from 'ember';
import { alias } from 'ember-computed-decorators';

const {
  Component
} = Ember;

export default Component.extend({
  tagName: '',

  @alias('indicator.catalyst') isCatalyst: null,

  @alias('indicator.alert.fname') filename: null,

  @alias('indicator.alert.shost') shost: null
});
