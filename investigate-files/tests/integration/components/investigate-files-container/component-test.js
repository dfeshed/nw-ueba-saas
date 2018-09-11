import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import files from '../../state/files';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;
moduleForComponent('investigate-files-container', 'Integration | Component | investigate files container', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
    this.registry.injection('component', 'i18n', 'service:i18n');
    initialize(this);
  },
  afterEach() {
    revertPatch();
  }
});

test('Investigate files container, when files are available', function(assert) {
  const { files: { schema: { schema } } } = files;
  new ReduxDataHelper(setState).schema(schema).fileCount(3).setSelectedFileList([]).build();

  this.render(hbs`{{investigate-files-container}}`);

  assert.equal(this.$('.files-body .rsa-data-table').length, 1, 'file-list called.');
});

test('it renders error page when endpointserver is offline', function(assert) {
  new ReduxDataHelper(setState).isEndpointServerOffline(true).build();
  this.render(hbs`{{investigate-files-container}}`);
  assert.equal(this.$('.files-body').length, 0, 'file list is not rendered');
  assert.equal(this.$('.error-page').length, 1, 'endpoint server is offline');
});

test('it renders file list when endpointserver is online', function(assert) {
  const { files: { schema: { schema } } } = files;
  new ReduxDataHelper(setState).schema(schema).fileCount(3).setSelectedFileList([]).isEndpointServerOffline(false).build();
  this.render(hbs`{{investigate-files-container}}`);
  assert.equal(this.$('.error-page').length, 0, 'endpoint server is online');
  assert.equal(this.$('.files-body').length, 1, 'file list is rendered');
});