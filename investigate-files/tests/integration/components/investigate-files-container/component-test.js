import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import files from '../../state/files';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

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
  },
  afterEach() {
    revertPatch();
  }
});

test('Investigate files container elements', function(assert) {
  this.render(hbs`{{investigate-files-container}}`);

  assert.equal(this.$('.rsa-investigate-files.main-zone').length, 1, 'Investigate files container present');
  assert.equal(this.$('.files-content-filter').length, 1, 'Add filter present');

  assert.equal(this.$('.filter-zone').length, 1, 'filter zone present');
  assert.equal(this.$('.filter-zone > h2').text(), 'Saved Filters', 'Filter Zone title text');
  assert.equal(this.$('.filter-zone .favourite-filters .filter-list').length, 2, 'Default filters and custom filters component called');

  assert.equal(this.$('.files-zone').length, 1, 'files zone present');
  assert.equal(this.$('.files-zone .title-header').length, 1, 'files toolbar called');
  assert.equal(this.$('.files-zone .files-body ').length, 1, 'files body present');
  assert.equal(this.$('.files-body .rsa-loader').length, 1, 'Loader persent before data is rendered.');
  assert.equal(this.$('.files-footer .file-pager').length, 1, 'file-pager called.');
});

test('Investigate files container, when files are available', function(assert) {
  const { files: { schema: { schema } } } = files;
  new ReduxDataHelper(setState).schema(schema).fileCount(3).build();

  this.render(hbs`{{investigate-files-container}}`);

  assert.equal(this.$('.files-body .rsa-data-table').length, 1, 'file-list called.');
});
