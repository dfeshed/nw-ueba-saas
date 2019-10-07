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
import { QueryFilter, ComplexFilter } from 'investigate-events/util/filter-types';
import { createOperator } from 'investigate-events/util/query-parsing';
import { OPERATOR_AND, OPERATOR_OR } from 'investigate-events/constants/pill';

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

  test('addGuidedPill action creator invalidates text that returns multiple pills from the parser', function(assert) {
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
        assert.deepEqual(action.payload.pillData, { meta: 'alias.ip', operator: '=', value: '192.168.0.1 /24' }, 'action pillData has the right value');
        assert.equal(action.payload.position, 0, 'action position has the right value');
      }
    };

    const validateDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.VALIDATE_GUIDED_PILL, 'action has the correct type - validate');
      action.promise.catch((error) => {
        assert.equal(error.meta, 'The text you entered seems like more than one filter. (Try removing spaces).', 'Expected validaiton error');
        done();
      });
    };

    // this thunk will shoot out 2 actions - one to add and one to validate
    const thunk = guidedCreators.addGuidedPill({
      pillData: {
        meta: 'alias.ip',
        operator: '=',
        value: '192.168.0.1 /24'
      },
      position: 0
    });
    thunk(myDispatch);
  });

  test('batchAddPills action creator returns proper type and payload', function(assert) {
    assert.expect(10);
    const getState = () => {
      return new ReduxDataHelper().language().invalidPillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateClientSideDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.BATCH_ADD_PILLS, 'action has the correct type');
        assert.ok(action.payload.pillsData[0] instanceof QueryFilter, 'action pillsData has the right value');
        assert.ok(action.payload.pillsData[1] instanceof ComplexFilter, 'action pillsData has the right value');
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
    const queryPill = QueryFilter.create({
      meta: 'ip.proto',
      operator: '=',
      value: '\'boom\''
    });
    const complexPill = ComplexFilter.create({
      complexFilterText: 'starttime = 1 || starttime = 2'
    });
    const thunk = guidedCreators.batchAddPills({
      pillsData: [queryPill, complexPill],
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
      .markSelected(['1', '2', '3'])
      .build();
    const getState = () => state;

    const fourthDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, '4th action has the correct type');
      assert.deepEqual(action.payload.pillData, state.investigate.queryNode.pillsData, '4th action pillData has the right value');
      done();
    };

    const firstDispatch = (action) => {
      // if is function, is deselectAllGuidedPills thunk, which has chain of thunks to
      // get through
      if (typeof action === 'function') {

        const thirdDispatch = (thunk4) => {
          thunk4(fourthDispatch, getState);
        };
        const secondDispatch = (thunk3) => {
          thunk3(thirdDispatch, getState);
        };
        const thunk2 = action;
        thunk2(secondDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, '1st action has the correct type');
        assert.deepEqual(action.payload.pillData, ['foo'], '1st action pillData has the right value');
        done();
      }
    };

    const thunk1 = guidedCreators.deleteGuidedPill({
      pillData: ['foo']
    });

    thunk1(firstDispatch, getState);
  });

  test('deleteGuidedPill action creator removes empty paren sets resulting from deleting pill', function(assert) {
    const done = assert.async(2);
    let state = new ReduxDataHelper()
      .pillsDataWithParens()
      .markSelected(['2'])
      .build();
    const [, deletedPill] = state.investigate.queryNode.pillsData;
    let firstCall = true;
    let openParen, closeParen;

    const dispatch = (action) => {
      if (action.type === ACTION_TYPES.DELETE_GUIDED_PILLS) {
        if (firstCall) {
          assert.deepEqual(action.payload.pillData, [deletedPill], 'action pillData has the right value, first call');
          // set state to what it should be at this point
          state = new ReduxDataHelper()
            .pillsDataWithEmptyParens()
            .build();
          // save off the parens for testing later
          [openParen, closeParen] = state.investigate.queryNode.pillsData;
          firstCall = false;
          done();
        } else {
          assert.deepEqual(action.payload.pillData, [openParen, closeParen], 'action pillData has the right value, second call');
          done();
        }

      } else if (typeof action === 'function') {
        action(dispatch, () => state);
      }
    };

    const thunk = guidedCreators.deleteGuidedPill({
      pillData: [deletedPill]
    });

    thunk(dispatch, () => state);
  });

  test('deleteGuidedPill action creator includes missins twins', function(assert) {
    const done = assert.async(3);
    const state = new ReduxDataHelper()
      .language()
      .pillsDataWithParens()
      .markFocused(['3'])
      .build();
    const getState = () => state;

    const firstDispatch = (action) => {
      // if is function, is deselectAllGuidedPills thunk, which has chain of thunks to
      // get through
      if (typeof action === 'function') {
        done();
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
        assert.equal(action.payload.pillData.length, 2, 'action pillData has the right number of pills');
        assert.equal(action.payload.pillData[0].id, '3', 'action pillData includes focused pill');
        assert.equal(action.payload.pillData[1].id, '1', 'action pillData includes focused pills twin');
        done();
      }
    };

    const thunk1 = guidedCreators.deleteGuidedPill({
      pillData: [state.investigate.queryNode.pillsData[2]]
    });

    thunk1(firstDispatch, getState);
  });

  test('deleteSelectedGuidedPills action creator returns proper types/payloads when a focused pill is passed in which is not selected', function(assert) {
    const done = assert.async(3);
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markFocused(['1'])
      .build();

    const getState = () => state;

    const secondDispatch = async(action) => {
      if (typeof action === 'function') {
        const thunk3 = action;
        // does not get invoked
        const thirdDispatch = () => {};
        await thunk3(thirdDispatch, getState);
        done();
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [{ pillData: 'foo' } ], 'action pillData has the right value');
        done();
      }
    };

    const firstDispatch = (thunk2) => {
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
      .markSelected(['1', '2', '3'])
      .markFocused(['1'])
      .build();
    const getState = () => state;

    const fifthDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, '5th action has the correct type');
      assert.deepEqual(action.payload.pillData, state.investigate.queryNode.pillsData, '5th action pillData has the right value');
      done();
    };

    const fourthDispatch = (thunk5) => {
      thunk5(fifthDispatch, getState);
    };

    // There should be still one pill present which is selected
    // DeselectAll should catch that pill after focused pill is deleted
    const thirdDispatch = (thunk4) => {
      thunk4(fourthDispatch, getState);
    };

    // beacuse there is no pill selected, deselectAllGuidedPills will not be triggered
    const secondDispatch = (action) => {
      if (typeof action === 'function') {
        const thunk3 = action;
        thunk3(thirdDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, '2nd action has the correct type');
        assert.deepEqual(action.payload.pillData, [{ pillData: 'foo' } ], '2nd action pillData has the right value');
        done();
      }
    };

    const firstDispatch = (action) => {
      const thunk2 = action;
      thunk2(secondDispatch, getState);
    };

    const thunk1 = guidedCreators.deleteSelectedGuidedPills({
      pillData: 'foo'
    });

    thunk1(firstDispatch);
  });

  test('deselectGuidedPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper()
      .pillsDataPopulated()
      .build();
    const { pillsData } = state.investigate.queryNode;
    const getState = () => state;

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };
    const thunk1 = guidedCreators.deselectGuidedPills({
      pillData: pillsData
    });
    thunk1(firstDispatch, getState);
  });

  test('deselectGuidedPills action creator will collect missing twins and send for deselect', function(assert) {
    assert.expect(5);
    const done = assert.async();
    const state = new ReduxDataHelper()
      .pillsDataWithParens()
      .markSelected(['1', '3'])
      .build();

    const { pillsData } = state.investigate.queryNode;
    const getState = () => state;


    let calledOnce = false;
    const secondDispatch = (action) => {
      // 2nd call sends the pill
      if (calledOnce) {
        assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [pillsData[2]], 'action pillData has the right value');
        done();
      } else {
        // 1st call sends the twin of the pill
        calledOnce = true;
        assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [pillsData[0]], 'action pillData has the right value');
        assert.deepEqual(action.payload.shouldIgnoreFocus, true, 'should be ignoring focus on twin dispatch');
      }
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };
    const thunk1 = guidedCreators.deselectGuidedPills({
      // just pull out the selected 2nd twin and send it alone
      // would expect two dispatches, one to collect the missing twin
      pillData: [pillsData[2]]
    });
    thunk1(firstDispatch, getState);
  });

  test('deselectAllGuidedPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();

    const getState = () => {
      return new ReduxDataHelper().pillsDataPopulated().markSelected(['1', '2', '3']).build();
    };
    const { pillsData } = getState().investigate.queryNode;

    const thirdDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DESELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };
    const secondDispatch = (thunk3) => {
      thunk3(thirdDispatch, getState);
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = guidedCreators.deselectAllGuidedPills();
    thunk1(firstDispatch, getState);
  });

  test('selectGuidedPills action creator returns proper type and payload', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper().pillsDataPopulated().build();
    const { pillsData } = state.investigate.queryNode;
    const getState = () => state;

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = guidedCreators.selectGuidedPills({
      pillData: pillsData
    });
    thunk1(firstDispatch, getState);
  });

  test('selectGuidedPills action creator will collect missing twins and send for deselect', function(assert) {
    assert.expect(5);
    const done = assert.async();
    const state = new ReduxDataHelper()
      .pillsDataWithParens()
      .build();

    const { pillsData } = state.investigate.queryNode;
    const getState = () => state;

    let calledOnce = false;
    const secondDispatch = (action) => {
      // 2nd call sends the pill
      if (calledOnce) {
        assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [pillsData[2]], 'action pillData has the right value');
        done();
      } else {
        // 1st call sends the twin of the pill
        calledOnce = true;
        assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
        assert.deepEqual(action.payload.pillData, [pillsData[0]], 'action pillData has the right value');
        assert.deepEqual(action.payload.shouldIgnoreFocus, true, 'should be ignoring focus on twin dispatch');
      }
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };
    const thunk1 = guidedCreators.selectGuidedPills({
      // just pull out the  2nd twin and send it alone
      // would expect two dispatches, one to collect the missing twin
      pillData: [pillsData[2]]
    });
    thunk1(firstDispatch, getState);
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

    const thirdDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData will contain the pills that will need to be selected');
      done();
    };
    const secondDispatch = (thunk3) => {
      thunk3(thirdDispatch, getState);
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = guidedCreators.selectAllPillsTowardsDirection(position, direction);
    thunk1(firstDispatch, getState);
  });

  test('selectAllPillsTowardsDirection -> left dispatches the proper events', function(assert) {
    assert.expect(2);
    const done = assert.async();

    const position = 2;
    const direction = 'left';

    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .markSelected(['3'])
        .build();
    };

    const { investigate: { queryNode: { pillsData } } } = getState();

    const thirdDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.SELECT_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData will contain the pills that will need to be selected');
      done();
    };

    const secondDispatch = (thunk3) => {
      thunk3(thirdDispatch, getState);
    };
    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = guidedCreators.selectAllPillsTowardsDirection(position, direction);
    thunk1(firstDispatch, getState);
  });

  test('deleteAllGuidedPills dispatches action to deleteGuidedPill', function(assert) {
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .build();
    };
    const done = assert.async(2);
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

  test('cancelPillCreation dispatches action to delete parens if they are empty', function(assert) {
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataWithEmptyParens() // op, cp
        .build();
    };
    // extract pillsData for testing
    const { investigate: { queryNode: { pillsData } } } = getState();

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action payload has the right value');
      done();
    };

    const thunk = guidedCreators.cancelPillCreation(1);
    thunk(dispatch, getState);
  });

  test('cancelPillCreation DOES NOT dispatches action to delete parens if they are populated', function(assert) {
    assert.expect(0);
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataWithParens() // op, qp, cp
        .build();
    };

    const dispatch = (action) => {
      assert.notOk(true, `dispatched ${action.type} when it should not have`);
    };

    const thunk = guidedCreators.cancelPillCreation(2);
    thunk(dispatch, getState);
  });

  test('cancelPillCreation dispatches action to delete orphaned logical operator', function(assert) {
    const OR = createOperator(OPERATOR_OR);
    const done = assert.async();
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataWithParens() // ( P )
        .insertPillAt(OR, 2) //   ( P || )
        .build();
    };
    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
      assert.ok(Array.isArray(action.payload.pillData), 'action payload is correct type');
      assert.propEqual(action.payload.pillData[0], OR, 'action payload has the right value');
      done();
    };
    const thunk = guidedCreators.cancelPillCreation(3);
    thunk(dispatch, getState);
  });

  test('addLogicalOperator action creator returns proper type and payload', function(assert) {
    const pillData = createOperator(OPERATOR_AND);
    const position = 2;
    const action = guidedCreators.addLogicalOperator({ pillData, position });
    assert.equal(action.type, ACTION_TYPES.INSERT_LOGICAL_OPERATOR, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillData, 'action pillData has the right value');
    assert.deepEqual(action.payload.position, 2, 'action position has the right value');
  });

  test('replaceLogicalOperator action creator returns proper type and payload', function(assert) {
    const pillData = createOperator(OPERATOR_OR);
    const position = 2;
    const action = guidedCreators.replaceLogicalOperator({ pillData, position });
    assert.equal(action.type, ACTION_TYPES.REPLACE_LOGICAL_OPERATOR, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillData, 'action pillData has the right value');
    assert.deepEqual(action.payload.position, 2, 'action position has the right value');
  });

  test('focusAndToggleLogicalOperator action creator returns proper type and payload', function(assert) {
    assert.expect(5);

    const pillData = createOperator(OPERATOR_OR);
    pillData.isFocused = false;
    const position = 2;
    const thunk = guidedCreators.focusAndToggleLogicalOperator({ pillData, position });

    const dispatch1 = (action) => {
      if (typeof action === 'function') {
        // is call to removePillFocus

        const getState = () => {
          return new ReduxDataHelper()
            .language()
            .pillsDataPopulated()
            .markFocused(['1'])
            .build();
        };

        const dispatch2 = (action) => {
          assert.equal(action.type, ACTION_TYPES.REMOVE_FOCUS_GUIDED_PILL, 'focus action has the correct type');
        };

        action(dispatch2, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.REPLACE_LOGICAL_OPERATOR, 'replace action has the correct type');
        assert.equal(action.payload.pillData.isFocused, true, 'action pillData has the right value');
        assert.equal(action.payload.pillData.type, OPERATOR_AND, 'action pillData has the right value');
        assert.equal(action.payload.position, 3, 'action position has the right value');
      }
    };

    thunk(dispatch1);
  });
});

test('wrapWithParens will figure out startIndex and endIndex for selected pills and return them as payload', function(assert) {
  const action = guidedCreators.wrapWithParens({ startIndex: 0, endIndex: 2 });
  assert.equal(action.type, ACTION_TYPES.WRAP_WITH_PARENS, 'action has the correct type');
  assert.deepEqual(action.payload.startIndex, 0, 'action startIndex has the right value');
  assert.deepEqual(action.payload.endIndex, 2, 'action endIndex has the right value');
});