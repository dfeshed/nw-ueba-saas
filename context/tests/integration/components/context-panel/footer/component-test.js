import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import listData from '../../../../data/list';
import * as ACTION_TYPES from 'context/actions/types';
import EmberObject from '@ember/object';
import dSDetails from 'context/config/im-alerts';

moduleForComponent('context-panel/footer', 'Integration | Component | context panel/footer', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders', function(assert) {
  const dataSourceData = EmberObject.create({
    class: 'alarm-sound',
    isConfigured: true,
    dataSourceType: 'LIST',
    displayType: 'table',
    details: dSDetails,
    field: 'LIST',
    tabRequired: true,
    title: 'context.header.lIST'
  });
  const contextData = EmberObject.create({});
  contextData.set('LIST', listData);
  this.set('contextData', contextData);

  this.get('redux').dispatch({
    type: ACTION_TYPES.INITIALIZE_CONTEXT_PANEL,
    payload: { lookupKey: '1.1.1.1', meta: 'IP' }
  });

  this.get('redux').dispatch({ type: ACTION_TYPES.GET_ALL_DATA_SOURCES, payload: [dataSourceData] });
  this.get('redux').dispatch({ type: ACTION_TYPES.GET_LOOKUP_DATA, payload: [listData] });
  this.get('redux').dispatch({
    type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
    payload: 'lIST'
  });
  this.render(hbs`{{context-panel/footer}}`);
  assert.equal(this.$('.rsa-context-panel__footer').text().trim(), '0 List(s)', 'Showing total count for list data.');
});
