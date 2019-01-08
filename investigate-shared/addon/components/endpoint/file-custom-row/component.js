import computed from 'ember-computed-decorators';
import layout from './template';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import { inject as service } from '@ember/service';
import { externalLookup } from 'investigate-shared/utils/file-external-lookup';


export default DataTableBodyRow.extend({
  layout,

  classNameBindings: ['isRowChecked'],

  eventBus: service(),

  accessControl: service(),

  showServiceModal: false,

  serviceList: null,

  selections: null,

  showFileStatusModal: false,

  showProcessAnalysis: false,

  @computed('item', 'selections')
  isRowChecked(item, selections = []) {
    const isSelected = selections.findBy('id', item.id);
    return !!isSelected;
  },

  @computed('item')
  contextItems() {
    const canManageFiles = this.get('accessControl.endpointCanManageFiles');

    const contextConf = [
      {
        label: 'editFileStatus',
        prefix: 'investigateShared.endpoint.fileActions.',
        action(selection, context) {
          context.retrieveRemediationStatus(context.get('selections'));
          context.editFileStatus(context.get('item'));
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
            action(selection, context) {
              externalLookup({ name: 'fileName', type: 'google' }, context.get('selections'));
            }
          },
          { label: 'md5',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              externalLookup({ name: 'md5', type: 'google' }, context.get('selections'));
            }
          },
          { label: 'sha1',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              externalLookup({ name: 'sha1', type: 'google' }, context.get('selections'));
            }
          },
          { label: 'sha256',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              externalLookup({ name: 'sha256', type: 'google' }, context.get('selections'));
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
            action(selection, context) {
              externalLookup({ name: 'md5', type: 'VirusTotal' }, context.get('selections'));
            }
          },
          { label: 'sha1',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              externalLookup({ name: 'sha1', type: 'VirusTotal' }, context.get('selections'));
            }
          },
          { label: 'sha256',
            prefix: 'investigateShared.endpoint.fileActions.',
            action(selection, context) {
              externalLookup({ name: 'sha256', type: 'VirusTotal' }, context.get('selections'));
            }
          }
        ]
      },
      {
        label: 'viewCertificate',
        order: 5,
        prefix: 'investigateShared.endpoint.fileActions.',
        disabled(selection, context) {
          return context.get('selections').length > 10;
        },
        action(selection, context) {
          context.toggleCertificateView();
        }
      },
      {
        label: 'resetRiskScore',
        order: 6,
        prefix: 'investigateShared.endpoint.fileActions.',
        showDivider: true,
        action(selection, context) {
          context.resetRiskScore(context.get('selections'));
        }
      }
    ];

    if (this.get('fileDownloadButtonStatus') && canManageFiles) {

      const fileDownloadButtons = [
        {
          label: 'downloadToServer',
          order: 7,
          prefix: 'investigateShared.endpoint.fileActions.',
          showDivider: true,
          disabled(selection, context) {
            return context.get('fileDownloadButtonStatus').isDownloadToServerDisabled;
          },
          action(selection, context) {
            context.downloadFiles();
          }
        },
        {
          label: 'saveLocalCopy',
          order: 8,
          prefix: 'investigateShared.endpoint.fileActions.',
          disabled(selection, context) {
            return context.get('fileDownloadButtonStatus').isSaveLocalAndFileAnalysisDisabled;
          },
          action(selection, context) {
            context.saveLocalCopy();
          }
        },
        {
          label: 'analyzeFile',
          order: 9,
          prefix: 'investigateShared.endpoint.fileActions.',
          disabled(selection, context) {
            return context.get('fileDownloadButtonStatus').isSaveLocalAndFileAnalysisDisabled;
          },
          action(selection, context) {
            context.analyzeFile();
          }
        }
      ];
      contextConf.push(...fileDownloadButtons);
    }

    if (this.get('showProcessAnalysis')) {
      contextConf.push({
        label: 'analyzeProcess',
        prefix: 'investigateHosts.process.',
        action(selection, context) {
          context.navigateToProcessAnalysis(context.get('item'));
        },
        order: 1
      });
    }


    if (this.get('showPivotToInvestigate') !== false) {
      const pivot = {
        label: 'pivotToInvestigate',
        order: 2,
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
        ],
        disabled(selection, context) {
          return (context.get('selections').length > 1);
        }
      };
      contextConf.push(pivot);
    }

    return contextConf.sortBy('order');
  }
});
