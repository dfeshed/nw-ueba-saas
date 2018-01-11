import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getCurrentPreferences, isDataEmpty, shouldShowStatus, getSelectedColumnGroup, getColumns } from 'investigate-events/reducers/investigate/data-selectors';
import EventColumnGroups from '../../data/subscriptions/investigate-columns/data';

module('Unit | Selectors | data-selectors');

test('get the current preferences to save', function(assert) {
  const response = {
    eventPreferences: {
      columnGroup: 'EMAIL'
    }
  };
  const state = Immutable.from({
    investigate: {
      data: {
        reconSize: 'max',
        columnGroup: 'EMAIL'
      },
      queryNode: {
        queryTimeFormat: 'DB'
      }
    },
    recon: {
      visuals: {
        currentReconView: 'TEXT',
        defaultLogFormat: 'LOG',
        defaultPacketFormat: 'PCAP',
        isReconExpanded: false
      },
      packets: {
        packetsPageSize: 100
      },
      files: {
        isAutoDownloadFile: true
      }
    }
  });
  const preferences = getCurrentPreferences(state);
  assert.deepEqual(preferences, response);
});

test('data is not empty', function(assert) {
  const state = {
    investigate: {
      eventTimeline: {
        data: new Date(),
        status: 'resolved'
      }
    }
  };
  const dataEmpty = isDataEmpty(state);

  assert.equal(dataEmpty, false);
});

test('data is empty and status is resolved', function(assert) {
  const state = {
    investigate: {
      eventTimeline: {
        data: null,
        status: 'resolved'
      }
    }
  };
  const dataEmpty = isDataEmpty(state);

  assert.equal(dataEmpty, true);
});


test('should show status', function(assert) {
  const state = {
    investigate: {
      eventTimeline: {
        data: 1,
        status: 'wait'
      }
    }
  };
  const showStatus = shouldShowStatus(state);

  assert.equal(showStatus, true);
});

test('no need to show status', function(assert) {
  const state = {
    investigate: {
      eventTimeline: {
        data: new Date(),
        status: 'resolved'
      }
    }
  };
  const showStatus = shouldShowStatus(state);

  assert.equal(showStatus, false);
});

test('Should get default column group as Summary', function(assert) {
  const state = {
    investigate: {
      data: {
        columnGroup: 'SUMMARY',
        columnGroups: EventColumnGroups
      }
    }
  };
  const selectedColumnGroup = getSelectedColumnGroup(state);
  assert.equal(selectedColumnGroup.name, 'Summary List');
  assert.equal(selectedColumnGroup.columns.length, 5);
  assert.deepEqual(EventColumnGroups[2], selectedColumnGroup);
});

test('Should fall back to Summary for wrong column group', function(assert) {
  const state = {
    investigate: {
      data: {
        columnGroup: 'XYZ',
        columnGroups: EventColumnGroups
      }
    }
  };
  const selectedColumnGroup = getSelectedColumnGroup(state);
  assert.equal(selectedColumnGroup.name, 'Summary List');
  assert.equal(selectedColumnGroup.columns.length, 5);

  assert.deepEqual(EventColumnGroups[2], selectedColumnGroup);
});

test('Should get selected column groups', function(assert) {
  const state = {
    investigate: {
      data: {
        columnGroup: 'WEB',
        columnGroups: EventColumnGroups
      }
    }
  };
  const selectedColumnGroup = getSelectedColumnGroup(state);
  assert.equal(selectedColumnGroup.name, 'Web Analysis');
  assert.equal(selectedColumnGroup.columns.length, 53);
  assert.deepEqual(EventColumnGroups[6], selectedColumnGroup);
});

test('Should get mutable columns for data table', function(assert) {
  const state = Immutable.from({
    investigate: {
      data: {
        columnGroup: 'SUMMARY',
        columnGroups: EventColumnGroups
      }
    }
  });
  const columns = getColumns(state);
  assert.equal(columns.length, 5);
  assert.notOk(columns.isMutable, 'Columns should not be a mutable object.');
});
