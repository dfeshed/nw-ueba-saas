import { module, test } from 'qunit';
import { bindActionCreators } from 'redux';
import { settled } from '@ember/test-helpers';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import ReduxDataHelper from '../../helpers/redux-data-helper';
import { throwSocket } from '../../helpers/patch-socket';
import { patchReducer } from '../../helpers/vnext-patch';
import { invalidServerResponse } from './data';
import guidedCreators from 'investigate-events/actions/guided-creators';
import ACTION_TYPES from 'investigate-events/actions/types';

module('Unit | Actions | Guided Creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('addGuidedPill action creator returns proper type and payload', function(assert) {
    assert.expect(5);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.ADD_PILL, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, { meta: 'ip.proto', operator: '=', value: 'boom' }, 'action pillData has the right value');
        assert.equal(action.payload.position, 0, 'action position has the right value');
      }
    };

    const validateDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
      action.promise.catch((error) => {
        assert.equal(error.meta, 'You must enter an 8-bit Integer.', 'Expected validaiton error');
        done();
      });
    };

    // this thunk will shoot out 2 actions - one to add and one to validate
    const thunk = guidedCreators.addGuidedPill({
      pillData: {
        meta: 'ip.proto',
        operator: '=',
        value: 'boom'
      },
      position: 0
    });
    thunk(myDispatch);
  });

  test('batchAddPills action creator returns proper type and payload', function(assert) {
    assert.expect(9);
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateClientSideDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.BATCH_ADD_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillsData, [
          { meta: 'ip.proto', operator: '=', value: '\'boom\'' },
          { meta: 'starttime', operator: '=', value: '\'boom\'' }
        ], 'action pillsData has the right value');
        assert.equal(action.payload.initialPosition, 0, 'action initialPosition has the right value');
      }
    };

    // Called twice. This is the dispatch passed to _clientSideValidation
    const validateClientSideDispatch = (action) => {
      assert.equal(typeof action, 'function', '_clientSideValidation returns another function');
      action(validateServerSideDispatch, getState);
    };

    // Called twice. This is the dispatch passed to _serverSideValidation
    const validateServerSideDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
      assert.ok(action.meta.isServerSide, 'server side validation happens');
    };

    // this thunk will shoot out 3 actions - one to add and two to validate
    const thunk = guidedCreators.batchAddPills({
      pillsData: [
        {
          meta: 'ip.proto',
          operator: '=',
          value: '\'boom\''
        },
        {
          meta: 'starttime',
          operator: '=',
          value: '\'boom\''
        }
      ],
      initialPosition: 0
    });
    thunk(myDispatch);
  });

  test('editGuidedPill action creator returns proper type and payload', function(assert) {
    assert.expect(4);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.EDIT_GUIDED_PILL, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, { meta: 'ip.proto', operator: '=', value: 'boom' }, 'action pillData has the right value');
      }
    };

    const validateDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
      action.promise.catch((error) => {
        assert.equal(error.meta, 'You must enter an 8-bit Integer.', 'Expected validaiton error');
        done();
      });
    };

    // this thunk will shoot out 2 actions - one to edit and one to validate
    const thunk = guidedCreators.editGuidedPill({
      pillData: {
        meta: 'ip.proto',
        operator: '=',
        value: 'boom'
      },
      position: 0
    });
    thunk(myDispatch);
  });

  test('editGuidedPill action creator returns proper type and payload for complex filter', function(assert) {
    assert.expect(3);
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    // Dispatch inside editGuidedPill
    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.EDIT_GUIDED_PILL, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, { complexFilterText: 'foobarfoobarFOObaz ==== foooOOOOOOooo' }, 'action pillData has the right value');
      }
    };

    // Dispatch inside _clientSideValidation
    const validateDispatch = (action) => {
      action(validateDispatch2, getState);
    };

    // Dispatch inside _serverSideValidation
    const validateDispatch2 = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
    };

    // this thunk will shoot out 2 actions - one to edit and one to validate
    const thunk = guidedCreators.editGuidedPill({
      pillData: {
        complexFilterText: 'foobarfoobarFOObaz ==== foooOOOOOOooo'
      },
      position: 0
    });
    thunk(myDispatch);
  });

  test('editGuidedPill action creator returns proper type and payload for Text filter', function(assert) {
    assert.expect(2);
    const done = assert.async();
    const getState = () => {};
    const pillData = { searchTerm: 'foobar', type: 'text' };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.EDIT_GUIDED_PILL, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, pillData, 'action pillData has the right value');
      }
    };

    const validateDispatch = () => {
      assert.notOk('Should not validate Text filters');
    };

    // this thunk will shoot out 1 action to edit, but not one for validation
    const thunk = guidedCreators.editGuidedPill({ pillData, position: 0 });

    thunk(myDispatch);

    return settled().then(() => {
      done();
    });
  });

  test('deleteGuidedPill action creator returns proper types/payloads', function(assert) {
    const done = assert.async(2);
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markSelected(['1', '2'])
      .build();

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, state.investigate.queryNode.pillsData, 'action pillData has the right value');
      done();
    };

    const firstDispatch = (action) => {
      // if is function, is _deselectAllGuidedPills thunk
      if (typeof action === 'function') {
        const thunk2 = action;
        const getState = () => state;
        thunk2(secondDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, 'foo', 'action pillData has the right value');
        done();
      }
    };

    const thunk1 = guidedCreators.deleteGuidedPill({
      pillData: 'foo'
    });

    thunk1(firstDispatch);
  });

  test('deleteSelectedGuidedPills action creator returns proper types/payloads when a focused pill is passed in which is not selected', function(assert) {
    const done = assert.async(2);
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markFocused(['1'])
      .build();

    // beacuse there is no pill selected, deselectAllGuidedPills will not be triggered
    const secondDispatch = (action) => {
      if (typeof action === 'function') {
        done();
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [{ pillData: 'foo' } ], 'action pillData has the right value');
        done();
      }
    };

    const firstDispatch = (action) => {
      const thunk2 = action;
      const getState = () => state;
      thunk2(secondDispatch, getState);
    };

    const thunk1 = guidedCreators.deleteSelectedGuidedPills({
      pillData: 'foo'
    });

    thunk1(firstDispatch);
  });

  test('deleteSelectedGuidedPills action creator returns proper types/payloads when a focused pill is passed in which is also selected', function(assert) {
    const done = assert.async(2);
    assert.expect(4);
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markSelected(['1', '2'])
      .markFocused(['1'])
      .build();

    // There should be still one pill present which is selected
    // DeselectAll should catch that pill after focused pill is deleted
    const thirdDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, state.investigate.queryNode.pillsData, 'action pillData has the right value');
      done();
    };

    // beacuse there is no pill selected, deselectAllGuidedPills will not be triggered
    const secondDispatch = (action) => {
      if (typeof action === 'function') {
        const thunk3 = action;
        const getState = () => state;
        thunk3(thirdDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [{ pillData: 'foo' } ], 'action pillData has the right value');
        done();
      }
    };

    const firstDispatch = (action) => {
      const thunk2 = action;
      const getState = () => state;
      thunk2(secondDispatch, getState);
    };

    const thunk1 = guidedCreators.deleteSelectedGuidedPills({
      pillData: 'foo'
    });

    thunk1(firstDispatch);
  });


  test('deselectGuidedPills action creator returns proper type and payload', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .pillsDataPopulated()
      .build()
      .investigate
      .queryNode;

    const action = guidedCreators.deselectGuidedPills({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('deselectAllGuidedPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();

    const getState = () => {
      return new ReduxDataHelper().pillsDataPopulated().markSelected(['1', '2']).build();
    };
    const { pillsData } = getState().investigate.queryNode;

    const thunk = guidedCreators.deselectAllGuidedPills();

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };

    thunk(dispatch, getState);
  });

  test('selectGuidedPills action creator returns proper type and payload', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .pillsDataPopulated()
      .build()
      .investigate
      .queryNode;

    const action = guidedCreators.selectGuidedPills({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('_serverSideValidation will flag isInvalid and add a validation error in state from the response returned from web socket', async function(assert) {
    assert.expect(2);

    const mockedPillsData = [
      {
        meta: 'sessionid',
        operator: '=',
        value: 242424242424242424242424,
        id: 1
      }
    ];

    const done = throwSocket({ methodToThrow: 'query', modelNameToThrow: 'core-query-validate', message: invalidServerResponse });

    new ReduxDataHelper((state) => patchReducer(this, state)).language().hasSummaryData(true, 1).pillsDataPopulated(mockedPillsData).build();

    const redux = this.owner.lookup('service:redux');
    const init = bindActionCreators(guidedCreators._serverSideValidation, redux.dispatch.bind(redux));

    const position = 0;
    init({
      meta: 'sessionid',
      operator: '=',
      value: '242424242424242424242424',
      id: 1
    }, position);

    return settled().then(() => {
      const { investigate: { queryNode: { pillsData } } } = redux.getState();
      const currentPill = pillsData[position];
      assert.ok(currentPill.isInvalid, 'Expected error flag');
      assert.equal(currentPill.validationError.message, 'expecting <comma-separated list of numeric ranges, values, or value aliases> or <comma-separated list of keys> here: \'242424242424242424242424\'', 'Excpected server validation error');
      done();
    });
  });

  test('openGuidedPillForEdit dispatches the proper events', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markEditing(['1'])
      .build()
      .investigate
      .queryNode;

    const action = guidedCreators.openGuidedPillForEdit({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.OPEN_GUIDED_PILL_FOR_EDIT, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('addFreeFormFilter action creator returns proper type and payload, and validates if pill is not complex', function(assert) {
    assert.expect(3);
    const thunk = guidedCreators.addFreeFormFilter({
      pillData: {
        type: 'query',
        meta: 'medium',
        operator: '=',
        value: '50'
      }
    });

    const getState = () => {
      return new ReduxDataHelper().language().hasRequiredValuesToQuery().build();
    };

    const dispatch = (action) => {
      if (typeof action === 'function') {
        const thunk3 = action;
        thunk3(secondDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.ADD_PILL, 'action has the correct type');
        assert.propEqual(action.payload.pillData, {
          type: 'query',
          meta: 'medium',
          operator: '=',
          value: '50'
        }, 'action pillData has the right value');
      }
    };
    const secondDispatch = (action) => {
      action(thirdDispatch, getState);
    };
    const thirdDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
    };

    thunk(dispatch, getState);
  });

  test('addFreeFormFilter action creator returns proper type and payload, and validates if the pill is complex', function(assert) {
    assert.expect(3);
    const action = guidedCreators.addFreeFormFilter({
      pillData: {
        type: 'complex',
        complexFilterText: '(medium = 50 && service = 443)'
      }
    });

    const getState = () => {
      return new ReduxDataHelper().language().serviceId().build();
    };

    const addPillDispatch = (action) => {
      if (typeof action === 'function') {
        action(clientSideValidationDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.ADD_PILL, 'action has the correct type');
        assert.propEqual(action.payload.pillData, {
          type: 'complex',
          complexFilterText: '(medium = 50 && service = 443)'
        }, 'action pillData has the right value and is a complex pill');
      }
    };
    const clientSideValidationDispatch = (action) => {
      if (typeof action === 'function') {
        action(serverSideValidationDispatch, getState);
      }
    };
    const serverSideValidationDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
    };

    action(addPillDispatch, getState);
  });

  test('updatedFreeFormText action creator returns proper type and payload', function(assert) {
    assert.expect(2);
    const thunk = guidedCreators.updatedFreeFormText('medium = 50');

    const getState = () => {
      return new ReduxDataHelper().language().build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.UPDATE_FREE_FORM_TEXT, 'action has the correct type');
      assert.propEqual(action.payload.pillData, {
        type: 'query',
        meta: 'medium',
        operator: '=',
        value: '50'
      }, 'action pillData has the right value');
    };

    thunk(dispatch, getState);
  });

  test('resetGuidedPill action creator returns proper type and payload', function(assert) {
    const action = guidedCreators.resetGuidedPill('foo');
    assert.equal(action.type, ACTION_TYPES.RESET_GUIDED_PILL, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, 'foo', 'action pillData has the right value');
  });

  test('selectAllPillsTowardsDirection -> right dispatches the proper events', function(assert) {
    assert.expect(2);
    const done = assert.async();

    const position = 0;
    const direction = 'right';
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .markSelected(['1'])
        .build();
    };
    const { investigate: { queryNode: { pillsData } } } = getState();

    const myDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData will contain the pills that will need to be selected');
      done();
    };

    const thunk = guidedCreators.selectAllPillsTowardsDirection(position, direction);
    thunk(myDispatch, getState);
  });

  test('selectAllPillsTowardsDirection -> left dispatches the proper events', function(assert) {
    assert.expect(2);
    const done = assert.async();

    const position = 1;
    const direction = 'left';

    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .markSelected(['2'])
        .build();
    };

    const { investigate: { queryNode: { pillsData } } } = getState();

    const myDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData will contain the pills that will need to be selected');
      done();
    };

    const thunk = guidedCreators.selectAllPillsTowardsDirection(position, direction);
    thunk(myDispatch, getState);
  });

  test('deleteAllGuidedPills dispatches action to deleteGuidedPill', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .build();
    };
    const done = assert.async();
    const { investigate: { queryNode: { pillsData } } } = getState();

    const deleteAllPillsDispatch = (action) => {
      if (typeof action === 'function') {
        const thunk = action;
        thunk(secondDispatch, getState);
      }
    };

    const secondDispatch = (action) => {
      if (typeof action === 'function') {
        // dispatch to deselectAllGuidedPills is sent out
        // But because there would be no selected pills in the
        // first place, the action will never be triggered.
        done();
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      }
    };

    const thunk = guidedCreators.deleteAllGuidedPills();
    thunk(deleteAllPillsDispatch, getState);
  });

});