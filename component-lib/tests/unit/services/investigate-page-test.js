import { moduleFor, test } from 'ember-qunit';
import Service from '@ember/service';
import rsvp from 'rsvp';

const requestStub = Service.extend({
  promiseRequest: () => {
    return new rsvp.Promise((resolve) => resolve(true));
  }
});

const eventSettingsRequestStub = (value) => {
  return Service.extend({
    promiseRequest: () => {
      return new rsvp.Promise((resolve) =>
        resolve({
          data: { legacyEventsEnabled: value }
        }));
    }
  });
};

moduleFor('service:investigate-page', 'Unit | Service | investigate page', {
  // Specify the other units that are required for this test.
  needs: ['service:accessControl', 'service:flashMessages', 'service:i18n']
});

test('set default investigate page preference', function(assert) {
  this.register('service:request', requestStub);
  const service = this.subject();
  assert.ok(service);
  ['/eventanalysis', '/navigate', '/malware', '/hosts', '/files'].forEach(function(option) {
    assert.ok(service.selected === null || service.selected.key !== option);
    service.setDefaultInvestigatePage(option);
    assert.equal(service.selected.key, option);
    assert.ok(service.options.includes(service.selected));
  });
});

test('enable legacy events flag to display legacy events tab in investigate page', async function(assert) {
  this.register('service:request', eventSettingsRequestStub(true));

  const service = this.subject();
  assert.ok(service);

  await service.checkLegacyEventsEnabled();
  assert.ok(service.legacyEventsEnabled);
});

test('disable legacy events flag to hide legacy events tab in investigate page', async function(assert) {
  this.register('service:request', eventSettingsRequestStub(false));

  const service = this.subject();
  assert.ok(service);

  await service.checkLegacyEventsEnabled();
  assert.notOk(service.legacyEventsEnabled);
});
