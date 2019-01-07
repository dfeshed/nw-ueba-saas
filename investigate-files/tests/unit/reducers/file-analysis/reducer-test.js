import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/file-analysis/reducer';
import * as ACTION_TYPES from 'investigate-shared/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

const initialState = {
  'fileData': null,
  'filePropertiesData': null,
  'fileDataLoadingStatus': null
};

module('Unit | Reducers | File Analysis', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('FETCH_FILE_ANALYZER_FILE_PROPERTIES_DATA sets the filedata', function(assert) {
    const previous = Immutable.from({
      'fileData': null,
      'filePropertiesData': null,
      'isFileAnalysisView': false
    });

    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_FILE_ANALYZER_FILE_PROPERTIES_DATA,
      payload: {
        data: { format: 'macho' }
      }
    });
    const result = reducer(previous, action);

    assert.equal(result.filePropertiesData.format, 'macho', 'updated with the new file data');
  });

  test('FETCH_FILE_ANALYZER_DATA sets the filedata', function(assert) {
    const previous = Immutable.from({
      'fileData': null,
      'filePropertiesData': null,
      'isFileAnalysisView': false
    });
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_FILE_ANALYZER_DATA,
      payload: {
        data: { name: 'test data' }
      }
    });
    const result = reducer(previous, action);

    assert.equal(result.fileData.name, 'test data', 'updated with the new file data');
  });

});
