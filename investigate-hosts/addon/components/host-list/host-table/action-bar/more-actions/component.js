import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

export default Component.extend({

  tagName: 'section',

  classNames: 'host_more_actions',

  accessControl: service(),

  @computed('isMFTEnabled', 'selectedHostList', 'accessControl', 'hostDetails')
  moreOptions() {
    let subNavItem = {};
    const systemMemoryDump = [
      {
        panelId: 'panel5',
        name: 'investigateShared.endpoint.fileActions.downloadSystemDump',
        buttonId: 'downloadSystemDump-button'
      }
    ];
    if (this.get('hostDetails').isIsolated) {
      subNavItem = {
        modalName: 'release',
        name: 'investigateHosts.networkIsolation.menu.releaseFromIsolation',
        buttonId: 'release-isolation-button',
        isDisabled: false
      };
    } else {
      subNavItem = {
        modalName: 'isolate',
        name: 'investigateHosts.networkIsolation.menu.isolate',
        buttonId: 'isolation-button',
        isDisabled: false
      };
    }
    // Separating network isolation as an additional check will be added for this in future.
    const networkIsolation = [{
      panelId: 'panel3',
      divider: true,
      name: 'investigateHosts.networkIsolation.menu.networkIsolation',
      buttonId: 'isolation-button',
      isDisabled: false,
      subItems: [
        subNavItem,
        {
          modalName: 'edit',
          name: 'investigateHosts.networkIsolation.menu.edit',
          buttonId: 'isolation-button',
          isDisabled: !this.get('hostDetails').isIsolated
        }
      ]
    }];
    const moreActionOptions = [
      {
        panelId: 'panel1',
        name: 'investigateHosts.hosts.button.resetRiskScore',
        buttonId: 'reset-button'
      },
      {
        panelId: 'panel2',
        name: 'investigateHosts.hosts.button.delete',
        buttonId: 'delete-button'
      }
    ];
    const mft = [
      {
        panelId: 'panel4',
        name: 'investigateShared.endpoint.fileActions.downloadMFT',
        buttonId: 'downloadMFT-button',
        divider: true
      }
    ];
    if ((this.get('selectedHostList').length === 1) && this.get('accessControl.endpointCanManageFiles')) {
      if (this.get('hostDetails').isIsolationEnabled) {
        moreActionOptions.push(...networkIsolation);
      }
      if (this.get('isMFTEnabled').isDisplayed) {
        moreActionOptions.push(...mft, ...systemMemoryDump);
      }
    }
    return moreActionOptions;
  },
  actions: {
    displayIsolationModal(item) {
      this.showIsolationModal(item);
    }
  }
});

