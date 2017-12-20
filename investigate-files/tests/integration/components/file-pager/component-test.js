import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

moduleForComponent('file-pager', 'Integration | Component | file pager', {
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

test('Footer without filter applied file list is > 1000', function(assert) {
  new ReduxDataHelper(setState)
    .totalItems(2000)
    .fileCount(11)
    .isValidExpression(false)
    .build();
  this.render(hbs`{{file-pager}}`);
  return wait().then(() => {
    assert.equal(this.$('.file-info').text().trim(), '11 of 2000', 'total number of files displayed');
  });
});

test('Footer with filter applied and file list is 1000', function(assert) {
  new ReduxDataHelper(setState)
    .totalItems(1000)
    .fileCount(12)
    .isValidExpression(true)
    .build();
  this.render(hbs`{{file-pager}}`);
  return wait().then(() => {
    assert.equal(this.$('.file-info').text().trim(), '12 of 1000+', 'total number of files with + displayed');
  });
});

test('Footer with filter applied and file list is < 1000', function(assert) {
  new ReduxDataHelper(setState)
    .totalItems(500)
    .fileCount(11)
    .isValidExpression(true)
    .build();
  this.render(hbs`{{file-pager}}`);
  return wait().then(() => {
    assert.equal(this.$('.file-info').text().trim(), '11 of 500', 'total number of files displayed');
  });
});
