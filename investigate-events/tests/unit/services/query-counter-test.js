import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Service | query-counter', function(hooks) {
  setupTest(hooks);

  test('it exists', function(assert) {
    const service = this.owner.lookup('service:queryCounter');
    assert.ok(service);
  });

  test('it sets counts to defaults on init', function(assert) {

    const service = this.owner.lookup('service:queryCounter');
    assert.equal(service.metaTabCount, 0, 'metaTabCount not set to default?');
    assert.equal(service.recentQueryTabCount, 0, 'recentQueryTabCount not set to default?');
    assert.notOk(service.isExpectingResponse, 'isExpectingResponse not set to default?');
  });

  test('it sets metTabCount with the value being sent', function(assert) {

    const service = this.owner.lookup('service:queryCounter');
    service.setMetaTabCount(2);
    assert.equal(service.metaTabCount, 2, 'Count not being set in service');
  });

  test('it sets metTabCount with default when no value is sent', function(assert) {

    const service = this.owner.lookup('service:queryCounter');
    service.setMetaTabCount();
    assert.equal(service.metaTabCount, 0, 'Count not being set in service');
  });

  test('it sets recentQueryTabCount with the value being sent', function(assert) {

    const service = this.owner.lookup('service:queryCounter');
    service.setRecentQueryTabCount(2);
    assert.equal(service.recentQueryTabCount, 2, 'Count not being set in service');
  });

  test('it sets recentQueryTabCount with default when no value is sent', function(assert) {

    const service = this.owner.lookup('service:queryCounter');
    service.setRecentQueryTabCount();
    assert.equal(service.recentQueryTabCount, 0, 'Count not being set in service');
  });

  test('it resets all counts when resetAll is called', function(assert) {

    const service = this.owner.lookup('service:queryCounter');
    service.resetAllTabCounts();
    assert.equal(service.metaTabCount, 0, 'metaTabCount not set to default?');
    assert.equal(service.recentQueryTabCount, 0, 'recentQueryTabCount not set to default?');
    assert.notOk(service.isExpectingResponse, 'isExpectingResponse not set to default?');
  });

  test('it sets isExpectingResponse flag', function(assert) {

    const service = this.owner.lookup('service:queryCounter');
    service.setResponseFlag(true);
    assert.ok(service.isExpectingResponse, 'isExpectingResponse is not being reflected in the service');
  });
});
