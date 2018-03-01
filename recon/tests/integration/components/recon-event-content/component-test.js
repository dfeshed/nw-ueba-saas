import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import DataHelper from '../../../helpers/data-helper';

moduleForComponent('recon-event-content', 'Integration | Component | recon event content', {
  integration: true,
  beforeEach() {
    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.hasReconAccess', true);

    this.registry.injection('component:recon-event-content', 'i18n', 'service:i18n');
    this.registry.injection('component:recon-event-detail/single-text', 'i18n', 'service:i18n');
    this.inject.service('redux');
    initialize(this);
  }
});

test('it renders child view', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData()
    .setViewToText()
    .populateTexts();
  this.render(hbs`{{recon-event-content accessControl=accessControl}}`);
  return wait().then(() => {
    assert.equal(this.$().find('.recon-event-detail-text').length, 1, 'Child view missing');
  });
});

test('it renders content error', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .contentRetrieveFailure(2);
  this.render(hbs`{{recon-event-content accessControl=accessControl}}`);
  return wait().then(() => {
    assert.equal(this.$().find('.rsa-panel-message').length, 1, 'Content error not set');
  });
});

test('it renders spinner', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .contentRetrieveStarted();
  this.render(hbs`{{recon-event-content accessControl=accessControl}}`);
  return wait().then(() => {
    assert.equal(this.$().find('.recon-loader').length, 1, 'Spinner missing');
  });
});

test('log events redirect to text view', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData({ eventId: 1, endpointId: 2, meta: [['medium', 32]] })
    .setViewToText()
    .populateTexts();

  this.render(hbs`{{recon-event-content accessControl=accessControl}}`);
  return wait().then(() => {
    assert.equal(this.$('.recon-event-detail-text').length, 1, 'On the Text View');
    assert.equal(this.$('.recon-event-detail-packets').length, 0, 'Not on the Packet View');
    assert.equal(this.$('.recon-event-detail-files').length, 0, 'Not on the File View');
  });
});

test('displays correct error when missing permissionss', function(assert) {
  this.set('accessControl.hasReconAccess', false);

  new DataHelper(this.get('redux'))
    .initializeData({ eventId: 1, endpointId: 2, meta: [['medium', 32]] })
    .setViewToText();

  this.render(hbs`{{recon-event-content accessControl=accessControl}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-panel-message').text().trim(), 'You do not have the required permissions to view this content.', 'Error is displayed with correct message');
  });
});
