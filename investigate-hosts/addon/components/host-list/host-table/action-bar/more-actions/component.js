import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

export default Component.extend({

  tagName: 'section',

  classNames: 'host_more_actions',

  accessControl: service(),

  @computed('isMFTEnabled', 'selectedHostList', 'accessControl')
  moreOptions() {
    const systemMemoryDump = [
      {
        panelId: 'panel4',
        name: 'investigateShared.endpoint.fileActions.downloadSystemDump',
        buttonId: 'downloadSystemDump-button'
      }
    ];
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
        panelId: 'panel3',
        name: 'investigateShared.endpoint.fileActions.downloadMFT',
        buttonId: 'downloadMFT-button',
        divider: true
      }
    ];
    if (this.get('isMFTEnabled').isDisplayed && (this.get('selectedHostList').length === 1) && this.get('accessControl.endpointCanManageFiles')) {
      moreActionOptions.push(...mft, ...systemMemoryDump);
    }
    return moreActionOptions;
  }
});

