import classic from 'ember-classic-decorator';
import { classNameBindings } from '@ember-decorators/component';
import { computed } from '@ember/object';
import { inject as service } from '@ember/service';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';

@classic
@classNameBindings('isRowChecked')
export default class TableRow extends DataTableBodyRow {
  @service
  eventBus;

  @service
  accessControl;

  showServiceModal = false;
  serviceList = null;
  selections = null;
  showFileStatusModal = false;

  @computed('item', 'selections')
  get isRowChecked() {
    const selections = this.selections || [];
    if (this.item) {
      const isSelected = selections.findBy('thumbprint', this.item.thumbprint);
      return !!isSelected;
    }
    return false;
  }

  @computed('item')
  get contextItems() {
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
        ]
      }
    ];
    return contextConf;
  }
}
