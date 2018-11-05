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

  test('Footer with filter applied and file list is 1200', async function(assert) {
    new ReduxDataHelper(setState)
      .totalItems(1200)
      .fileCount(11)
      .isValidExpression(true)
      .setSelectedFileList([])
      .build();
    await render(hbs`{{file-pager}}`);
    return settled().then(() => {
      assert.equal(find('.file-info').textContent.trim(), 'Showing 11 of 1200  | 0 selected', 'total number of files displayed');
    });
  });
});
