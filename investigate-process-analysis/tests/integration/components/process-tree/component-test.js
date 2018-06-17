import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, waitUntil, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { selectAll } from 'd3-selection';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
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
      vid: '2',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    this.set('queryInput', queryInput);
    new ReduxDataHelper(setState).path(['0', '2', '3']).queryInput(queryInput).build();
    await render(hbs`{{process-tree queryInput=queryInput}}`);
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    assert.equal(findAll('circle.process').length, 3, 'Expected to render 4 nodes');
  });

  test('it should display child count for process', async function(assert) {
    const queryInput = {
      sid: '1',
      vid: '4',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    this.set('queryInput', queryInput);
    new ReduxDataHelper(setState).path(['0', '2', '3']).queryInput(queryInput).build();
    await render(hbs`{{process-tree queryInput=queryInput}}`);
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    assert.equal(find('.child-count').textContent, 1, 'Expected to render child count');
  });

  test('it should expand the node on click', async function(assert) {
    const queryInputs = {
      sid: '1',
      vid: '4',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    this.set('queryInput', queryInputs);
    new ReduxDataHelper(setState).path(['0', '2', '3']).queryInput(queryInputs).build();
    await render(hbs`{{process-tree queryInput=queryInput}}`);
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    await selectAll('g.process:nth-of-type(2) .button-wrapper').dispatch('click');
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    assert.equal(findAll('circle.process').length, 4, 'Expected to render 4 nodes');
  });
  test('it should show tooltip on mouseover', async function(assert) {
    const queryInputs = {
      sid: '1',
      vid: '4',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    this.set('queryInput', queryInputs);
    new ReduxDataHelper(setState).path(['0', '2', '3']).queryInput(queryInputs).build();
    await render(hbs`{{process-tree queryInput=queryInput}}`);
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    document.getElementById('endpoint-process-2').dispatchEvent(new MouseEvent('mouseover'));
    return settled().then(() => {
      assert.equal(findAll('.panel-content').length, 1, 'Expected to render tether panel');
    });

  });

  test('it should hide tooltip on mouseleave', async function(assert) {
    const queryInputs = {
      sid: '1',
      vid: '4',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    this.set('queryInput', queryInputs);
    new ReduxDataHelper(setState).path(['0', '2', '3']).queryInput(queryInputs).build();
    await render(hbs`{{process-tree queryInput=queryInput}}`);
    await waitUntil(() => !find('.rsa-fast-force__wait'), { timeout: Infinity });
    document.getElementById('endpoint-process-2').dispatchEvent(new MouseEvent('mouseover'));
    return settled().then(() => {
      assert.equal(findAll('.panel-content').length, 1, 'Expected to render tether panel');
      document.getElementById('endpoint-process-2').dispatchEvent(new MouseEvent('mouseleave'));
      return settled().then(() => {
        assert.equal(findAll('.panel-content').length, 0, 'Expected to hide tether panel');
      });
    });

  });
});
