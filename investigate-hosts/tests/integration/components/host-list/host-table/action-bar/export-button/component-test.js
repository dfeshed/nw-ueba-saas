import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolverFor from '../../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';

let setState;
moduleForComponent('host-list/host-table/action-bar/export-button', 'Integration | Component | host table action bar export button', {
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

test('it renders host table action bar export button', function(assert) {
  this.render(hbs`{{host-list/host-table/action-bar/export-button}}`);
  assert.equal(this.$('.rsa-form-button-wrapper').length, 1, 'export button is rendered');
});

test('it renders host table action bar export button when exportStatus is streaming', function(assert) {
  new ReduxDataHelper(setState)
    .hostExportStatus('streaming')
    .build();
  this.render(hbs`{{host-list/host-table/action-bar/export-button}}`);
  assert.equal(this.$('.rsa-form-button-wrapper button .rsa-loader').length, 1, 'loader is rendered');
  assert.equal(this.$('.rsa-form-button-wrapper button')[0].innerText.trim(), 'Downloading', 'downloading export button is rendered');
});

test('it renders host table action bar export button when exportStatus is completed', function(assert) {
  new ReduxDataHelper(setState)
    .hostExportStatus('completed')
    .build();
  this.render(hbs`{{host-list/host-table/action-bar/export-button}}`);
  assert.equal(this.$('.rsa-form-button-wrapper button .rsa-loader').length, 0, 'loader is not present');
  assert.equal(this.$('.rsa-form-button-wrapper button')[0].innerText.trim(), 'Export to CSV', 'default export button is rendered');
});