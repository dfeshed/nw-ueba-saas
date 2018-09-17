import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import files from '../../state/files';
import { waitForSockets } from '../../../helpers/wait-for-sockets';
let initState;

module('Integration | Component | file found on machines', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });


  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('Investigate files container, when files are available', async function(assert) {
    const { files: { schema: { schema } } } = files;
    new ReduxDataHelper(initState)
      .schema(schema)
      .fileCount(3)
      .setSelectedFileList([])
      .build();

    await render(hbs`{{investigate-files-container}}`);

    assert.equal(findAll('.files-body .rsa-data-table').length, 1, 'file-list called.');
  });

  test('it renders error page when endpointserver is offline', async function(assert) {
    new ReduxDataHelper(initState)
      .isEndpointServerOffline(true)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(findAll('.files-body').length, 0, 'file list is not rendered');
    assert.equal(findAll('.error-page').length, 1, 'endpoint server is offline');
  });

  test('it renders file list when endpointserver is online', async function(assert) {
    const { files: { schema: { schema } } } = files;
    new ReduxDataHelper(initState)
      .schema(schema)
      .fileCount(3)
      .setSelectedFileList([])
      .isEndpointServerOffline(false)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(findAll('.error-page').length, 0, 'endpoint server is online');
    assert.equal(findAll('.files-body').length, 1, 'file list is rendered');
  });

  test('when hosts tab is active, file-found-on-hosts renders', async function(assert) {
    const { files: { schema: { schema } } } = files;
    const done = waitForSockets();
    new ReduxDataHelper(initState)
      .schema(schema)
      .fileCount(3)
      .setSelectedFileList([])
      .isEndpointServerOffline(false)
      .hostNameList([{ value: 'Machine1' }])
      .activeDataSourceTab('HOSTS')
      .build();
    await render(hbs`{{investigate-files-container}}`);
    await click(findAll('.files-body .rsa-data-table-body-row')[0]);
    return settled().then(() => {
      assert.equal(findAll('.files-host-list').length, 1, 'Machine list is rendered');
      done();
    });

  });

});
