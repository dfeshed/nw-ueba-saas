import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import sinon from 'sinon';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | process-details/host-list-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });


  test('it renders the host-list-container', async function(assert) {
    setState({
      processAnalysis: {
        hostContext: {
          hostList: {
            data: [
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC', 'hostname': 'windows', 'score': 0 },
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAB', 'hostname': 'mac', 'score': 0 },
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAD', 'hostname': 'linux', 'score': 0 }
            ]
          }
        }
      }
    });
    await render(hbs`{{process-details/host-list-container}}`);
    assert.equal(findAll('.host-list-container').length, 1, 'it renders the component');
    assert.equal(findAll('[test-id=hostNameList]').length, 1, 'it renders host name component');
    assert.equal(findAll('.host-name').length, 3, 'Expected to render 3 host name');
    assert.equal(findAll('.score-detail').length, 3, 'Expected to render 3 Risk score');
  });

  test('clicking on the host name navigates to host details page', async function(assert) {
    assert.expect(3);
    setState({
      processAnalysis: {
        hostContext: {
          hostList: {
            data: [
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC', 'hostname': 'windows', 'score': 0 },
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAB', 'hostname': 'mac', 'score': 0 },
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAD', 'hostname': 'linux', 'score': 0 }
            ]
          }
        }
      }
    });

    await render(hbs`{{process-details/host-list-container}}`);
    const actionSpy = sinon.spy(window, 'open');
    await click(findAll('.host-name__link')[0]);
    assert.ok(actionSpy.calledOnce, 'Window.open is called');
    assert.ok(actionSpy.args[0][0].includes('0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC'), 'expected to include agent id');
    assert.ok(actionSpy.args[0][0].includes('/investigate/hosts/'), 'expected to include details in url');
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('clicking on the icon navigates to events page', async function(assert) {
    assert.expect(4);
    setState({
      processAnalysis: {
        hostContext: {
          hostList: {
            data: [
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC', 'hostname': 'windows', 'score': 0 },
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAB', 'hostname': 'mac', 'score': 0 },
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAD', 'hostname': 'linux', 'score': 0 }
            ]
          }
        }
      },
      investigate: {
        serviceId: 123456789,
        startTime: 1234567890,
        endTime: 1234567891
      }
    });

    await render(hbs`{{process-details/host-list-container}}`);
    const actionSpy = sinon.spy(window, 'open');
    await click(findAll('.pivot-to-investigate button')[0]);
    assert.ok(actionSpy.calledOnce, 'Window.open is called');
    assert.ok(actionSpy.args[0][0].includes('123456789'), 'expected to include agent id');
    assert.ok(actionSpy.args[0][0].includes('2009-02-13T23:31:30Z'));
    assert.ok(actionSpy.args[0][0].includes('/navigate/query'), 'expected to include details in url');
    actionSpy.resetHistory();
    actionSpy.restore();
  });


});
