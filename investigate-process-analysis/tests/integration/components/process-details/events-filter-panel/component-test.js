import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | events-filter-panel', function(hooks) {
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


  test('events-filter-panel/container renders', async function(assert) {
    new ReduxDataHelper(setState).processFilter(processFilter).build();

    await render(hbs`{{process-details/events-filter-panel}}`);
    assert.equal(findAll('.label').length, 1, 'Title present');
    assert.equal(find('.label').textContent.trim(), 'Filters', 'Title value "Filters" present');
    assert.equal(findAll('.resetFilter').length, 1, 'Reset link');
    assert.equal(find('.resetFilter').textContent.trim(), 'Reset Filter', 'Title for reset link "Reset Filter" present');
    assert.equal(findAll('.meta-filter').length, 2, '2 meta filter wrapper call present');
  });

});
