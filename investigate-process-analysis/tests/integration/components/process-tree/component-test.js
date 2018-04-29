import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { selectAll } from 'd3-selection';
import engineResolverFor from '../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | process-tree', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
  });

  test('it renders the process tree', async function(assert) {
    const queryInput = {
      sid: '1',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    this.set('queryInput', queryInput);
    new ReduxDataHelper(setState).queryInput(queryInput).build();
    await render(hbs`{{process-tree queryInput=queryInput}}`);
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    assert.equal(findAll('.process').length, 24, 'Expected to render 5 nodes');
  });

  test('it should expand the node on click', async function(assert) {
    const queryInputs = {
      sid: '1',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    this.set('queryInput', queryInputs);
    new ReduxDataHelper(setState).queryInput(queryInputs).build();
    await render(hbs`{{process-tree queryInput=queryInput}}`);
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    await selectAll('.process:nth-of-type(2)').dispatch('click');
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    assert.equal(findAll('.process').length, 47, 'Expected to render 7 nodes');
  });
});
