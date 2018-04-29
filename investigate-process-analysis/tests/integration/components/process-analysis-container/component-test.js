import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | process-analysis-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('process-analysis/container renders', async function(assert) {
    const queryInput = {
      sid: '1',
      pn: 'test',
      st: 1231233,
      et: 13123,
      osType: 'windows',
      checksum: '07d15ddf2eb7be486d01bcabab7ad8df35b7942f25f5261e3c92cd7a8931190a',
      aid: '51687D32-BB0F-A424-1D64-A8B94C957BD2'
    };
    new ReduxDataHelper(setState).queryInput(queryInput).error(null).build();
    await render(hbs`{{process-analysis-container}}`);
    await waitUntil(() => !find('.process-list-box'), { timeout: Infinity });
    assert.equal(findAll('.process-list-box').length, 3, '3 columns present');
  });
});
