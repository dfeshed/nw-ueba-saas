import contextMenuMixin from 'ember-context-menu';
import computed from 'ember-computed-decorators';
import layout from './template';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import { inject as service } from '@ember/service';
import { externalLookup } from 'investigate-shared/utils/file-external-lookup';


export default DataTableBodyRow.extend(contextMenuMixin, {
  layout,

  classNameBindings: ['isRowChecked'],

  eventBus: service(),

  accessControl: service(),

  showServiceModal: false,

  serviceList: null,

  selections: null,

  showFileStatusModal: false,

  @computed('item', 'selections')
  isRowChecked(item, selections = []) {
    const isSelected = selections.findBy('id', item.id);
    return !!isSelected;
  },

  @computed('item')
  contextItems() {
    const cntx = this;
    const canManageFiles = cntx.get('accessControl.endpointCanManageFiles');

    const contextConf = [
      {
        label: 'editFileStatus',
        prefix: 'investigateShared.endpoint.fileActions.',
        action() {
          cntx.retrieveRemediationStatus(cntx.get('selections'));
          cntx.editFileStatus(cntx.get('item'));
        },
        order: 1
      },
      {
        label: 'googleLookup',
        prefix: 'investigateShared.endpoint.fileActions.',
        order: 3,
        subActions: [
          { label: 'fileName',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              externalLookup({ name: 'fileName', type: 'google' }, cntx.get('selections'));
            }
          },
          { label: 'md5',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              externalLookup({ name: 'md5', type: 'google' }, cntx.get('selections'));
            }
          },
          { label: 'sha1',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              externalLookup({ name: 'sha1', type: 'google' }, cntx.get('selections'));
            }
          },
          { label: 'sha256',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              externalLookup({ name: 'sha256', type: 'google' }, cntx.get('selections'));
            }
          }
        ]
      },
      {
        label: 'virusTotalLookup',
        prefix: 'investigateShared.endpoint.fileActions.',
        order: 4,
        subActions: [
          { label: 'md5',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              externalLookup({ name: 'md5', type: 'VirusTotal' }, cntx.get('selections'));
            }
          },
          { label: 'sha1',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              externalLookup({ name: 'sha1', type: 'VirusTotal' }, cntx.get('selections'));
            }
          },
          { label: 'sha256',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              externalLookup({ name: 'sha256', type: 'VirusTotal' }, cntx.get('selections'));
            }
          }
        ]
      },
      {
        label: 'resetRiskScore',
        order: 5,
        prefix: 'investigateShared.endpoint.fileActions.',
        showDivider: true,
        action() {
          cntx.resetRiskScore(cntx.get('selections'));
        }
      }
    ];

    if (cntx.get('fileDownloadButtonStatus') && canManageFiles) {

      const fileDownloadButtons = [
        {
          label: 'downloadToServer',
          order: 6,
          prefix: 'investigateShared.endpoint.fileActions.',
          showDivider: true,
          disabled() {
            return cntx.get('fileDownloadButtonStatus').isDownloadToServerDisabled;
          },
          action() {
            cntx.downloadFiles();
          }
        },
        {
          label: 'saveLocalCopy',
          order: 7,
          prefix: 'investigateShared.endpoint.fileActions.',
          disabled() {
            return cntx.get('fileDownloadButtonStatus').isSaveLocalAndFileAnalysisDisabled;
          },
          action() {
            cntx.saveLocalCopy();
          }
        },
        {
          label: 'analyzeFile',
          order: 8,
          prefix: 'investigateShared.endpoint.fileActions.',
          disabled() {
            return cntx.get('fileDownloadButtonStatus').isSaveLocalAndFileAnalysisDisabled;
          },
          action() {
            cntx.analyzeFile();
          }
        }
      ];
      contextConf.push(...fileDownloadButtons);
    }

    if (cntx.get('showPivotToInvestigate') != false) {
      const pivot = {
        label: 'pivotToInvestigate',
        order: 2,
        prefix: 'investigateShared.endpoint.fileActions.',
        disabled() {
          return (cntx.get('selections').length > 1);
        },
        action() {
          cntx.pivotToInvestigate(cntx.get('item'));
        }
      };
      contextConf.push(pivot);
    }

    return contextConf.sortBy('order');
  }
});
