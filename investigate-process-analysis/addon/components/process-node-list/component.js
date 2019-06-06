import Component from '@ember/component';
import { set } from '@ember/object';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

const COLUMNS = [
  {
    title: '',
    class: 'rsa-form-row-checkbox',
    width: '1.5vw',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    width: '8vw',
    field: 'data.processName',
    title: 'investigateProcessAnalysis.nodeList.processName'
  },
  {
    width: '5vw',
    field: 'data.localScore',
    title: 'investigateProcessAnalysis.nodeList.riskScore'
  },
  {
    width: '5vw',
    field: 'data.hostCount',
    title: 'investigateProcessAnalysis.nodeList.hostCount'
  },
  {
    width: '8vw',
    field: 'data.paramSrc',
    title: 'investigateProcessAnalysis.nodeList.launchArguments'
  }
];
export default Component.extend({

  tagName: 'box',

  classNames: ['process-node-list'],

  columnsConfig: COLUMNS,

  onRowSelection: null,

  nodeList: null,

  currentSort: {
    field: 'data.localScore',
    direction: 'desc'
  },

  @computed('nodeList')
  nodeListCopy(nodeList) {
    return _.cloneDeep(nodeList).sort((node1, node2) => node2.data.localScore - node1.data.localScore);
  },

  @computed('nodeListCopy')
  allItemsChecked(nodeList) {
    const selections = nodeList.filter((node) => node.selected);
    return selections.length === nodeList.length;
  },

  _toggleSelection(item) {
    set(item, 'selected', !item.selected);
    const nodeList = this.get('nodeListCopy');
    const selections = this.get('nodeListCopy').filter((node) => node.selected);

    this.set('allItemsChecked', selections.length === nodeList.length);

    if (this.onRowSelection) {
      this.onRowSelection(nodeList);
    }
  },
  actions: {
    toggleSelection(item) {
      this._toggleSelection(item);
    },

    toggleSelectedRow(item, index, e) {
      const { target: { classList } } = e;
      if (classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox')) {
        return;
      }
      this._toggleSelection(item);
    },

    toggleAllSelection(items) {
      if (this.get('allItemsChecked')) {
        items.setEach('selected', false);
      } else {
        items.setEach('selected', true);
      }

      this.toggleProperty('allItemsChecked');

      if (this.onRowSelection) {
        this.onRowSelection(items);
      }

    },

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

});
