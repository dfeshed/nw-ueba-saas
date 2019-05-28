import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import computed from 'ember-computed-decorators';

/**
 * Extension of the Data Table default row class for supporting focus on the row
 * @public
 */
export default DataTableBodyRow.extend({

  classNameBindings: ['isRowChecked'],

  @computed('item', 'selections')
  isRowChecked(item, selections = []) {
    const isSelected = selections.findBy('id', item.id);
    return !!isSelected;
  },

  @computed('item')
  contextItems() {
    const contextConf = [
      {
        label: 'saveLocalCopy',
        order: 1,
        prefix: 'investigateHosts.downloads.buttons.',
        showDivider: true,
        action(selection, context) {
          context.saveLocalCopy();
        }
      },
      {
        label: 'delete',
        order: 2,
        prefix: 'investigateHosts.downloads.buttons.',
        showDivider: true,
        action(selection, context) {
          context.deleteFiles();
        }
      }
    ];

    return contextConf.sortBy('order');
  }

});
