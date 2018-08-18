import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import wait from 'ember-test-helpers/wait';
import * as serverActions from 'investigate-files/actions/endpoint-server-creators';
import sinon from 'sinon';

let setState;
const item = [
  {
    checksumSha256: '365a393f3a34bf13f49306868b',
    id: '365'
  }
];
moduleForComponent('files-toolbar', 'Integration | Component | Files toolbar', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    initialize(this);
    this.inject.service('redux');
    setState = (state) => {
      applyPatch(state);
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('Investigate files toolbar', function(assert) {
  const done = assert.async();
  const actionStub = sinon.stub(serverActions, 'getEndpointServers');
  actionStub.returns(() => () => {});
  new ReduxDataHelper(setState).totalItems(3).setSelectedFileList(item).build();
  this.render(hbs`{{files-toolbar}}`);
  assert.equal(this.$('.title-header').length, 1, 'Files toolbar present');
  assert.equal(this.$('.export-button').length, 1, 'Export button present');
  return wait().then(() => {
    actionStub.restore();
    done();
  });
});
