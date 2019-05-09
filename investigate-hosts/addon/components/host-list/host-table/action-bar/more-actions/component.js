import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend({

  tagName: 'section',

  classNames: 'host_more_actions',

  @computed('isMFTEnabled', 'selectedHostList')
  moreOptions() {
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
        buttonId: 'downloadMFT-button'
      }
    ];
    if (this.get('isMFTEnabled').isDisplayed && (this.get('selectedHostList').length === 1)) {
      moreActionOptions.push(...mft);
    }
    return moreActionOptions;
  }
});

