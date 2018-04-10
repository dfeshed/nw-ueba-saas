import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import alertData from '../../../data/alert-data';
import * as ACTION_TYPES from 'context/actions/types';
import EmberObject from '@ember/object';
import dSDetails from 'context/config/im-alerts';
import wait from 'ember-test-helpers/wait';
import { waitForSockets } from '../../../helpers/wait-for-sockets';

moduleForComponent('context-panel/body', 'Integration | Component | context-panel/body', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  const done = waitForSockets();

  const dataSourceData = EmberObject.create({
    class: 'alarm-sound',
    isConfigured: true,
    dataSourceType: 'Alerts',
    displayType: 'table',
    details: dSDetails,
    field: 'Alerts',
    tabRequired: true,
    title: 'context.header.alerts'
  });
  const contextData = EmberObject.create({});
  contextData.set('Alerts', alertData);
  this.set('contextData', contextData);
  this.set('i18n', { t() {
    return 'Return Value';
  }
  });

  this.get('redux').dispatch({
    type: ACTION_TYPES.INITIALIZE_CONTEXT_PANEL,
    payload: { lookupKey: '1.1.1.1', meta: 'IP' }
  });
  this.get('redux').dispatch({ type: ACTION_TYPES.GET_ALL_DATA_SOURCES, payload: [dataSourceData] });
  this.get('redux').dispatch({ type: ACTION_TYPES.GET_LOOKUP_DATA, payload: [alertData] });
  this.get('redux').dispatch({
    type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
    payload: 'Alerts'
  });
  this.render(hbs`{{context-panel/body contextData=contextData i18n=i18n}}`);
  assert.equal(this.$('.rsa-data-table-header-cell').length, 6, 'Testing count of data header cells');

  return wait().then(() => done());
});
