import Ember from 'ember';
import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

const { run } = Ember;

import VisualActions from 'recon/actions/visual-creators';

moduleForComponent('recon-event-header', 'Integration | Component | recon event header', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('headerItems render correctly', function(assert) {
  this.set('headerItems', [{ name: 'foo', value: 'bar' }, { name: 'bar', value: 'baz' }]);
  this.render(hbs`{{recon-event-header  headerItems=headerItems}}`);

  assert.equal(this.$('.header-item').length, 2);
  assert.equal(this.$('.header-item .name').first().text().trim(), 'foo');
  assert.equal(this.$('.header-item .value').first().text().trim(), 'bar');
});

test('isHeaderOpen can toggle header visibility', function(assert) {
  const done = assert.async();
  this.set('headerItems', [{ name: 'foo', value: 'bar' }, { name: 'bar', value: 'baz' }]);
  this.render(hbs`{{recon-event-header headerItems=headerItems }}`);

  run(() => {
    this.get('redux').dispatch(VisualActions.toggleReconHeader());
    wait().then(() => {
      assert.equal(this.$('.header-item').length, 0);
      this.get('redux').dispatch(VisualActions.toggleReconHeader());
      wait().then(() => {
        assert.equal(this.$('.header-item').length, 2);
        done();
      });
    });
  });
});