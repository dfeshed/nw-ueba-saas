import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click, fillIn } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchSocket } from '../../../../helpers/patch-socket';

module('Integration | Component | Utils | manual-file-download', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('manual-file-download has rendered', async function(assert) {

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId}}`);

    assert.equal(findAll('#modalDestination .manual-file-download').length, 1, 'Manual file download modal loaded');
  });

  test('manual-file-download content has rendered', async function(assert) {

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId}}`);

    assert.ok(find('.manual-file-download h3').textContent.trim().length, 'Manual file downloads title rendered');
    assert.equal(findAll('#modalDestination .modal-footer-buttons button').length, 2, 'Cancel and download buttons present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-path').length, 1, 'text box for file path present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 0, 'text box for file count not present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 0, 'text box for file size not present');
  });

  test('manual-file-download count and size content has rendered', async function(assert) {

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');
    this.set('filePath', '/test/file*/test.txt');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId
      filePath=filePath}}`);

    await fillIn('#modalDestination .manual-file-download_file-path input', '/test/*');

    assert.ok(find('.manual-file-download h3').textContent.trim().length, 'Manual file downloads title rendered');
    assert.equal(findAll('#modalDestination .modal-footer-buttons button').length, 2, 'Cancel and download buttons present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-path').length, 1, 'text box for file path present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 1, 'text box for file count present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 1, 'text box for file size present');
  });

  test('manual-file-download download button disabled when no path is present', async function(assert) {

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId}}`);

    assert.equal(findAll('#modalDestination .modal-footer-buttons .is-disabled.is-primary button').length, 1, 'Download button disabled.');
    await fillIn('#modalDestination .manual-file-download_file-path input', '/test/test');
    assert.equal(findAll('#modalDestination .modal-footer-buttons .is-disabled .is-primary button').length, 0, 'Download button enabled.');
  });

  test('manual-file-download socket call for wildcard', async function(assert) {

    assert.expect(6);

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');
    this.set('closeConfirmModal', function() {});

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'downloadWildcardMatchedFilesToServer');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentIds: ['agentID'],
          countFiles: 10,
          maxFileSize: 100,
          path: '/test/*'
        }
      });
    });

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId
      closeConfirmModal=closeConfirmModal}}`);

    assert.equal(findAll('#modalDestination .manual-file-download_file-path').length, 1, 'text box for file path present');

    await fillIn('#modalDestination .manual-file-download_file-path input', '/test/*');

    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 1, 'text box for file count present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 1, 'text box for file size present');
    await click(find('.modal-footer-buttons .is-primary button'));
  });

  test('manual-file-download socket call for full path', async function(assert) {

    assert.expect(6);

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');
    this.set('closeConfirmModal', function() {});

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'downloadFileToServer');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentId: 'agentID',
          files: [
            {
              fileName: '/test/test',
              hash: undefined,
              path: '/test/test'
            }
          ]
        }
      });
    });

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId
      closeConfirmModal=closeConfirmModal}}`);

    assert.equal(findAll('#modalDestination .manual-file-download_file-path').length, 1, 'text box for file path present');

    await fillIn('#modalDestination .manual-file-download_file-path input', '/test/test');

    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 0, 'text box for file count present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 0, 'text box for file size present');
    await click(find('.modal-footer-buttons .is-primary button'));
  });
});