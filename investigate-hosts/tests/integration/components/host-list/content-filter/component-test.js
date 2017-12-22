import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import endpoint from '../../state/schema';

let initState;
initState = Immutable.from({
  endpoint: {
    filter: {
      expressionList: [],
      lastFilterAdded: null,
      schemas: endpoint.schema,
      appliedFilters: null
    }
  }
});

moduleForComponent('host-list/content-filter', 'Integration | Component | endpoint host-list/content-filter', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    initState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('host-list content-filter renders default filters', function(assert) {

  // set height to get all lazy rendered items on the page
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    const textFilters = this.$('.content-filter').find('.text-filter');
    assert.equal(textFilters.length, 0, 'As there are no default filters');
  });
});

test('host-list content-filter renders filters', function(assert) {
  const testExpressionList = {
    expressionList: [
      {
        propertyName: 'id',
        propertyValues: null
      },
      {
        propertyName: 'machine.agentVersion',
        propertyValues: null
      }
    ]
  };
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
    // set height to get all lazy rendered items on the page
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    const textFilters = this.$('.content-filter').find('.text-filter');
    assert.equal(textFilters.length, 2, 'Two text filters rendered');
  });
});


test('host-list content-filter renders date time filters', function(assert) {
  const testExpressionList = {
    expressionList: [
      {
        propertyName: 'machine.scanStartTime',
        propertyValues: null
      }
    ]
  };
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
    // set height to get all lazy rendered items on the page
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    const textFilters = this.$('.content-filter').find('.datetime-filter');
    assert.equal(textFilters.length, 1, 'Datetime filter rendered');
  });
});