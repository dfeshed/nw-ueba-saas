import Component from '@ember/component';
import CellMixin from '../mixins/is-cell';
import layout from './template';

export default Component.extend(CellMixin, {
  layout,
  classNames: 'rsa-data-table-body-cell'
});
