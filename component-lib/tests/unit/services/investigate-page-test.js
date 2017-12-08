import { moduleFor, test } from 'ember-qunit';
import Service from 'ember-service';
import rsvp from 'rsvp';

const requestStub = Service.extend({
  promiseRequest: () => {
    return new rsvp.Promise((resolve) => resolve(true));
  }
});


moduleFor('service:investigate-page', 'Unit | Service | investigate page', {
  // Specify the other units that are required for this test.
  needs: ['service:accessControl', 'service:flashMessages', 'service:i18n'],
  beforeEach() {
    this.register('service:request', requestStub);
  }
});

// Replace this with your real tests.
test('set default investigate page preference', function(assert) {
  const service = this.subject();
  assert.ok(service);
  ['/events', '/eventanalysis', '/navigate', '/malware', '/hosts', '/files'].forEach(function(option) {
    assert.ok(service.selected === null || service.selected.key !== option);
    service.setDefaultInvestigatePage(option);
    assert.equal(service.selected.key, option);
    assert.ok(service.options.contains(service.selected));
  });
});
