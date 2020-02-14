import Component from '@glimmer/component';
import { inject as service } from '@ember/service';
import { action } from '@ember/object';
import { tracked } from '@glimmer/tracking';


const COLUMN_WIDTH = {
  'machineIdentity.machineName': '12vw',
  'firstFileName': 190,
  'score': 80
};

export default class TableWidgetComponent extends Component {

  @service('i18n') i18n;

  @service deepLink;

  @tracked currentSort = null;

  @tracked tableData = null;

  constructor() {
    super(...arguments);
    this.currentSort = { field: 'score', direction: 'desc' };
    this.tableData = this.args.data?.items || [];
  }

  get columns() {
    const cols = this.args.config?.columns || [];
    const newColumns = cols.map((col) => {
      return {
        width: COLUMN_WIDTH[col] || '110px',
        field: col,
        title: this.i18n.t(`springboard.columns.${col}`)
      };
    });
    return [...newColumns];
  }

  @action
  sortData({ field }, direction) {
    this.currentSort = { field, direction };
    const sorted = this.tableData.sortBy(field);
    if (direction === 'desc') {
      sorted.reverse();
    }
    this.tableData = sorted;
  }

  @action
  rowClick(item) {
    const { deepLink } = this.args.config;
    if (deepLink) {
      this.deepLink.transition(deepLink, item);
    }
  }
}
