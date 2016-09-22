import Ember from 'ember';
import RowMixin from '../mixins/is-row';
import layout from './template';

const { Component } = Ember;

export default Component.extend(RowMixin, {
  layout
});
