import Component from '@ember/component';
import { set } from '@ember/object';

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
    width: '10vw',
    field: 'data.processName',
    title: 'investigateProcessAnalysis.nodeList.processName'
  },
  {
    width: '5vw',
    field: 'data.riskScore',
    title: 'investigateProcessAnalysis.nodeList.riskScore'
  },
  {
    width: '5vw',
    field: 'data.hostCount',
    title: 'investigateProcessAnalysis.nodeList.hostCount'
  }
];
export default Component.extend({

  tagName: 'box',

  classNames: ['process-node-list'],

  columnsConfig: COLUMNS,

  allItemsChecked: false,

  onRowSelection: null,

  nodeList: null,

  currentSort: {
    field: 'data.riskScore',
    direction: 'desc'
  },

  actions: {
    toggleSelection(item) {
      set(item, 'selected', !item.selected);
      const nodeList = this.get('nodeList');
      const selections = this.get('nodeList').filter((node) => node.selected);

      this.set('allItemsChecked', selections.length === nodeList.length);

      if (this.onRowSelection) {
        this.onRowSelection(nodeList);
      }
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

      const sorted = this.get('nodeList').sortBy(this.get('currentSort.field'));
      if (this.get('currentSort.direction') === 'asc') {
        sorted.reverse();
      }
      this.set('nodeList', sorted);
    }
  }

});
