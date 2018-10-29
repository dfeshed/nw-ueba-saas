import contextMenuMixin from 'ember-context-menu';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

export default DataTableBodyRow.extend(contextMenuMixin, {

  eventBus: service(),

  accessControl: service(),

  showServiceModal: false,

  serviceList: null,

  selections: null,

  showFileStatusModal: false,

  @computed('item')
  contextItems() {

    const cntx = this;
    const contextConf = [
      {
        label: 'editCertificateStatus',
        prefix: 'investigateFiles.certificate.contextMenu.actions.',
        action() {
          cntx.editCertificateStatus();
        }
      }
    ];
    return contextConf;
  }
});
