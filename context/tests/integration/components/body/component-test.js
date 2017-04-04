import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import alertData from '../../../data/alert-data';
import * as ACTION_TYPES from 'context/actions/types';

moduleForComponent('context-panel/body', 'Integration | Component | context-panel/body', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  this.set('alertsData', alertData);
  this.get('redux').dispatch({
    type: ACTION_TYPES.INITIALIZE_CONTEXT_PANEL,
    payload: { lookupKey: '1.1.1.1', meta: 'IP' }
  });
  this.get('redux').dispatch({ type: ACTION_TYPES.GET_ALL_DATA_SOURCES, payload: ['Alerts'] });
  this.render(hbs`{{context-panel/body contextData=alertsData}}`);
  assert.equal(this.$('.rsa-data-table-header-cell').length, 6, 'Testing count of data header cells');
});
