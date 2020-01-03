import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { set, action, computed } from '@ember/object';
import _ from 'lodash';
import { TAB_FILTER } from '../const';

const COLUMNS = [
  {
    title: '',
    class: 'rsa-form-row-checkbox',
    width: '3%',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    width: '25%',
    field: 'data.processName',
    title: 'investigateProcessAnalysis.nodeList.processName'
  },
  {
    width: '11%',
    field: 'data.localScore',
    title: 'investigateProcessAnalysis.nodeList.riskScore'
  },
  {
    width: '40%',
    field: 'data.paramDst',
    title: 'investigateProcessAnalysis.nodeList.launchArguments'
  },
  {
    width: '15%',
    field: 'data.eventTypes',
    disableSort: true,
    title: 'investigateProcessAnalysis.nodeList.eventTypes'
  }
];
@classic
@tagName('box')
@classNames('process-node-list')
export default class ProcessNodeList extends Component {
  columnsConfig = COLUMNS;
  onRowSelection = null;
  nodeList = null;

  init() {
    super.init(...arguments);
    this.currentSort = this.currentSort || { field: 'data.localScore', direction: 'desc' };
  }

  @computed('nodeList')
  get nodeListCopy() {
    return _.cloneDeep(this.nodeList).sort((node1, node2) => node2.data.localScore - node1.data.localScore);
  }

  @computed('nodeListCopy', 'activeTab')
  get filteredList() {
    let filteredNodes = this.nodeListCopy;
    const filter = TAB_FILTER[this.activeTab];
    if (filter) {
      filteredNodes = this.nodeListCopy.filter((node) => {
        if (node.data.eventCategory) {
          return node.data.eventCategory[filter];
        }
      });
    }
    return filteredNodes;
  }

  @computed('filteredList')
  get allItemsChecked() {
    const filteredList = this.get('filteredList');
    const selections = filteredList.filter((node) => node.selected);
    return filteredList.length ? selections.length === filteredList.length : false;
  }

  set allItemsChecked(value) {
    return value;
  }

  @computed('filteredList.@each.selected')
  get selections() {
    return this.filteredList.filter((node) => node.selected).length;
  }

  _toggleSelection(item) {
    set(item, 'selected', !item.selected);
    const nodeList = this.get('nodeListCopy');
    const filteredList = this.get('filteredList');
    const selections = filteredList.filter((node) => node.selected);
    this.set('allItemsChecked', selections.length === filteredList.length);

    if (this.onRowSelection) {
      this.onRowSelection(nodeList);
    }
  }

  @action
  toggleSelection(item) {
    this._toggleSelection(item);
  }

  @action
  toggleSelectedRow(item, index, e) {
    const { target: { classList } } = e;
    if (classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox')) {
      return;
    }
    this._toggleSelection(item);
  }

  @action
  toggleAllSelection(items) {
    const nodeList = this.get('nodeListCopy');
    if (this.get('allItemsChecked')) {
      items.setEach('selected', false);
    } else {
      items.setEach('selected', true);
    }

    this.toggleProperty('allItemsChecked');

    if (this.onRowSelection) {
      this.onRowSelection(nodeList);
    }

  }

  @action
  sort(column) {
    const field = column.get('field');
    let direction = 'asc';
    if ((this.get('currentSort.field') === column.get('field')) && (this.get('currentSort.direction') === 'desc')) {
      direction = 'asc';
    } else {
      direction = 'desc';
    }
    this.set('currentSort', { field, direction });

    const sorted = this.get('nodeListCopy').sortBy(this.get('currentSort.field'));
    if (this.get('currentSort.direction') === 'asc') {
      sorted.reverse();
    }
    this.set('nodeListCopy', sorted);
  }
}
