import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Service | deep-link', function(hooks) {
  setupTest(hooks);

  test('it will transition to host list page', function(assert) {
    assert.expect(1);
    const originOpen = window.open;
    window.open = (url) => {
      assert.ok(url.includes('/investigate/hosts'), 'valid link');
    };
    const deepLink = {
      location: 'HOST_LIST'
    };
    const service = this.owner.lookup('service:deep-link');
    service.transition(deepLink);
    window.open = originOpen;
  });

  test('it will transition to host details page', function(assert) {
    assert.expect(2);
    const originOpen = window.open;
    window.open = (url) => {
      assert.ok(url.includes('1'), 'valid id');
      assert.ok(url.includes('test-server'), 'valid server');
    };
    const deepLink = {
      location: 'HOST_DETAILS',
      params: ['id', 'serviceId']
    };

    const item = {
      id: '1',
      serviceId: 'test-server'
    };

    const service = this.owner.lookup('service:deep-link');
    service.transition(deepLink, item);
    window.open = originOpen;
  });

  test('it will transition to file list page', function(assert) {
    assert.expect(1);
    const originOpen = window.open;
    window.open = (url) => {
      assert.ok(url.includes('/investigate/hosts'), 'valid link');
    };
    const deepLink = {
      location: 'HOST_LIST'
    };
    const service = this.owner.lookup('service:deep-link');
    service.transition(deepLink);
    window.open = originOpen;
  });

  test('it will transition to file details page', function(assert) {
    assert.expect(3);
    const originOpen = window.open;
    window.open = (url) => {
      assert.ok(url.includes('1f'), 'valid id');
      assert.ok(url.includes('OVERVIEW'), 'valid id');
      assert.ok(url.includes('test-server'), 'valid server');
    };
    const deepLink = {
      location: 'FILE_DETAILS',
      params: ['id', 'serviceId']
    };

    const item = {
      id: '1f',
      serviceId: 'test-server'
    };

    const service = this.owner.lookup('service:deep-link');
    service.transition(deepLink, item);
    window.open = originOpen;
  });
});
