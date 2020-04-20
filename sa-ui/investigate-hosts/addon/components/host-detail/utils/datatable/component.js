import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { alias } from '@ember/object/computed';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setHostDetailsDataTableSortConfig } from 'investigate-hosts/actions/data-creators/details';

const dispatchToActions = {
  setHostDetailsDataTableSortConfig
};

@classic
@tagName('box')
@classNames('host-detail__datatable')
class HostDetailsDataTable extends Component {
  customSort = null;

  @alias('status')
  isDataLoading;

  @computed('items', 'totalItems')
  get total() {
    return this.totalItems ? this.totalItems : this.items.length;
  }

  init() {
    super.init(...arguments);
    this.items = this.items || [];
  }

  @action
  sort(column) {
    column.set('isDescending', !column.isDescending);
    const customSort = this.get('customSort');
    if (customSort) {
      this.customSort(column);
    } else {
      this.send('setHostDetailsDataTableSortConfig', {
        isDescending: column.isDescending,
        field: column.field
      });
    }
  }

  @action
  toggleSelectedRow(item, index, e, table) {

    if (this.get('selectRowAction')) {
      table.set('selectedIndex', index);
      this.selectRowAction(item);
    } else {
      table.set('selectedIndex', -1);
    }

  }

  @action
  onCloseServiceModal() {
    this.set('showServiceModal', false);
  }

  @action
  onCloseEditFileStatus() {
    this.set('showFileStatusModal', false);
  }
}

export default connect(null, dispatchToActions)(HostDetailsDataTable);