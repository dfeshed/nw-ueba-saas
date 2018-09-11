import contextMenuMixin from 'ember-context-menu';
import computed from 'ember-computed-decorators';
import layout from './template';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import { inject as service } from '@ember/service';

export default DataTableBodyRow.extend(contextMenuMixin, {
  layout,

  eventBus: service(),

  showServiceModal: false,

  serviceList: null,

  selections: null,

  showFileStatusModal: false,

  @computed('item')
  contextItems() {
    const cntx = this;
    const contextConf = [
      {
        label: 'Edit File Status',
        icon: 'pencil-write-2',
        iconStyle: 'lined',
        action() {
          cntx.editFileStatus(cntx.get('item'));
        }
      },
      {
        label: 'Watch',
        icon: 'binoculars',
        iconStyle: 'lined'
      },
      {
        label: 'Download',
        icon: 'download-2',
        iconStyle: 'lined'
      },
      {
        label: 'More',
        icon: 'navigation-show-more-1',
        iconStyle: 'lined'
      }
    ];

    if (cntx.get('showPivotToInvestigate') != false) {
      const pivot = {
        label: 'Pivot to Investigate',
        icon: 'expand-6',
        disabled() {
          return (cntx.get('selections').length > 1);
        },
        action() {
          cntx.pivotToInvestigate(cntx.get('item'));
        }
      };
      contextConf.push(pivot);
    }

    return contextConf;
  },

  actions: {

    onCloseServiceModal() {
      this.set('showServiceModal', false);
    },

    onCloseEditFileStatus() {
      this.set('showFileStatusModal', false);
    }

  }
});
