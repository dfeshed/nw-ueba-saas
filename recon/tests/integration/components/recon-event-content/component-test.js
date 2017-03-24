import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import DataHelper from '../../../helpers/data-helper';

moduleForComponent('recon-event-content', 'Integration | Component | recon event content', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:recon-event-content', 'i18n', 'service:i18n');
    this.inject.service('redux');
  }
});

test('it renders child view', function(assert) {
  new DataHelper(this.get('redux')).initializeData();
  this.render(hbs`{{recon-event-content}}`);
  return wait().then(() => {
    assert.equal(this.$().find('.recon-event-detail-packets').length, 1);
  });
});

test('it renders content error', function(assert) {
  new DataHelper(this.get('redux')).contentRetrieveFailure(2);
  this.render(hbs`{{recon-event-content}}`);
  return wait().then(() => {
    assert.equal(this.$().find('.recon-error').length, 1);
  });
});

test('it renders spinner', function(assert) {
  new DataHelper(this.get('redux')).contentRetrieveStarted();
  this.render(hbs`{{recon-event-content}}`);
  return wait().then(() => {
    assert.equal(this.$().find('.recon-loader').length, 1);
  });
});

test('log events redirect to text view', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData({ eventId: 1, endpointId: 2, meta: [['medium', 32]] })
    .setEventTypeToLog()
    .setViewToText();

  this.render(hbs`{{recon-event-content}}`);
  return wait().then(() => {
    assert.equal(this.$('.recon-event-detail-text').length, 1, 'On the Text View');
    assert.equal(this.$('.recon-event-detail-packets').length, 0, 'Not on the Packet View');
    assert.equal(this.$('.recon-event-detail-files').length, 0, 'Not on the File View');
  });
});
