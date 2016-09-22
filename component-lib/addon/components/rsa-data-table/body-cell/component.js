import Ember from 'ember';
import CellMixin from '../mixins/is-cell';
import layout from './template';

const { Component } = Ember;

export default Component.extend(CellMixin, {
  layout,
  classNames: 'rsa-data-table-body-cell'
});
