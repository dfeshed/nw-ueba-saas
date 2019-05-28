import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { hostDownloads } from '../../../../components/state/downloads';
import Immutable from 'seamless-immutable';

let initState;

module('Integration | Component | file-pagination', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('file-pagination has loaded', async function(assert) {

    await render(hbs`{{host-detail/downloads/file-pagination}}`);
    assert.equal(findAll('.file-pager').length, 1, 'file-pagination loaded');
  });

  test('Pagination', async function(assert) {
    new ReduxDataHelper(initState).hostDownloads(hostDownloads).downloadsHasNext(true).downloadsFilterExpressionList([{ a: 1 }]).build();
    await render(hbs`{{host-detail/downloads/file-pagination}}`);
    assert.equal(find('.file-pager').textContent.trim(), 'Showing 6 out of 6 files | 0 selected', 'Pagintion text rendered');
  });
});
