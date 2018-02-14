import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import files from '../../../state/files';
import wait from 'ember-test-helpers/wait';

let setState;
moduleForComponent('files-toolbar/export-button', 'Integration | Component | files toolbar/export button', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders export button', function(assert) {
  this.render(hbs`{{files-toolbar/export-button}}`);

  assert.equal(this.$('.export-button').text().trim(), 'Export to CSV', 'Make sure button is present');
});

test('Loader present when downloaded status not completed', function(assert) {
  new ReduxDataHelper(setState).downloadStatus('start').build();
  this.render(hbs`{{files-toolbar/export-button}}`);

  assert.equal(this.$('.export-button .rsa-loader').length, 1, 'Loader is present');
  assert.equal(this.$('.export-button').text().trim(), 'Downloading', 'Downloading text present');
});

test('Loader not present when downloaded status is completed', function(assert) {
  new ReduxDataHelper(setState).downloadStatus('completed').build();
  this.render(hbs`{{files-toolbar/export-button}}`);

  assert.equal(this.$('.export-button .rsa-loader').length, 0, 'Loader not present');
});

test('Iframe with download link present', function(assert) {
  new ReduxDataHelper(setState).downloadId('21ff3f2d33').build();
  this.render(hbs`{{files-toolbar/export-button}}`);

  assert.equal(this.$('iframe').length, 1, 'Iframe is present');
  assert.notEqual(this.$('iframe').attr('src').indexOf('rsa/endpoint/file/property/download?id=21ff3f2d33'), '-1', 'Download link present');
});

test('Download triggered on click of Export to CSV button', function(assert) {
  const { files: { fileList } } = files;

  new ReduxDataHelper(setState).fileList(fileList).build();
  this.render(hbs`{{files-toolbar/export-button}}`);

  assert.equal(this.$('.export-button').text().trim(), 'Export to CSV', 'Button text before click, Export to CSV');

  this.$('.export-button').trigger('click');

  return wait().then(() => {
    assert.equal(this.$('.export-button').text().trim(), 'Downloading', 'Button text after click, Downloading');
  });
});