import Component from '@ember/component';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import classic from 'ember-classic-decorator';
import layout from './template';
import { HW_COLUMNS } from './hw-columns';
import { connect } from 'ember-redux';
import { action } from '@ember/object';

const stateToComputed = ({ hw }) => ({
  items: hw.monitors
});

@classic
@classNames('hw-table')
@templateLayout(layout)
class HWTable extends Component {
  columns = [];

  allItemsChecked = false;

  isRowSelected = false;

  monitorsData = [];

  init() {
    super.init(...arguments);
    this.monitorsData = [...this.get('items')];
    this.columns = HW_COLUMNS;
  }

  @action
  toggleItemSelection() {
    this.toggleProperty('isRowSelected');
  }

  @action
  toggleAllItemSelection() {
    this.toggleProperty('allItemsChecked');
  }

  @action
  sort(column) {
    if ((this.get('currentSort.field') === column.get('field')) && (this.get('currentSort.direction') === 'desc')) {
      this.set('currentSort.direction', 'asc');
    } else {
      this.set('currentSort', column);
      this.set('currentSort.direction', 'desc');
    }

    const sorted = this.get('monitorsData').sortBy(this.get('currentSort.field'));
    if (this.get('currentSort.direction') === 'desc') {
      sorted.reverse();
    }
    this.set('monitorsData', sorted);

  }

}

export default connect(stateToComputed)(HWTable);