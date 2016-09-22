import HasTableParent from '../mixins/has-table-parent';
import CellMixin from '../mixins/is-cell';
import SortableItem from 'ember-sortable/components/sortable-item';
import layout from './template';

export default SortableItem.extend(HasTableParent, CellMixin, {
  layout,
  classNames: 'rsa-data-table-header-cell'
});
