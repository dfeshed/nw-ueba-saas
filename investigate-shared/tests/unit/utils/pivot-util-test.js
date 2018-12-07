import { module, test } from 'qunit';
import sinon from 'sinon';

import {
  navigateToInvestigateNavigate,
  navigateToInvestigateEventsAnalysis,
  serializeQueryParams
} from 'investigate-shared/utils/pivot-util';

module('Unit | Utils | pivot to investigate', function() {

  const params = {
    et: 0,
    eid: 1,
    mf: 'a%3D\'a/%3Db%3D/a\'',
    mps: 'default',
    rs: 'max',
    sid: 2,
    st: 3
  };

  test('navigateToInvestigateEventsAnalysis', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateEventsAnalysis({ metaName: 'test', metaValue: 'test' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('12345'));
    assert.ok(actionSpy.args[0][0].includes('/investigate/events'));
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('navigateToInvestigateNavigate', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateNavigate({ metaName: 'test', metaValue: 'test' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('12345'));
    assert.ok(actionSpy.args[0][0].includes('/navigate/query'));
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('it escapes the backslash for user name meta', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateEventsAnalysis({ metaName: 'userName', metaValue: 'corp\\test' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('username%2520%253D%2520%2522corp%255C%255Ctest')); // escaped
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('navigateToInvestigateEventsAnalysis adds the additional filters passed', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateEventsAnalysis({ metaName: 'test', metaValue: 'test', additionalFilter: 'category="network event"' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('network%2520event'));
    assert.ok(actionSpy.args[0][0].includes('/investigate/events'));
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('serializeQueryParams gives the correct URI string', function(assert) {
    assert.expect(1);
    const result = serializeQueryParams(params);
    assert.equal(result, 'et=0&eid=1&mf=a%3D\'a/%3Db%3D/a\'&mps=default&rs=max&sid=2&st=3', 'serializeQueryParams gives the correct URL string');
  });
});
