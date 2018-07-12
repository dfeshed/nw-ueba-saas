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
