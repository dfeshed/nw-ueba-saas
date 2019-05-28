import Service from '@ember/service';
import Evented from '@ember/object/evented';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const eventBusStub = Service.extend(Evented, {});
const layoutStub = Service.extend();

module('Integration | Component | rsa-application-content', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.register('service:event-bus', eventBusStub);
    this.eventBus = this.owner.lookup('service:event-bus');
    this.owner.register('service:layout', layoutStub);
    this.layoutService = this.owner.lookup('service:layout');
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-application-content}}`);
    const content = this.$().find('.rsa-application-content').length;
    assert.equal(content, 1);
  });

  test('it includes the proper classes when hasBlur is true', async function(assert) {
    await render(hbs `{{#rsa-application-content hasBlur=true}}foo{{/rsa-application-content}}`);
    const content = this.$().find('.rsa-application-content').first();
    assert.ok(content.hasClass('has-blur'));
  });

  test('it updates hasBlur when rsa-application-modal-did-open is triggered', async function(assert) {
    this.set('initialBlur', false);
    await render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
    assert.equal(this.get('initialBlur'), false);
    this.get('eventBus').trigger('rsa-application-modal-did-open');

    const that = this;
    return settled().then(function() {
      assert.equal(that.get('initialBlur'), true);
    });
  });

  test('it updates hasBlur when rsa-application-user-preferences-panel-will-toggle is triggered', async function(assert) {
    this.set('initialBlur', false);
    this.set('layoutService.userPreferencesActive', true);
    await render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
    assert.equal(this.get('initialBlur'), false);
    this.get('eventBus').trigger('rsa-application-user-preferences-panel-will-toggle');

    return settled().then(() => {
      assert.equal(this.get('initialBlur'), true);
    });
  });

  test('it updates hasBlur when rsa-application-incident-queue-panel-will-toggle is triggered', async function(assert) {
    this.set('initialBlur', false);
    this.set('layoutService.incidentQueueActive', true);
    await render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
    assert.equal(this.get('initialBlur'), false);
    this.get('eventBus').trigger('rsa-application-incident-queue-panel-will-toggle');

    const that = this;
    return settled().then(function() {
      assert.equal(that.get('initialBlur'), true);
    });
  });

  test('it removes hasBlur is true when both userPreferencesActive and incidentQueueActive are false', async function(assert) {
    this.set('initialBlur', true);
    this.set('layoutService.incidentQueueActive', false);
    this.set('layoutService.userPreferencesActive', false);
    await render(hbs `{{#rsa-application-content hasBlur=initialBlur}}foo{{/rsa-application-content}}`);
    assert.equal(this.get('initialBlur'), true);
    this.get('eventBus').trigger('rsa-application-user-preferences-panel-will-toggle');

    const that = this;
    return settled().then(function() {
      assert.equal(that.get('initialBlur'), false);
    });
  });
});
