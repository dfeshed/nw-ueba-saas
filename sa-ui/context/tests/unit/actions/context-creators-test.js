import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ACTION_TYPES from 'context/actions/types';
import lookupData from '../../data/subscriptions/context/stream/data/ip';
import entitiesMetas from '../../data/subscriptions/entity-meta/findAll/data';
import {
  updatePanelClicked,
  restoreDefault,
  updateActiveTab,
  initializeContextPanel,
  getContextEntitiesMetas } from 'context/actions/context-creators';

const state = Immutable.from({
  meta: 'ip',
  lookupKey: '10.10.10.10',
  errorMessage: null,
  lookupData,
  entitiesMetas,
  isClicked: true
});

let currentActionType = [];

const dispatch = (obj) => {
  currentActionType.push(obj);
};

module('Unit | Utility | Context-Creators', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    currentActionType = [];
    initialize(this.owner);
  });

  test('Test action creator for updating panel click', async function(assert) {
    const action = updatePanelClicked(state);
    assert.equal(action.type, ACTION_TYPES.UPDATE_PANEL_CLICKED);
  });

  test('Test action creator for restore default', async function(assert) {
    const action = restoreDefault();
    assert.equal(action.type, ACTION_TYPES.RESTORE_DEFAULT);
  });

  test('Test action creator for active tab', async function(assert) {
    const action = updateActiveTab('Archer');
    assert.equal(action.type, ACTION_TYPES.UPDATE_ACTIVE_TAB);
    assert.equal(action.payload, 'Archer');
  });

  test('Test action creator for initializeContextPanel', async function(assert) {
    initializeContextPanel({ entityId: '1.1.1.1', entityType: 'IP' })(dispatch);
    assert.equal(currentActionType[0].type, ACTION_TYPES.INITIALIZE_CONTEXT_PANEL);
  });

  test('Test action creator for getContextEntitiesMetas', async function(assert) {
    getContextEntitiesMetas({ data: entitiesMetas })(dispatch);
    assert.equal(currentActionType[0].type, ACTION_TYPES.GET_CONTEXT_ENTITIES_METAS);
    assert.deepEqual(currentActionType[0].payload, entitiesMetas);
  });

});
