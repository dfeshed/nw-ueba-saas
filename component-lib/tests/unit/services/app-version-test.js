import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Service | app version', function(hooks) {
  setupTest(hooks);

  test('it sets marketingVersion', function(assert) {
    const service = this.owner.lookup('service:app-version');

    service.set('version', '11.0.0.0-7206.5.21dd2e7');
    assert.equal(service.get('marketingVersion'), '11.0.0.0');
  });

  test('it sets minServiceVersion for 11.0', function(assert) {
    const service = this.owner.lookup('service:app-version');

    service.set('version', '11.0.0.0-7206.5.21dd2e7');
    assert.equal(service.get('minServiceVersion'), '11.0');
  });

  test('it sets minServiceVersion for 11.1', function(assert) {
    const service = this.owner.lookup('service:app-version');

    service.set('version', '11.1.0.0-7206.5.21dd2e7');
    assert.equal(service.get('minServiceVersion'), '11.1');
  });

  test('it sets minServiceVersion for 11.2', function(assert) {
    const service = this.owner.lookup('service:app-version');

    service.set('version', '11.2.0.0-7206.5.21dd2e7');
    assert.equal(service.get('minServiceVersion'), '11.1');
  });

  test('it sets minServiceVersion for 11.3, etc', function(assert) {
    const service = this.owner.lookup('service:app-version');

    service.set('version', '11.3.0.0-7206.5.21dd2e7');
    assert.equal(service.get('minServiceVersion'), '11.2');
  });

});
