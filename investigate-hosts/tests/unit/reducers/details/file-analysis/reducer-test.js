import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/file-analysis/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';

const initialState = {
  isFileAnalysisView: true,
  fileData: {
    format: 'pe'
  }
};

module('Unit | Reducers | File Analysis', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });


  test('The TOGGLE_FILE_ANALYZER will toggle isFileAnalysisView in the state', function(assert) {
    const previous = Immutable.from({
      isFileAnalysisView: true,
      fileData: {
        format: 'pe'
      }
    });
    // without payload
    const resultWithoutPayload = reducer(previous, { type: ACTION_TYPES.TOGGLE_FILE_ANALYZER });

    assert.equal(resultWithoutPayload.isFileAnalysisView, false);

    // with payload
    const resultWithPayload = reducer(previous, { type: ACTION_TYPES.TOGGLE_FILE_ANALYZER, payload: false });

    assert.equal(resultWithPayload.isFileAnalysisView, false);
  });

  test('FETCH_FILE_ANALYZER_DATA sets the filedata', function(assert) {
    const previous = Immutable.from({
      isFileAnalysisView: true,
      fileData: {
        format: 'pe'
      }
    });
    const result = reducer(previous, { type: ACTION_TYPES.FETCH_FILE_ANALYZER_DATA, payload: {
      data: { format: 'macho' }
    } });

    assert.equal(result.fileData.format, 'macho', 'updated with the new file data');
  });

});
