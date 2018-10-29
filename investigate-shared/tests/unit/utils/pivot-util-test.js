import { module, test } from 'qunit';
import sinon from 'sinon';

import {
  navigateToInvestigateNavigate,
  navigateToInvestigateEventsAnalysis
} from 'investigate-shared/utils/pivot-util';

module('Unit | Utils | pivot to investigate', function() {

  test('navigateToInvestigateEventsAnalysis', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateEventsAnalysis({ metaName: 'test', metaValue: 'test' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('12345'));
    assert.ok(actionSpy.args[0][0].includes('/investigate/events'));
    actionSpy.reset();
    actionSpy.restore();
  });

  test('navigateToInvestigateNavigate', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateNavigate({ metaName: 'test', metaValue: 'test' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('12345'));
    assert.ok(actionSpy.args[0][0].includes('/navigate/query'));
    actionSpy.reset();
    actionSpy.restore();
  });

  test('it escapes the backslash for user name meta', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateEventsAnalysis({ metaName: 'userName', metaValue: 'corp\\test' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('username%2520%253D%2520%2522corp%255C%255Ctest')); // escaped
    actionSpy.reset();
    actionSpy.restore();
  });
});
