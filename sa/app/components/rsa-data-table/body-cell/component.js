import Ember from 'ember';
import CellMixin from '../mixins/is-cell';

const { Component } = Ember;

export default Component.extend(CellMixin, {
  classNames: 'rsa-data-table-body-cell'
});
