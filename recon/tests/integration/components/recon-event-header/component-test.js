import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

const { run } = Ember;

import VisualActions from 'recon/actions/visual-creators';
import DataActions from 'recon/actions/data-creators';

moduleForComponent('recon-event-header', 'Integration | Component | recon event header', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('headerItems render correctly', function(assert) {
  const done = assert.async();
  this.get('redux').dispatch(DataActions.initializeRecon({ eventId: 1, endpointId: 2 }));
  this.render(hbs`{{recon-event-header}}`);

  run.later(() => {
    assert.equal(this.$('.header-item').length, 12);
    assert.equal(this.$('.header-item .name').first().text().trim(), 'device');
    assert.equal(this.$('.header-item .value').first().text().trim(), 'devicename');
    done();
  }, 200);
});

test('isHeaderOpen can toggle header visibility', function(assert) {
  const done = assert.async();

  this.get('redux').dispatch(DataActions.initializeRecon({ eventId: 1, endpointId: 2 }));
  this.get('redux').dispatch(VisualActions.toggleReconHeader());

  this.render(hbs`{{recon-event-header }}`);

  run.later(() => {
    assert.equal(this.$('.header-item').length, 0);
    this.get('redux').dispatch(VisualActions.toggleReconHeader());
    run.later(() => {
      assert.equal(this.$('.header-item').length, 12);
      done();
    }, 200);
  }, 200);
});