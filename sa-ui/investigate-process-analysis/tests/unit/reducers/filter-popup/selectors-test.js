import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getFilterTabs } from 'investigate-process-analysis/reducers/filter-popup/selectors';

module('Unit | Selectors | process-filter', function() {
  test('Returns tabs with active tab', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        filterPopup: {
          activeFilterTab: 'network'
        }
      }
    });
    const result = getFilterTabs(state);
    assert.equal(result.length, 4, '4 tabs are present');
    assert.equal(result.find((option) => option.name === 'network').selected, true, 'network tab is selected');
  });
});