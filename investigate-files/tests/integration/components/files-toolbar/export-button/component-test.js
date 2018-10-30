import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, find, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import files from '../../../state/files';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | files toolbar/export button', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      applyPatch(state);
      initialize(this.owner);
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('it renders export button', async function(assert) {
    await render(hbs`{{files-toolbar/export-button}}`);

    assert.equal(findAll('.export-button .rsa-icon-upload-2-filled').length, 1, 'Make sure button is present');
  });

  test('Loader present when downloaded status not completed', async function(assert) {
    const { files: { fileList } } = files;
    new ReduxDataHelper(setState).files(fileList.files).downloadStatus('start').build();
    await render(hbs`{{files-toolbar/export-button}}`);

    assert.equal(findAll('.export-button .rsa-loader').length, 1, 'Loader is present');
  });

  test('Loader not present when downloaded status is completed', async function(assert) {
    new ReduxDataHelper(setState).downloadStatus('completed').build();
    await render(hbs`{{files-toolbar/export-button}}`);

    assert.equal(findAll('.export-button .rsa-loader').length, 0, 'Loader not present');
  });

  test('Iframe with download link present', async function(assert) {
    new ReduxDataHelper(setState).downloadId('21ff3f2d33').build();
    await render(hbs`{{files-toolbar/export-button}}`);

    assert.equal(findAll('iframe').length, 1, 'Iframe is present');
    assert.notEqual(find('iframe').getAttribute('src').indexOf('rsa/endpoint/file/property/download?id=21ff3f2d33'), '-1', 'Download link present');
  });

  test('Download triggered on click of Export to CSV button', async function(assert) {
    const { files: { fileList } } = files;

    new ReduxDataHelper(setState).files(fileList.files).downloadStatus('completed').build();
    await render(hbs`{{files-toolbar/export-button}}`);

    assert.equal(findAll('.export-button .rsa-icon-upload-2-filled').length, 1, 'Button before click, Export to CSV');

    click('.export-button');

    return settled().then(() => {
      assert.equal(findAll('.export-button .rsa-loader').length, 1, 'Downloading', 'Button after click, Downloading');
    });
  });
});