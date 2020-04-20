import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

function setPermissionsTestingState(permissions) {
  permissions.set('permissionsAvailable', false);
  permissions.set('waiters', []);
  permissions.set('permissions', null);
}

module('Unit | Service | permissions', function(hooks) {
  setupTest(hooks);

  test('require calls a function when it has permission', function(assert) {
    assert.timeout(1500);
    assert.expect(0);
    const done = assert.async(2);
    const permissions = this.owner.lookup('service:permissions');
    setPermissionsTestingState(permissions);
    permissions.require('sdk.meta', () => {
      done();
    }, () => {
      throw new Error('Should not call the negative callback');
    });
    permissions.set('permissionsAvailable', true);
    permissions.permissionsLoaded(['sdk.meta', 'storedproc.execute']);
    // Give the callback we don't want time to call, so that we don't
    // prematurely pass the test
    setTimeout(() => {
      done();
    }, 1000);
  });

  test('require calls another function when it does not have permission', function(assert) {
    assert.timeout(1500);
    assert.expect(0);
    const done = assert.async(2);
    const permissions = this.owner.lookup('service:permissions');
    setPermissionsTestingState(permissions);
    permissions.require('sys.manage', () => {
      throw new Error('Should not call the positive callback');
    }, () => {
      done();
    });
    permissions.set('permissionsAvailable', true);
    permissions.permissionsLoaded(['sdk.meta', 'storedproc.execute']);
    // Give the callback we don't want time to call, so that we don't
    // prematurely pass the test
    setTimeout(() => {
      done();
    }, 1000);
  });

  test('require calls a function when it has permission and permissions are already fetched', function(assert) {
    assert.timeout(1500);
    assert.expect(0);
    const done = assert.async(2);
    const permissions = this.owner.lookup('service:permissions');
    setPermissionsTestingState(permissions);
    permissions.set('permissionsAvailable', true);
    permissions.permissionsLoaded(['sdk.meta', 'storedproc.execute']);
    permissions.require('sdk.meta', () => {
      done();
    }, () => {
      throw new Error('Should not call the negative callback');
    });
    // Give the callback we don't want time to call, so that we don't
    // prematurely pass the test
    setTimeout(() => {
      done();
    }, 1000);
  });

  test('require calls another function when it does not have permission and the permissions are already fetched', function(assert) {
    assert.timeout(1500);
    assert.expect(0);
    const done = assert.async(2);
    const permissions = this.owner.lookup('service:permissions');
    setPermissionsTestingState(permissions);
    permissions.set('permissionsAvailable', true);
    permissions.permissionsLoaded(['sdk.meta', 'storedproc.execute']);
    permissions.require('sys.manage', () => {
      throw new Error('Should not call the positive callback');
    }, () => {
      done();
    });
    // Give the callback we don't want time to call, so that we don't
    // prematurely pass the test
    setTimeout(() => {
      done();
    }, 1000);
  });
});
