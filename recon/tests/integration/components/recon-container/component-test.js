import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

moduleForComponent('recon-container', 'Integration | Component | recon container', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:recon-event-actionbar/export-packet', 'i18n', 'service:i18n');
    this.registry.injection('component:recon-event-detail/text-content', 'i18n', 'service:i18n');
    this.registry.injection('component:recon-event-detail/single-text', 'i18n', 'service:i18n');
  }
});


test('recon container in standalone mode', function(assert) {
  assert.expect(1);
  this.set('eventId', '5');
  this.set('endpointId', '555d9a6fe4b0d37c827d402e');

  this.render(hbs`{{recon-container eventId=eventId endpointId=endpointId}}`);

  assert.equal(this.$('.header-button').length, 0, 'Recon container in standalone mode does not show \'close and expand\' button');


});

test('recon container in investigate-events', function(assert) {
  assert.expect(1);

  this.set('eventId', '5');
  this.set('endpointId', '555d9a6fe4b0d37c827d402e');
  this.set('closeAction', () => {});
  this.set('expandAction', () => {});

  this.render(hbs`{{recon-container eventId=eventId endpointId=endpointId closeAction=(action closeAction) expandAction=(action expandAction)}}`);

  return wait().then(() => {
    assert.equal(this.$('.header-button').length, 2, 'Recon container when provided with a closeAction does not run in standalone mode and has \'close and expand\' buttons');

  });


});
