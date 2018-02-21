import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import { patchSocket } from '../../../../../helpers/patch-socket';

moduleForComponent('host-detail/explore/file-found-categories', 'Integration | Component | host detail explore file found categories', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
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


test('file found categories should render', function(assert) {

  assert.expect(4);
  this.set('file', file);
  this.set('scanTime', file.scanStartTime);
  this.render(hbs`{{host-detail/explore/file-found-categories file=file scanTime=scanStartTime}}`);
  assert.equal(this.$('.file-found-categories').is(':visible'), true, 'should render file found categories');

  patchSocket((method, modelName) => {
    assert.equal(method, 'getFileContextList');
    assert.equal(modelName, 'endpoint');
  });

  this.$('.file-found-categories__list').click();
  const state = this.get('redux').getState();
  assert.equal(state.endpoint.explore.selectedTab.tabName, 'AUTORUNS', 'Selected tab validated');
});