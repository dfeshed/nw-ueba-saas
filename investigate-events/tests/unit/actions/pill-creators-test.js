import { module, test } from 'qunit';
import { settled } from '@ember/test-helpers';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper, { TEXT_PILL_DATA } from '../../helpers/redux-data-helper';
import pillCreators from 'investigate-events/actions/pill-creators';
import ACTION_TYPES from 'investigate-events/actions/types';
import { QueryFilter, ComplexFilter } from 'investigate-events/util/filter-types';
import { createOperator } from 'investigate-events/util/query-parsing';
import { OPERATOR_AND, OPERATOR_OR } from 'investigate-events/constants/pill';
import {
  OpenParen,
  CloseParen,
  OperatorAnd
} from 'investigate-events/util/grammar-types';

const createPill = (meta, operator, value) => QueryFilter.create({ meta, operator, value });
const createAnd = () => OperatorAnd.create();
const createClose = () => CloseParen.create();
const createOpen = () => OpenParen.create();
const addInvalidProp = (pill) => {
  return QueryFilter.create({
    ...pill,
    isInvalid: true,
    validationError: {
      message: 'Client error'
    }
  });
};

module('Unit | Actions | Pill Creators', function(hooks) {
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
    const thunk = pillCreators.addGuidedPill({
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
    const thunk = pillCreators.addGuidedPill({
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
    const thunk = pillCreators.batchAddPills({
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
    const thunk = pillCreators.editGuidedPill({
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
    const thunk = pillCreators.editGuidedPill({
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
    const thunk = pillCreators.editGuidedPill({ pillData, position: 0 });

    thunk(myDispatch);

    return settled().then(() => {
      done();
    });
  });

  test('deleteGuidedPill action creator returns proper types/payloads', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .build();
    const getState = () => state;

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'Action has the correct type');
      assert.deepEqual(action.payload.pillData, ['foo'], 'Action pillData has the right value');
      done();
    };

    const action = pillCreators.deleteGuidedPill({
      pillData: ['foo']
    });

    action(dispatch, getState);
  });

  test('deleteGuidedPill action creator includes missins twins', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper()
      .language()
      .pillsDataWithParens()
      .markFocused(['3'])
      .build();
    const getState = () => state;
    const [ , , closeParen ] = state.investigate.queryNode.pillsData;

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'Action has the correct type');
      assert.equal(action.payload.pillData.length, 2, 'Action pillData has the right number of pills');
      assert.equal(action.payload.pillData[0].id, '3', 'Action pillData includes focused pill');
      assert.equal(action.payload.pillData[1].id, '1', 'Action pillData includes focused pills twin');
      done();
    };

    const action = pillCreators.deleteGuidedPill({
      pillData: [closeParen]
    });

    action(dispatch, getState);
  });

  test('deleteSelectedGuidedPills action creator returns proper types/payloads when a focused pill is passed in which is not selected', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markFocused(['1'])
      .build();

    const getState = () => state;

    const secondDispatch = async(action) => {
      assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, [{ pillData: 'foo' } ], 'action pillData has the right value');
      done();
    };

    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = pillCreators.deleteSelectedGuidedPills({
      pillData: 'foo'
    });

    thunk1(firstDispatch);
  });

  test('deleteSelectedGuidedPills action creator returns proper types/payloads when a focused pill is passed in which is also selected', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markSelected(['1', '2', '3'])
      .markFocused(['1'])
      .build();
    const getState = () => state;

    const secondDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, [{ pillData: 'foo' } ], 'action pillData has the right value');
      done();
    };

    const firstDispatch = (thunk2) => {
      thunk2(secondDispatch, getState);
    };

    const thunk1 = pillCreators.deleteSelectedGuidedPills({
      pillData: 'foo'
    });

    thunk1(firstDispatch);
  });

  test('deleteSelectedGuidedPills when a focused and selected paren set is passed in, it removes parens and content', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper()
      .language()
      .pillsDataWithParens()// ( P )
      .markSelected(['1', '3'])
      .markFocused(['1'])
      .build();
    const getState = () => state;
    const [openParen, pill, closeParen] = state.investigate.queryNode.pillsData;

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
      assert.equal(action.payload.pillData[0].type, openParen.type, 'action pillData[0] is correct type');
      assert.equal(action.payload.pillData[1].type, pill.type, 'action pillData[1] is correct type');
      assert.equal(action.payload.pillData[2].type, closeParen.type, 'action pillData[2] is correct type');
      done();
    };

    const thunk = pillCreators.deleteSelectedGuidedPills(openParen);

    thunk(dispatch, getState);
  });

  test('openGuidedPillForEdit dispatches the proper events', function(assert) {
    const { pillsData } = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .markEditing(['1'])
      .build()
      .investigate
      .queryNode;

    const action = pillCreators.openGuidedPillForEdit({
      pillData: pillsData
    });

    assert.equal(action.type, ACTION_TYPES.OPEN_GUIDED_PILL_FOR_EDIT, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
  });

  test('addFreeFormFilter action creator returns proper type and payload, and validates if pill is not complex', function(assert) {
    assert.expect(3);
    const thunk = pillCreators.addFreeFormFilter({
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
    const action = pillCreators.addFreeFormFilter({
      pillData: {
        type: 'complex',
        complexFilterText: '(medium = 50 AND service = 443)'
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
          complexFilterText: '(medium = 50 AND service = 443)'
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

  test('addTextFilter action creator returns second action if pill before is OR', function(assert) {
    assert.expect(5);
    const pillData = {
      type: 'text',
      isEditing: false,
      isFocused: false,
      isInvalid: false,
      isSelected: false,
      isValidationInProgress: false,
      searchTerm: 'blahblahblah'
    };
    const thunk = pillCreators.addTextFilter({ pillData, position: 2 });

    const getState = () => {
      return new ReduxDataHelper()
        .pillsDataWithOr()
        .build();
    };

    const dispatch = (arr) => {
      assert.ok(Array.isArray(arr));
      assert.strictEqual(arr.length, 2);
      assert.deepEqual(arr[0], {
        type: ACTION_TYPES.ADD_PILL,
        payload: {
          pillData,
          position: 2,
          shouldAddFocusToNewPill: false
        }
      }, 'first action should be add pill');
      assert.strictEqual(arr[1].type, ACTION_TYPES.REPLACE_LOGICAL_OPERATOR, 'second action should be replace logical operator');
      assert.strictEqual(arr[1].payload.pillData.type, OPERATOR_AND, 'should replace with an AND');
    };

    thunk(dispatch, getState);
  });

  test('addTextFilter action creator returns three actions if pills before and after are OR', function(assert) {
    assert.expect(7);
    const pillData = {
      type: 'text',
      isEditing: false,
      isFocused: false,
      isInvalid: false,
      isSelected: false,
      isValidationInProgress: false,
      searchTerm: 'blahblahblah'
    };
    const thunk = pillCreators.addTextFilter({ pillData, position: 2 });

    const getState = () => {
      return new ReduxDataHelper()
        .pillsDataWithOr()
        .insertPillAt({
          id: '3',
          type: 'operator-or',
          isFocused: false,
          isSelected: false
        }, 2)
        .build();
    };

    const dispatch = (arr) => {
      assert.ok(Array.isArray(arr));
      assert.strictEqual(arr.length, 3);
      assert.deepEqual(arr[0], {
        type: ACTION_TYPES.ADD_PILL,
        payload: {
          pillData,
          position: 2,
          shouldAddFocusToNewPill: false
        }
      }, 'first action should be add pill');
      assert.strictEqual(arr[1].type, ACTION_TYPES.REPLACE_LOGICAL_OPERATOR, 'second action should be replace logical operator');
      assert.strictEqual(arr[1].payload.pillData.type, OPERATOR_AND, 'should replace with an AND');
      assert.strictEqual(arr[2].type, ACTION_TYPES.REPLACE_LOGICAL_OPERATOR, 'second action should be replace logical operator');
      assert.strictEqual(arr[2].payload.pillData.type, OPERATOR_AND, 'should replace with an AND');
    };

    thunk(dispatch, getState);
  });

  test('addTextFilter action creator returns only one action if pill before is AND', function(assert) {
    assert.expect(3);
    const pillData = {
      type: 'text',
      isEditing: false,
      isFocused: false,
      isInvalid: false,
      isSelected: false,
      isValidationInProgress: false,
      searchTerm: 'blahblahblah'
    };
    const thunk = pillCreators.addTextFilter({ pillData, position: 2 });

    const getState = () => {
      return new ReduxDataHelper()
        .pillsDataEmpty()
        .pillsDataWithAnd()
        .build();
    };

    const dispatch = (arr) => {
      assert.ok(Array.isArray(arr));
      assert.strictEqual(arr.length, 1);
      assert.deepEqual(arr[0], {
        type: ACTION_TYPES.ADD_PILL,
        payload: {
          pillData,
          position: 2,
          shouldAddFocusToNewPill: false
        }
      }, 'first action should be add pill');
    };

    thunk(dispatch, getState);
  });

  test('updatedFreeFormText action creator returns proper type and payload', function(assert) {
    assert.expect(2);
    const action = pillCreators.updatedFreeFormText('medium = 50');

    assert.strictEqual(action.type, ACTION_TYPES.UPDATE_FREE_FORM_TEXT);
    assert.strictEqual(action.payload.freeFormText, 'medium = 50');
  });

  test('resetGuidedPill action creator returns proper type and payload', function(assert) {
    const action = pillCreators.resetGuidedPill('foo');
    assert.equal(action.type, ACTION_TYPES.RESET_GUIDED_PILL, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, 'foo', 'action pillData has the right value');
  });

  test('deleteAllGuidedPills dispatches action to deleteGuidedPill', function(assert) {
    const done = assert.async();
    const state = new ReduxDataHelper()
      .language()
      .pillsDataPopulated()
      .build();
    const getState = () => state;
    const { investigate: { queryNode: { pillsData } } } = state;

    const deleteAllPillsDispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.DELETE_GUIDED_PILLS, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillsData, 'action pillData has the right value');
      done();
    };

    const firstDispatch = (thunk2) => {
      thunk2(deleteAllPillsDispatch, getState);
    };

    const thunk = pillCreators.deleteAllGuidedPills();

    thunk(firstDispatch, getState);
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

    const thunk = pillCreators.cancelPillCreation(1);
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

    const thunk = pillCreators.cancelPillCreation(2);
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
    const thunk = pillCreators.cancelPillCreation(3);
    thunk(dispatch, getState);
  });

  test('addLogicalOperator action creator returns proper type and payload', function(assert) {
    assert.expect(3);
    const getState = () => {
      return new ReduxDataHelper()
        .pillsDataPopulated()
        .build();
    };

    const pillData = createOperator(OPERATOR_AND);
    const position = 4;
    const action = pillCreators.addLogicalOperator({ pillData, position });

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.INSERT_LOGICAL_OPERATOR, 'action has the correct type');
      assert.deepEqual(action.payload.pillData, pillData, 'action pillData has the right value');
      assert.deepEqual(action.payload.position, position, 'action position has the right value');
    };

    action(dispatch, getState);
  });

  test('addLogicalOperator action creator replaces OR with AND when after text pill as first pill', function(assert) {
    assert.expect(3);
    const getState = () => {
      return new ReduxDataHelper()
        .pillsDataText()
        .build();
    };

    const pillData = createOperator(OPERATOR_OR);
    const position = 1;
    const action = pillCreators.addLogicalOperator({ pillData, position });

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.INSERT_LOGICAL_OPERATOR, 'action has the correct type');
      assert.strictEqual(action.payload.pillData.type, OPERATOR_AND, 'action pillData has the right value');
      assert.deepEqual(action.payload.position, position, 'action position has the right value');
    };

    action(dispatch, getState);
  });

  test('addLogicalOperator action creator replaces OR with AND after text pill when text pill is not first', function(assert) {
    assert.expect(3);
    let [ textPill ] = TEXT_PILL_DATA;
    textPill = { ...textPill, id: '3' };
    const getState = () => {
      return new ReduxDataHelper()
        .pillsDataWithAnd()
        .insertPillAt(textPill, 2)
        .build();
    };

    const pillData = createOperator(OPERATOR_OR);
    const position = 3;
    const action = pillCreators.addLogicalOperator({ pillData, position });

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.INSERT_LOGICAL_OPERATOR, 'action has the correct type');
      assert.strictEqual(action.payload.pillData.type, OPERATOR_AND, 'action pillData has the right value');
      assert.deepEqual(action.payload.position, position, 'action position has the right value');
    };

    action(dispatch, getState);
  });

  test('replaceLogicalOperator action creator returns proper type and payload', function(assert) {
    const pillData = createOperator(OPERATOR_OR);
    const position = 2;
    const action = pillCreators.replaceLogicalOperator({ pillData, position });
    assert.equal(action.type, ACTION_TYPES.REPLACE_LOGICAL_OPERATOR, 'action has the correct type');
    assert.deepEqual(action.payload.pillData, pillData, 'action pillData has the right value');
    assert.deepEqual(action.payload.position, 2, 'action position has the right value');
  });

  test('focusAndToggleLogicalOperator action creator returns proper type and payload', function(assert) {
    assert.expect(5);

    const pillData = createOperator(OPERATOR_OR);
    pillData.isFocused = false;
    const position = 2;
    const thunk = pillCreators.focusAndToggleLogicalOperator({ pillData, position });

    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .markFocused(['1'])
        .build();
    };

    const dispatch = (actions) => {
      assert.equal(actions[0].type, ACTION_TYPES.REMOVE_FOCUS_GUIDED_PILL, 'focus action has the correct type');

      assert.equal(actions[1].type, ACTION_TYPES.REPLACE_LOGICAL_OPERATOR, 'replace action has the correct type');
      assert.equal(actions[1].payload.pillData.isFocused, true, 'action pillData has the right value');
      assert.equal(actions[1].payload.pillData.type, OPERATOR_AND, 'action pillData has the right value');
      assert.equal(actions[1].payload.position, 3, 'action position has the right value');
    };

    thunk(dispatch, getState);
  });

  test('wrapWithParens will figure out startIndex and endIndex for selected pills and return them as payload', function(assert) {
    const action = pillCreators.wrapWithParens({ startIndex: 0, endIndex: 2 });
    assert.equal(action.type, ACTION_TYPES.WRAP_WITH_PARENS, 'action has the correct type');
    assert.deepEqual(action.payload.startIndex, 0, 'action startIndex has the right value');
    assert.deepEqual(action.payload.endIndex, 2, 'action endIndex has the right value');
  });

  test('unstash pills sends out action if pills are stashed', async function(assert) {
    assert.expect(1);
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .populateOriginalPills()
        .isPillsDataStashed()
        .build();
    };

    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.UNSTASH_PILLS, 'action has the correct type');
    };
    const thunk = pillCreators.unstashPills();
    thunk(dispatch, getState);
  });

  test('unstash pills will not send out action if pills are not stashed', async function(assert) {
    assert.expect(0);
    const getState = () => {
      return new ReduxDataHelper()
        .language()
        .pillsDataPopulated()
        .populateOriginalPills()
        .isPillsDataStashed(false)
        .build();
    };

    const dispatch = () => {
      assert.notOk('This action was not called');
    };
    const thunk = pillCreators.unstashPills();
    thunk(dispatch, getState);
  });

  test('batchValidate action creator returns proper type and payload', function(assert) {
    assert.expect(11);
    const done = assert.async();
    const getState = () => {
      // Already has 3 pills
      return new ReduxDataHelper().language().pillsDataPopulated().build();
    };

    const myDispatch = (action) => {
      if (typeof action === 'function') {
        action(updateFlagDispatch, getState);
      } else {
        assert.equal(action.type, ACTION_TYPES.BATCH_ADD_PILLS, 'action BATCH_ADD_PILLS was called');
        assert.ok(action.payload.pillsData[0] instanceof QueryFilter, 'action pillsData has the right value');
        assert.equal(action.payload.initialPosition, 3, 'action initialPosition has the right value');
      }
    };

    let i = 0;
    // This should be called as soon as we have some validatable pills to process
    const updateFlagDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateServerSideDispatch, getState);
      } else if (i === 0) {
        i++;
        assert.equal(action.type, ACTION_TYPES.VALIDATION_IN_PROGRESS, 'Should contain validation in progress action');
        assert.deepEqual(action.payload.positionArray, [3, 5, 7, 9, 11], 'Should contain all positions');
      } else {
        // If there are any invalid pills, BATCH_VALIDATE_GUIDED_PILL will be sent out with those failed
        // client side pills
        const { pillsData } = action.payload;
        assert.equal(action.type, ACTION_TYPES.BATCH_VALIDATE_GUIDED_PILL, 'BATCH_VALIDATE_GUIDED_PILL is called with invalid client side pill');
        assert.ok(pillsData[0].pillData.isInvalid, 'Failed client side error');
        assert.equal(pillsData[0].position, 5, 'it should have the correct position so that state can update it');
      }
    };

    // Called twice because of its recursive nature.
    const validateServerSideDispatch = (action) => {
      if (typeof action === 'function') {
        action(validateServerSideDispatch, getState);
      } else {
        const { pillsData } = action.payload;
        assert.equal(action.type, ACTION_TYPES.BATCH_VALIDATE_GUIDED_PILL, 'BATCH_VALIDATE_GUIDED_PILL is called with invalid server side pills');
        assert.ok(pillsData[0].pillData.isInvalid, 'Should have failed server validation');
        assert.equal(pillsData[0].position, 11, 'it should have the correct position so that state can update it');
        done();
      }
    };

    const pillsData = [
      createPill('medium', '=', '1'),
      createAnd(),
      addInvalidProp(createPill('medium', '=', 'foo')), // client side failure
      createOpen(),
      createPill('action', 'exists'),
      createClose(),
      createPill('action', '=', 'bar'),
      createAnd(),
      createPill('action', '=', 'xxx') // server side failure
    ];
    const thunk = pillCreators.batchAddPills({
      pillsData,
      initialPosition: 3
    });
    thunk(myDispatch);
  });
});