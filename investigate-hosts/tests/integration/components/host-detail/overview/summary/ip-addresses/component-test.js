import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { linux } from '../../../../state/overview.hostdetails';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

import engineResolverFor from '../../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';

let setState;
moduleForComponent('host-detail/overview/summary/ip-addresses', 'Integration | Component | endpoint host detail/overview/summary/ip-addresses', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    setState = (machine) => {
      const { overview } = machine;
      const state = Immutable.from({ endpoint: { overview } });
      applyPatch(state);
      this.inject.service('redux');
    };
  },

  afterEach() {
    revertPatch();
  }
});

test('it renders multiple Ip addresses', function(assert) {
  setState(linux);
  this.render(hbs`
    {{host-detail/overview/summary/ip-addresses}}
  `);

  return wait().then(() => {
    const networkInterfacesCount = this.$('hbox.host-ip-addresses.host-content__ip-details').length;
    assert.equal(networkInterfacesCount, 1, 'number of network interfaces');
  });
});

test(' it renders when machine is undefined', function(assert) {
  const agent = {
    overview: {
      hostDetails: {}
    }
  };

  setState(agent);
  this.render(hbs`
    {{host-detail/overview/summary/ip-addresses}}
  `);

  return wait().then(() => {
    const networkInterfacesCount = this.$('hbox.host-ip-addresses.host-content__ip-details').length;
    assert.equal(networkInterfacesCount, 0, 'number of network interfaces');
  });
});

test('it renders when ipv4 is 127.0.0.1', function(assert) {
  const agent = {
    overview: {
      hostDetails: {
        machine: {
          networkInterfaces: [
            {
              name: 'local',
              ipv4: ['127.0.0.1'],
              ipv6: ['::1']
            }
          ]
        }
      }
    }
  };

  setState(agent);
  this.render(hbs`
    {{host-detail/overview/summary/ip-addresses}}
  `);

  return wait().then(() => {
    const networkInterfacesCount = this.$('hbox.host-ip-addresses.host-content__ip-details').length;
    assert.equal(networkInterfacesCount, 0, 'number of network interfaces');
  });
});

test('it renders when ipv4 is undefined', function(assert) {
  const agent = {
    overview: {
      hostDetails: {
        machine: {
          networkInterfaces: [
            {
              name: 'em03',
              ipv6: ['fe80::250:56ff:fe01:2bb5'],
              macAddress: '00:50:56:01:47:01'
            },
            {
              name: 'emc2',
              ipv4: ['10.40.12.13', '10.40.12.10'],
              ipv6: ['fe80::250:56ff:fe01:2b12'],
              macAddress: '00:50:56:01:47:05'
            }
          ]
        }
      }
    }
  };

  setState(agent);
  this.render(hbs`
    {{host-detail/overview/summary/ip-addresses}}
  `);

  return wait().then(() => {
    const networkInterfacesCount = this.$('hbox.host-ip-addresses.host-content__ip-details').length;
    assert.equal(networkInterfacesCount, 1, 'number of network interfaces');
  });
});
