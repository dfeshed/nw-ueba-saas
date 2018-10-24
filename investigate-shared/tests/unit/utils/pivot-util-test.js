import { module, test } from 'qunit';
import sinon from 'sinon';

import {
  navigateToInvestigateNavigate,
  navigateToInvestigateEventsAnalysis
} from 'investigate-shared/utils/pivot-util';

module('Unit | Utils | pivot to investigate', function() {

  test('navigateToInvestigateNavigate', function(assert) {
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
});
