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
    assert.ok(actionSpy.args[0][0].includes('username%20%3D%20%22corp%5C%5Ctest')); // escaped
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('navigateToInvestigateEventsAnalysis adds the additional filters passed', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateEventsAnalysis({ metaName: 'test', metaValue: 'test', additionalFilter: 'category="network event"' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('network%20event'));
    assert.ok(actionSpy.args[0][0].includes('/investigate/events'));
    actionSpy.resetHistory();
    actionSpy.restore();
  });

  test('serializeQueryParams gives the correct URI string', function(assert) {
    assert.expect(1);
    const result = serializeQueryParams(params);
    assert.equal(result, 'et=0&eid=1&mf=a%3D\'a/%3Db%3D/a\'&mps=default&rs=max&sid=2&st=3', 'serializeQueryParams gives the correct URL string');
  });

  test('if meta is checksum256 it adds the file name to query', function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    navigateToInvestigateEventsAnalysis({ metaName: 'checksumSha256', itemList: [{ fileName: 'test_file.exe', checksumSha256: 'test' }], additionalFilter: 'category="network event"' }, '12345', { unit: 'days', value: 2 }, 'UTC');
    assert.ok(actionSpy.calledOnce);
    assert.ok(actionSpy.args[0][0].includes('test_file.exe'));
    assert.ok(actionSpy.args[0][0].includes('filename.all'));
    assert.ok(actionSpy.args[0][0].includes('/investigate/events'));
    actionSpy.resetHistory();
    actionSpy.restore();
  });
});
