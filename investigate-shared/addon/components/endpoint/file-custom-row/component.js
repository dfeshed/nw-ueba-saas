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
    return [
      {
        label: 'Pivot to Investigate',
        icon: 'expand-6',
        disabled() {
          return (cntx.get('selections').length > 1);
        },
        action() {
          cntx.set('showServiceModal', true);
        }
      },
      {
        label: 'Edit File Status',
        icon: 'pencil-write-2',
        action() {
          if (!cntx.get('isDestroyed') && !cntx.get('isDestroying')) {
            cntx.set('showFileStatusModal', true);
          }
        }
      },
      {
        label: 'Watch',
        icon: 'binoculars'
      },
      {
        label: 'Download',
        icon: 'download-2'
      },
      {
        label: 'More',
        icon: 'navigation-show-more-1'
      }
    ];
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
