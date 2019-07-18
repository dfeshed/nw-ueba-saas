import Component from '@ember/component';
import { set } from '@ember/object';
import computed from 'ember-computed-decorators';
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

  @computed('nodeListCopy', 'activeTab')
  filteredList(nodes, tab) {
    let filteredNodes = nodes;
    const filter = TAB_FILTER[tab];
    if (filter) {
      filteredNodes = nodes.filter((node) => {
        if (node.data.eventCategory) {
          return node.data.eventCategory[filter];
        }
      });
    }
    return filteredNodes;
  },

  @computed('filteredList')
  allItemsChecked(filteredList) {
    const selections = filteredList.filter((node) => node.selected);
    return filteredList.length ? selections.length === filteredList.length : false;
  },

  _toggleSelection(item) {
    set(item, 'selected', !item.selected);
    const nodeList = this.get('nodeListCopy');
    const filteredList = this.get('filteredList');
    const selections = filteredList.filter((node) => node.selected);
    this.set('allItemsChecked', selections.length === filteredList.length);

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
