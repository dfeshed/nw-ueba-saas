import HasTableParent from '../mixins/has-table-parent';
import CellMixin from '../mixins/is-cell';
import SortableItem from 'sa/components/sortable-item';

export default SortableItem.extend(HasTableParent, CellMixin, {
  classNames: 'rsa-data-table-header-cell'
});
