import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render, click } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { hostDownloads } from '../../../../components/state/downloads';
import { patchSocket } from '../../../../../helpers/patch-socket';
import Immutable from 'seamless-immutable';

let initState;

module('Integration | Component | directory-wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('directory-wrapper has loaded', async function(assert) {

    await render(hbs`{{host-detail/downloads/directory-wrapper}}`);
    assert.equal(findAll('.directory-wrapper').length, 1, 'directory-wrapper has loaded');
    assert.equal(findAll('.rsa-loader').length, 1, 'Loader present as there is no date');
  });

  test('directory-wrapper directory structure has rendered', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/directory-wrapper}}`);
    assert.equal(findAll('.directory-wrapper .mft-directory').length, 12, 'Directory rendered');

    assert.equal(findAll('.deleted-files').length, 1, 'Deleted files present');
    assert.equal(findAll('.all-files').length, 1, 'all files present');
  });

  test('Loader present on click', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/directory-wrapper}}`);

    await click(findAll('.mft-directory_arrow')[1]);
    assert.equal(findAll('.rsa-loader').length, 1, 'Loader present on click');
  });

  test('WS call on clicking the arrow', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/directory-wrapper}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'mftGetRecords');
      assert.equal(modelName, 'endpoint');
      assert.equal(query.data.criteria.criteriaList[0].expressionList.length, 3);
    });

    await click(findAll('.mft-directory_arrow')[1]);
  });

  test('WS call on clicking the subdirectory', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/directory-wrapper}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'mftGetRecords');
      assert.equal(modelName, 'endpoint');
      assert.equal(query.data.criteria.criteriaList[0].expressionList.length, 2);
    });

    await click(findAll('.mft-folder')[3]);
  });

  test('WS call on clicking All files', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/directory-wrapper}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'mftGetRecords');
      assert.equal(modelName, 'endpoint');
      assert.equal(query.data.criteria.criteriaList[0].expressionList.length, 1);
    });
    assert.equal(findAll('.all-files.selected').length, 0);
    await click(find('.all-files'));
    assert.equal(findAll('.all-files.selected').length, 1);
  });

  test('WS call on clicking Deleted files', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).build();
    await render(hbs`{{host-detail/downloads/directory-wrapper}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'mftGetRecords');
      assert.equal(modelName, 'endpoint');
      assert.equal(query.data.criteria.criteriaList[0].expressionList.length, 2);
    });
    assert.equal(findAll('.deleted-files.selected').length, 0);
    await click(find('.deleted-files'));
    assert.equal(findAll('.deleted-files.selected').length, 1);
  });
});