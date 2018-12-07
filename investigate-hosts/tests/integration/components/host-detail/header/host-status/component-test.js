import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';

const selectors = {
  summary: '.host-overview.host-item',
  scoreField: 'svg.rsa-risk-score',
  summaryFields: '.rsa-content-definition'
};

module('Integration | Component | host detail host-status', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
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
              scanStatus: 'idle'
            },
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
    assert.equal(findAll(selectors.summaryFields).length, 4, '4 summary fields present');

  });
});
