import contextMenuMixin from 'ember-context-menu';
import computed from 'ember-computed-decorators';
import layout from './template';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import { inject as service } from '@ember/service';
import { externalLookup } from 'investigate-shared/utils/file-external-lookup';

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
          cntx.retrieveRemediationStatus(cntx.get('selections'));
          cntx.editFileStatus(cntx.get('item'));
        }
      },
      {
        label: 'Download',
        icon: 'download-2',
        iconStyle: 'lined'
      },
      {
        label: 'Google Lookup',
        subActions: [
          { label: 'Filename',
            action() {
              externalLookup({ name: 'fileName', type: 'google' }, cntx.get('selections'));
            }
          },
          { label: 'MD5',
            action() {
              externalLookup({ name: 'md5', type: 'google' }, cntx.get('selections'));
            }
          },
          { label: 'SHA1',
            action() {
              externalLookup({ name: 'sha1', type: 'google' }, cntx.get('selections'));
            }
          },
          { label: 'SHA256',
            action() {
              externalLookup({ name: 'sha256', type: 'google' }, cntx.get('selections'));
            }
          }
        ]
      },
      {
        label: 'VirusTotal Lookup',
        subActions: [
          { label: 'MD5',
            action() {
              externalLookup({ name: 'md5', type: 'VirusTotal' }, cntx.get('selections'));
            }
          },
          { label: 'SHA1',
            action() {
              externalLookup({ name: 'sha1', type: 'VirusTotal' }, cntx.get('selections'));
            }
          },
          { label: 'SHA256',
            action() {
              externalLookup({ name: 'sha256', type: 'VirusTotal' }, cntx.get('selections'));
            }
          }
        ]
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
  }
});
