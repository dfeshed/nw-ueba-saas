import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import * as ACTION_TYPES from 'recon/actions/types';
import DataActions from 'recon/actions/data-creators';

const { run } = Ember;

moduleForComponent('recon-event-content', 'Integration | Component | recon event content', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('it renders child view', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch(DataActions.initializeRecon({ eventId: 1, endpointId: 2, meta: [['medium', 1]] }));
  this.render(hbs`{{recon-event-content}}`);
  run.later(() => {
    assert.equal(this.$().find('.recon-event-detail-packets').length, 1);
    done();
  }, 400);
});

test('it renders content error', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch({ type: ACTION_TYPES.CONTENT_RETRIEVE_FAILURE, payload: 2 });
  this.render(hbs`{{recon-event-content}}`);
  run.later(() => {
    assert.equal(this.$().find('.recon-error').length, 1);
    done();
  }, 400);
});

test('it renders spinner', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch({ type: ACTION_TYPES.CONTENT_RETRIEVE_STARTED });
  this.render(hbs`{{recon-event-content}}`);
  run.later(() => {
    assert.equal(this.$().find('.recon-loader').length, 1);
    done();
  }, 400);
});

test('log events redirect to text view', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch(DataActions.initializeRecon({ eventId: 1, endpointId: 2, meta: [['medium', 32]] }));
  this.render(hbs`{{recon-event-content}}`);
  run.later(() => {
    assert.equal(this.$('.recon-event-detail-text').length, 1, 'On the Text View');
    assert.equal(this.$('.recon-event-detail-packets').length, 0, 'Not on the Packet View');
    assert.equal(this.$('.recon-event-detail-files').length, 0, 'Not on the File View');
    done();
  }, 400);
});
