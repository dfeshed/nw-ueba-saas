import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

export default DataTableBodyRow.extend({

  classNameBindings: ['isRowChecked'],

  eventBus: service(),

  accessControl: service(),

  showServiceModal: false,

  serviceList: null,

  selections: null,

  showFileStatusModal: false,

  @computed('item', 'selections')
  isRowChecked(item, selections = []) {
    if (item) {
      const isSelected = selections.findBy('thumbprint', item.thumbprint);
      return !!isSelected;
    }
    return false;
  },

  @computed('item')
  contextItems() {
    const contextConf = [
      {
        label: 'editCertificateStatus',
        prefix: 'investigateFiles.certificate.contextMenu.actions.',
        action(selection, context) {
          context.editCertificateStatus();
        }
      },
      {
        label: 'pivotToInvestigate',
        prefix: 'investigateShared.endpoint.fileActions.',
        subActions: [
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
        ]
      }
    ];
    return contextConf;
  }
});
