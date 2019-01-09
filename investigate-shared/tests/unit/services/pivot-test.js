import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Service from '@ember/service';
import sinon from 'sinon';

const reduxStub = Service.extend({
  state: {
    investigate: {
      serviceId: '12345',
      timeRange: {
        unit: 'Days',
        value: 7
      }
    }
  },
  getState() {
    return this.get('state');
  }
});


module('Unit | Service | Pivot', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    this.owner.register('service:redux', reduxStub);
  });

  test('pivoting to navigate page', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    const pivot = this.owner.lookup('service:pivot');
    pivot.pivotToInvestigate('machineIdentity.machineName,', [{ machineIdentity: { machineName: 'test' } }], null, 'EVENTS');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('12345'));
    assert.ok(actionSpy.args[0][0].includes('/investigate/events'));
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('pivoting to investigate navigate', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    const pivot = this.owner.lookup('service:pivot');
    pivot.pivotToInvestigate('machineIdentity.machineName,', [{ machineIdentity: { machineName: 'test' } }]);
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('12345'));
    assert.ok(actionSpy.args[0][0].includes('/navigate/query'));
    actionSpy.resetHistory();
    actionSpy.restore();
  });

});
