import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import computed from 'ember-computed-decorators';
import layout from './template';
import { inject as service } from '@ember/service';

/**
 * Extension of the Data Table default row class for supporting focus on the row
 * @public
 */
export default DataTableBodyRow.extend({
  layout,

  classNameBindings: ['isRowChecked'],

  updatedContextConfBackup: [],

  initialContextConfBackup: [],

  accessControl: service(),

  @computed('item', 'selections')
  isRowChecked(item, selections = []) {
    const isSelected = selections.findBy('id', item.id);
    return !!isSelected;
  },

  @computed('item')
  contextItems() {
    let subNavItem = {};
    const { agentStatus = {}, groupPolicy = {} } = this.get('item');
    const { isolationAllowed = false } = groupPolicy;
    const { isolationStatus = {} } = agentStatus;
    const { isolated = false } = isolationStatus;
    const mft = [{
      label: 'downloadMFT',
      order: 6,
      prefix: 'investigateShared.endpoint.fileActions.',
      showDivider: true,
      action(selection, context) {
        context.requestMFTDownload();
      },
      disabled(selection, context) {
        return context.get('isAgentMigrated');
      }
    }];
    if (isolated) {
      subNavItem = {
        modalName: 'release',
        label: 'releaseFromIsolation',
        prefix: 'investigateHosts.networkIsolation.menu.',
        buttonId: 'release-isolation-button',
        action(selection, context) {
          context.showIsolationModal('release');
        },
        disabled(selection, context) {
          return context.get('isAgentMigrated');
        }
      };
    } else {
      subNavItem = {
        modalName: 'isolate',
        label: 'isolate',
        prefix: 'investigateHosts.networkIsolation.menu.',
        buttonId: 'isolation-button',
        action(selection, context) {
          context.showIsolationModal('isolate');
        },
        disabled(selection, context) {
          return context.get('hostDetails').isIsolated;
        }
      };
    }
    // Separating network isolation as an additional check will be added for this in future.
    const networkIsolation = [{
      showDivider: true,
      label: 'networkIsolation',
      prefix: 'investigateHosts.networkIsolation.menu.',
      buttonId: 'isolation-button',
      disabled(selection, context) {
        return context.get('isAgentMigrated');
      },
      order: 5,
      subActions: [
        subNavItem,
        {
          modalName: 'edit',
          label: 'edit',
          prefix: 'investigateHosts.networkIsolation.menu.',
          buttonId: 'isolation-button',
          action(selection, context) {
            context.showIsolationModal('edit');
          },
          disabled(selection, context) {
            return !context.get('hostDetails').isIsolated;
          }
        }
      ]
    }];
    const systemDump = [{
      label: 'downloadSystemDump',
      order: 7,
      prefix: 'investigateShared.endpoint.fileActions.',
      action(selection, context) {
        context.requestSystemDumpDownload();
      },
      disabled(selection, context) {
        return context.get('isAgentMigrated');
      }
    }];

    const contextConf = [
      {
        label: 'resetRiskScore',
        order: 8,
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
          return context.get('isScanStopButtonDisabled');
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
    if (this.get('item').isMFTEnabled && this.get('accessControl.endpointCanManageFiles')) {
      contextConf.push(...mft, ...systemDump);
      if (isolationAllowed) {
        contextConf.push(...networkIsolation);
      }
    }
    return contextConf.sortBy('order');
  }

});
