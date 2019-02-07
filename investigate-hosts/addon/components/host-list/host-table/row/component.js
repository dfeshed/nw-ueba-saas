import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import computed from 'ember-computed-decorators';
import layout from './template';

/**
 * Extension of the Data Table default row class for supporting focus on the row
 * @public
 */
export default DataTableBodyRow.extend({
  layout,

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
        label: 'resetRiskScore',
        order: 5,
        prefix: 'investigateShared.endpoint.fileActions.',
        showDivider: true,
        action(selection, context) {
          context.showRiskScoreModal();
        }
      },
      {
        label: 'startScan',
        order: 3,
        prefix: 'investigateShared.endpoint.hostActions.',
        action(selection, context) {
          context.showScanModal('START_SCAN');
        },
        disabled(selection, context) {
          return context.get('isScanStartButtonDisabled');
        }
      },
      {
        label: 'stopScan',
        order: 4,
        prefix: 'investigateShared.endpoint.hostActions.',
        action(selection, context) {
          context.showScanModal('STOP_SCAN');
        },
        disabled(selection, context) {
          return context.get('isScanStartButtonDisabled');
        }
      },
      {
        label: 'delete',
        customComponent: true,
        order: 2,
        prefix: 'investigateShared.endpoint.hostActions.',
        action(selection, context) {
          context.showConfirmationModal();
        }
      },
      {
        label: 'pivotToInvestigate',
        order: 1,
        prefix: 'investigateShared.endpoint.fileActions.',
        subActions: [
          {
            label: 'consoleEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              context.pivotToInvestigate(context.get('item'), 'Console Event');
            }
          },
          {
            label: 'networkEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              context.pivotToInvestigate(context.get('item'), 'Network Event');
            }
          },
          {
            label: 'fileEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              context.pivotToInvestigate(context.get('item'), 'File Event');
            }
          },
          {
            label: 'processEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              context.pivotToInvestigate(context.get('item'), 'Process Event');
            }
          },
          {
            label: 'registryEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              context.pivotToInvestigate(context.get('item'), 'Registry Event');
            }
          }
        ],
        disabled(selection, context) {
          return (context.get('selections').length > 1);
        }
      }
    ];
    return contextConf.sortBy('order');
  }

});
