import Component from '@ember/component';

export default Component.extend({

  tagName: 'section',

  classNames: 'host_more_actions',

  moreOptions: [
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
  ]
});

