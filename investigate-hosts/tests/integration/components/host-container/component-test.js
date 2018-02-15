import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';

let setState;
moduleForComponent('host-container', 'Integration | Component | host container', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders host container', function(assert) {
  this.render(hbs`{{host-container}}`);
  assert.equal(this.$('.host-container').length, 1, 'host container rendered');
});

test('it renders host container detail', function(assert) {
  new ReduxDataHelper(setState)
    .hasMachineId(true)
    .build();
  this.render(hbs`{{host-container}}`);
  assert.equal(this.$('.host-container-detail').length, 1, 'host container detail rendered');
});

test('it renders host container list', function(assert) {
  new ReduxDataHelper(setState)
    .hasMachineId(false)
    .build();
  this.render(hbs`{{host-container}}`);
  assert.equal(this.$('.host-container-list').length, 1, 'host container list rendered');
});