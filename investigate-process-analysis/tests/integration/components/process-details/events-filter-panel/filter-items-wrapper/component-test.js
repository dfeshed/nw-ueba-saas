import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | filter-items-wrapper', function(hooks) {
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
  const filters = {
    action: [],
    category: []
  };
  const filterConfig = [
    {
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
    },
    {
      name: 'action',
      options: [
        {
          name: 'writeToExecutable'
        },
        {
          name: 'renameToExecutable'
        },
        {
          name: 'readDocument'
        }
      ]
    }
  ];
  const processFilter = Immutable.from({
    schema: [...filterConfig],
    filter: { ...filters }
  });


  test('events-filter-panel/filter-items-wrapper container renders', async function(assert) {
    new ReduxDataHelper(setState).processFilter(processFilter).build();

    await render(hbs`{{process-details/events-filter-panel/filter-items-wrapper filterName='action' option='readDocument'}}`);
    assert.equal(findAll('.filterItem').length, 1, 'filter-items-wrapper present');
    assert.equal(find('.filterItem span').textContent.trim(), 'readDocument', 'Option value rendered');
  });

  test('Selection of filter option on Click', async function(assert) {
    new ReduxDataHelper(setState).processFilter(processFilter).queryInput(queryInputs).build();

    await render(hbs`{{process-details/events-filter-panel/filter-items-wrapper filterName='action' option='readDocument'}}`);
    assert.equal(findAll('.is-selected').length, 0, 'is-selected class not present before click');

    await click('.filterItem span');
    assert.equal(findAll('.is-selected').length, 1, 'is-selected added');

    await click('.filterItem span');
    assert.equal(findAll('.is-selected').length, 0, 'is-selected removed');
  });
});
