import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import copyToClipboard from 'component-lib/utils/copy-to-clipboard';

const keyMapping = {
  ipv4: 'machineIpv4',
  ipv6: 'machineIpv6'
};


export default Component.extend({

  layout,

  tagName: 'hbox',

  classNames: 'col-xs-6 col-md-7',

  classNameBindings: ['property-value'],

  pivot: service(),

  @computed('property')
  contextItems(property) {
    const cntx = this;
    const key = keyMapping[property.field];
    const item = {};
    const machineName = cntx.get('hostName');
    if (property.value) {
      item[key] = property.value[0];
    }
    // Adding machine name to the item
    item.machineName = machineName;

    return [
      {
        label: 'Copy',
        action([item]) {
          copyToClipboard(item);
        }
      },
      {
        label: 'pivotToInvestigate',
        prefix: 'investigateShared.endpoint.fileActions.',
        subActions: [
          {
            label: 'consoleEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              cntx.get('pivot').pivotToInvestigate(key, item, 'Console Event');
            }
          },
          {
            label: 'networkEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              cntx.get('pivot').pivotToInvestigate(key, item, 'Network Event');
            }
          },
          {
            label: 'fileEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              cntx.get('pivot').pivotToInvestigate(key, item, 'File Event');
            }
          },
          {
            label: 'processEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              cntx.get('pivot').pivotToInvestigate(key, item, 'Process Event');
            }
          },
          {
            label: 'registryEvents',
            prefix: 'investigateShared.endpoint.fileActions.',
            action() {
              cntx.get('pivot').pivotToInvestigate(key, item, 'Registry Event');
            }
          }
        ]
      }
    ];
  },

  @computed('property')
  propertyValueLength({ value }) {
    return Array.isArray(value) ? `(${value.length})` : '';
  },

  actions: {
    navigateToUEBA(user) {
      if (user.includes('\\')) {
        user = user.split('\\')[1];
      }
      const path = `${window.location.origin}/investigate/entities?ueba=/username/${user}`;
      window.open(path);

    }
  }
});
