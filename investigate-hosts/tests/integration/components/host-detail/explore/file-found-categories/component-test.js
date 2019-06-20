import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | host detail explore file found categories', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.redux = this.owner.lookup('service:redux');
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const file = {
    id: '5a0e581c59afc102e8d504a2',
    checksumSha256: 'f08d7d0e723e4404b8845d1982c0eb99064db526cbcc423fb5ef57d877a574b7',
    path: 'E\\Program Files\\schemas\\State',
    fileName: 'wininitU.sys',
    scanStartTime: 1510889499000,
    signature: 'Unsigned',
    categories: ['AUTORUNS']
  };


  test('file found categories should render', async function(assert) {

    let patchSuccess = false;
    assert.expect(4);
    this.set('file', file);
    this.set('scanTime', file.scanStartTime);
    await render(hbs`{{host-detail/explore/file-found-categories file=file scanTime=scanStartTime}}`);
    assert.equal(document.querySelectorAll('.file-found-categories').length, 1, 'should render file found categories');

    patchSocket((method, modelName) => {
      assert.equal(method, 'getFileContextList');
      assert.equal(modelName, 'endpoint');
      patchSuccess = true;
    });

    await click('.file-found-categories__list');
    await waitUntil(() => patchSuccess === true, { timeout: Infinity });
    const state = this.get('redux').getState();
    assert.equal(state.endpoint.explore.selectedTab.tabName, 'AUTORUNS', 'Selected tab validated');
  });
});
