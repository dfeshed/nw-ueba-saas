import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click, fillIn } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchSocket } from '../../../../helpers/patch-socket';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | Utils | manual-file-download', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('manual-file-download has rendered', async function(assert) {

    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId}}`);

    assert.equal(findAll('#modalDestination .manual-file-download').length, 1, 'Manual file download modal loaded');
  });

  test('manual-file-download content has rendered', async function(assert) {

    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

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

    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

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

    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

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

    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

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
          path: 'c:\\test\\*'
        }
      });
    });

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId
      closeConfirmModal=closeConfirmModal}}`);

    assert.equal(findAll('#modalDestination .manual-file-download_file-path').length, 1, 'text box for file path present');

    await fillIn('#modalDestination .manual-file-download_file-path input', 'c:\\test\\*');

    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 1, 'text box for file count present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 1, 'text box for file size present');
    await click(find('.modal-footer-buttons .is-primary button'));
  });

  test('manual-file-download socket call for full path, windows', async function(assert) {

    assert.expect(6);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'downloadFileToServer');
      assert.equal(modelName, 'agent');
      assert.deepEqual(query, {
        data: {
          agentId: 'agentID',
          files: [
            {
              fileName: 'test.txt',
              hash: undefined,
              path: 'c:\\test'
            }
          ]
        }
      });
    });

    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');
    this.set('closeConfirmModal', function() {});

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId
      closeConfirmModal=closeConfirmModal}}`);

    assert.equal(findAll('#modalDestination .manual-file-download_file-path').length, 1, 'text box for file path present');

    await fillIn('#modalDestination .manual-file-download_file-path input', 'c:\\test\\test.txt');

    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 0, 'text box for file count present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 0, 'text box for file size present');
    await click(find('.modal-footer-buttons .is-primary button'));
  });

  test('manual-file-download socket call for full path, linux', async function(assert) {

    assert.expect(6);

    new ReduxDataHelper(setState)
      .machineOSType('linux')
      .build();

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
              fileName: 'test.txt',
              hash: undefined,
              path: '/testFolder'
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

    await fillIn('#modalDestination .manual-file-download_file-path input', '/testFolder/test.txt');

    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 0, 'text box for file count not present');
    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 0, 'text box for file size not present');
    await click(find('.modal-footer-buttons .is-primary button'));
  });

  test('manual-file-download validate filePath, windows', async function(assert) {

    assert.expect(7);

    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');
    this.set('closeConfirmModal', function() {});

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId
      closeConfirmModal=closeConfirmModal}}`);

    assert.equal(findAll('#modalDestination .manual-file-download_file-path').length, 1, 'text box for file path present');
    assert.equal(findAll('#modalDestination .file-path.is-error').length, 0, 'text box error not present');

    await fillIn('#modalDestination .manual-file-download_file-path input', '\\test\\*');

    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 1, 'text box for file count present');
    assert.equal(findAll('#modalDestination .file-path.is-error').length, 0, 'text box error not present');

    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 1, 'text box for file size present');
    assert.equal(findAll('#modalDestination .file-path.is-error').length, 0, 'text box error not present');

    await fillIn('#modalDestination .manual-file-download_file-size input', 0);
    await fillIn('#modalDestination .manual-file-download_file-count input', 'test');

    await click(find('.modal-footer-buttons .is-primary button'));
    assert.equal(findAll('#modalDestination .file-path.is-error').length, 3, 'text box error present for all 3 fields');
  });

  test('manual-file-download validate filePath, Linux', async function(assert) {

    assert.expect(7);

    new ReduxDataHelper(setState)
      .machineOSType('linux')
      .build();

    this.set('agentId', 'agentID');
    this.set('serverId', 'serverId ');
    this.set('closeConfirmModal', function() {});

    await render(hbs `<div id='modalDestination'></div>
      {{utils/manual-file-download
      agentId=agentId
      serverId=serverId
      closeConfirmModal=closeConfirmModal}}`);

    assert.equal(findAll('#modalDestination .manual-file-download_file-path').length, 1, 'text box for file path present');
    assert.equal(findAll('#modalDestination .file-path.is-error').length, 0, 'text box error not present');

    await fillIn('#modalDestination .manual-file-download_file-path input', '\\test\\*');

    assert.equal(findAll('#modalDestination .manual-file-download_file-count').length, 1, 'text box for file count present');
    assert.equal(findAll('#modalDestination .file-path.is-error').length, 0, 'text box error not present');

    assert.equal(findAll('#modalDestination .manual-file-download_file-size').length, 1, 'text box for file size present');
    assert.equal(findAll('#modalDestination .file-path.is-error').length, 0, 'text box error not present');

    await fillIn('#modalDestination .manual-file-download_file-size input', 0);
    await fillIn('#modalDestination .manual-file-download_file-count input', 'test');

    await click(find('.modal-footer-buttons .is-primary button'));
    assert.equal(findAll('#modalDestination .file-path.is-error').length, 3, 'text box error present for all 3 fields');
  });

});