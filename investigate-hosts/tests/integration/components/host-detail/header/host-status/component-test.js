import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

const selectors = {
  summary: '.host-overview.host-item',
  scoreField: 'svg.rsa-risk-score',
  summaryFields: '.rsa-content-definition',
  rarIcon: '.rar-icon'
};

module('Integration | Component | host detail host-status', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('it renders', async function(assert) {
    const state = {
      endpoint: {
        overview: {
          hostDetails: {
            machine: {
              machineAgentId: 'A8F19AA5-A48D-D17E-2930-DF5F1A75A711',
              machineName: 'INENDHUPAAL1C',
              machineOsType: 'windows'
            },
            agentStatus: {
              lastSeenTime: '2018-10-26T04:00:22.898+0000',
              scanStatus: 'idle',
              lastSeen: 'RelayServer'
            },
            isAgentRoaming: true,
            score: 10
          }
        }
      }
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchReducer(this, Immutable.from(state));

    await render(hbs`{{host-detail/header/host-status}}`);

    assert.equal(findAll(selectors.summary).length, 1, 'summary is present');
    assert.equal(findAll(selectors.scoreField).length, 1, 'score is present');
    assert.equal(findAll(selectors.summaryFields).length, 3, '3 summary fields present');
    assert.equal(findAll(selectors.rarIcon).length, 1, 'RAR icon is present');
  });

  test('RAR icon does not render if last seen is not RelayServer', async function(assert) {
    const state = {
      endpoint: {
        overview: {
          hostDetails: {
            machine: {
              machineAgentId: 'A8F19AA5-A48D-D17E-2930-DF5F1A75A711',
              machineName: 'INENDHUPAAL1C',
              machineOsType: 'windows'
            },
            agentStatus: {
              lastSeenTime: '2018-10-26T04:00:22.898+0000',
              scanStatus: 'idle',
              lastSeen: 'EndpointServer'
            },
            isAgentRoaming: false,
            score: 10
          }
        }
      }
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchReducer(this, Immutable.from(state));

    await render(hbs`{{host-detail/header/host-status}}`);

    assert.equal(findAll(selectors.rarIcon).length, 0, 'RAR icon is not present');
  });

  test('for insight agent risk score should displayed as N/A', async function(assert) {
    const state = {
      endpoint: {
        machines: {
          focusedHost: {
            machineIdentity: {
              agentMode: 'insights'
            }
          }
        }
      }
    };
    patchReducer(this, Immutable.from(state));

    await render(hbs`{{host-detail/header/host-status}}`);

    assert.equal(findAll(selectors.summaryFields)[0].textContent.trim(), 'N/A', 'N/A should be displayed');

  });

  test('it checks for host name and OS', async function(assert) {
    new ReduxDataHelper(setState)
      .hostOverview({
        machineIdentity: { machineOsType: 'mac', machineName: 'XYZ' },
        agentStatus: {
          agentId: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
          lastSeenTime: '2019-05-09T09:22:09.713+0000',
          scanStatus: 'scanPending'
        }
      })
      .build();
    await render(hbs`{{host-detail/header/host-status}}`);
    assert.equal(find('.host-name').textContent.trim(), 'XYZ', 'Rendered the hostname');
    assert.equal(find('.osType').textContent.trim(), 'mac', 'Rendered the OS');
  });

  test('Renders pivot to analysis and more action icons', async function(assert) {
    new ReduxDataHelper(setState)
      .hostOverview({
        machineIdentity: { machineOsType: 'mac', machineName: 'XYZ' },
        agentStatus: {
          agentId: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
          lastSeenTime: '2019-05-09T09:22:09.713+0000',
          scanStatus: 'scanPending'
        }
      }).listOfServices([
        {
          machineIdentity: {
            machineName: 'RemDbgDrv'
          },
          id: 'A0351965-30D0-2201-F29B-FDD7FD32EB21',
          version: '11.4.0.0',
          managed: true,
          serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0'
        }
      ])
      .serviceId('e7ec4c9f-0429-4fdc-8ab5-51fe958a9d87')
      .build();
    await render(hbs`{{host-detail/header/host-status}}`);
    assert.equal(findAll('.pivot-to-investigate').length, 1, 'Analyze events present');
    assert.equal(findAll('.host_more_actions').length, 1, 'more actions present');
  });
});
