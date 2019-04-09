import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Service | global preferences', function(hooks) {
  setupTest(hooks);

  test('it sets preferences and triggers event', function(assert) {
    assert.expect(5);
    const service = this.owner.lookup('service:global-preferences');

    service.on('rsa-application-user-preferences-did-change', () => {
      assert.ok(true);
    });
    service.set('timeFormat', { selected: { format: 'timeFormat' } });
    service.set('dateFormat', { selected: { format: 'dateFormat' } });
    service.set('timezone', { selected: { zoneId: 'timezone' } });
    service.set('i18n', { locale: 'locale' });

    assert.deepEqual(service.get('preferences'), {
      timeFormat: 'timeFormat',
      dateFormat: 'dateFormat',
      timezone: 'timezone',
      locale: 'locale'
    });
  });

});
