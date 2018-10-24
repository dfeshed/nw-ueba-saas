import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | file pager', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      applyPatch(state);
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('Footer without filter applied file list is > 1000', async function(assert) {
    new ReduxDataHelper(setState)
      .totalItems(2000)
      .fileCount(11)
      .isValidExpression(false)
      .setSelectedFileList([])
      .build();
    await render(hbs`{{file-pager}}`);
    return settled().then(() => {
      assert.equal(find('.file-info').textContent.trim(), 'Showing 11 of 2000+  | 0 selected', 'total number of files displayed');
    });
  });

  test('Footer with filter applied and file list is 1000', async function(assert) {
    new ReduxDataHelper(setState)
      .totalItems(1000)
      .fileCount(12)
      .isValidExpression(true)
      .setSelectedFileList([])
      .build();
    await render(hbs`{{file-pager}}`);
    return settled().then(() => {
      assert.equal(find('.file-info').textContent.trim(), 'Showing 12 of 1000+  | 0 selected', 'total number of files with + displayed');
    });
  });

  test('Footer with filter applied and file list is < 1000', async function(assert) {
    new ReduxDataHelper(setState)
      .totalItems(500)
      .fileCount(11)
      .isValidExpression(true)
      .setSelectedFileList([])
      .build();
    await render(hbs`{{file-pager}}`);
    return settled().then(() => {
      assert.equal(find('.file-info').textContent.trim(), 'Showing 11 of 500  | 0 selected', 'total number of files displayed');
    });
  });
});
