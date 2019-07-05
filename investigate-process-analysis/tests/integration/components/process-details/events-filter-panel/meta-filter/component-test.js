import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | meta-filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  const hostDetails = [{
    machineOsType: 'windows'
  }];

  test('events-filter-panel/meta-filter container renders', async function(assert) {
    const filter = {
      name: 'category',
      options: [
        {
          name: 'Process Event'
        },
        {
          name: 'File Event'
        },
        {
          name: 'Registry Event'
        },
        {
          name: 'Network Event'
        }
      ]
    };

    this.set('filter', filter);
    new ReduxDataHelper(setState)
      .processProperties(hostDetails)
      .build();
    await render(hbs`{{process-details/events-filter-panel/meta-filter filter=filter}}`);

    assert.equal(findAll('.filterName').length, 1, 'filter name container present');
    assert.equal(find('.filterName').textContent.trim(), 'category', 'Title value "Category" present');
    assert.equal(findAll('.filterItem').length, 4, 'filter-items-wrapper present and 4 options are present');

  });
});
