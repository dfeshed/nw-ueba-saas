import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import sinon from 'sinon';
import FilterCreators from 'investigate-process-analysis/actions/creators/process-filter';
let setState;

const resetFilterValueSpy = sinon.spy(FilterCreators, 'resetFilterValue');

const spys = [
  resetFilterValueSpy
];


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

  hooks.afterEach(function() {
    spys.forEach((s) => s.resetHistory());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
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


  test('it renders the header and footer sections', async function(assert) {
    new ReduxDataHelper(setState).selectedProcess().processFilter(processFilter).build();
    await render(hbs`{{process-details/events-filter-panel}}`);
    assert.equal(findAll('.rsa-header').length, 1, 'Header section exists');
    assert.equal(findAll('.filter-footer').length, 1, 'Filter footer is exists');
  });

  test('Clicking on the close icon will call the external action', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState).selectedProcess().processFilter(processFilter).build();
    this.set('toggleFilterPanel', () => {
      assert.ok(true);
    });
    await render(hbs`{{process-details/events-filter-panel toggleFilterPanel=(action toggleFilterPanel)}}`);
    await click('.close-zone button');
  });

  test('Clicking on the reset will call the reset action', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState).selectedProcess().processFilter(processFilter).build();
    this.set('toggleFilterPanel', () => {
      assert.ok(true);
    });
    await render(hbs`{{process-details/events-filter-panel toggleFilterPanel=(action toggleFilterPanel)}}`);
    await click('.filter-footer button');
    assert.equal(resetFilterValueSpy.callCount, 1, 'Reset is called once');
  });
});
