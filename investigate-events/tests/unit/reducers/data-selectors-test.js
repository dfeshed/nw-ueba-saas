import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  getCurrentPreferences,
  isDataEmpty,
  shouldShowStatus,
  getSelectedColumnGroup,
  getColumns,
  hasColumnGroups,
  getFlattenedColumnList
} from 'investigate-events/reducers/investigate/data-selectors';
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
        defaultMetaFormat: 'TEXT',
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
  assert.deepEqual(EventColumnGroups[9], selectedColumnGroup);
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

test('columns should not include meta-details column', function(assert) {
  assert.expect(5);
  const state = Immutable.from({
    investigate: {
      data: {
        columnGroup: 'SUMMARY2',
        columnGroups: EventColumnGroups
      }
    }
  });
  const columns = getColumns(state);

  assert.equal(columns.length, 4);
  columns.forEach((col) => {
    assert.ok(col.field !== 'custom.meta-details', 'Should not be a meta-details column in list');
  });
});

test('flattened list should include fields inside meta-summary and fields always required', function(assert) {
  const state = Immutable.from({
    investigate: {
      data: {
        columnGroup: 'SUMMARY',
        columnGroups: EventColumnGroups
      }
    }
  });
  const columns = getFlattenedColumnList(state);

  assert.ok(columns.includes('medium'), 'must always include medium');
  assert.ok(columns.includes('nwe.callback_id'), 'must always include callback id');
  assert.ok(columns.includes('sessionid'), 'must always include sessionid');
  assert.ok(columns.includes('ip.dst'), 'fields from inside meta-summary are flattened into array');
});

test('flattened list of columns do not include summary fields if no meta-summary column', function(assert) {
  const state = Immutable.from({
    investigate: {
      data: {
        columnGroup: 'SUMMARY2',
        columnGroups: EventColumnGroups
      }
    }
  });
  const columns = getFlattenedColumnList(state);
  assert.notOk(columns.includes('ip.dst'), 'fields from inside meta-summary should not be present');
});

test('flattened list of columns do not include dupe columns if exist in list and in', function(assert) {
  const state = Immutable.from({
    investigate: {
      data: {
        columnGroup: 'SUMMARY3',
        columnGroups: EventColumnGroups
      }
    }
  });
  const columns = getFlattenedColumnList(state);
  const ipDstColumns = columns.filter((col) => col === 'ip.dst');
  assert.ok(ipDstColumns.length === 1, 'summary fields not double included');
});

test('flattened list should include fields inside metasummary and fields always required', function(assert) {
  const state = Immutable.from({
    investigate: {
      data: {
        columnGroup: 'SUMMARY4',
        columnGroups: EventColumnGroups
      }
    }
  });
  const columns = getFlattenedColumnList(state);

  assert.ok(columns.includes('medium'), 'must always include medium');
  assert.ok(columns.includes('nwe.callback_id'), 'must always include callback id');
  assert.ok(columns.includes('sessionid'), 'must always include sessionid');
  assert.ok(columns.includes('ip.dst'), 'fields from inside meta-summary are flattened into array');
});


test('Should set hasColumnGroups', function(assert) {
  assert.ok(
    hasColumnGroups(
      Immutable.from({
        investigate: {
          data: {
            columnGroup: 'SUMMARY',
            columnGroups: EventColumnGroups
          }
        }
      })
    ), 'hasColumnGroups should be true'
  );

  assert.notOk(
    hasColumnGroups(
      Immutable.from({
        investigate: {
          data: {}
        }
      })
    ), 'hasColumnGroups should be false'
  );
});
