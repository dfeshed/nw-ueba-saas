import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  getCurrentPreferences,
  isDataEmpty,
  shouldShowStatus,
  getSelectedColumnGroup,
  getColumns,
  getFlattenedColumnList,
  hasMetaSummaryColumn,
  validEventSortColumns,
  isSummaryColumnVisible
} from 'investigate-events/reducers/investigate/data-selectors';
import EventColumnGroups from '../../data/subscriptions/column-group/findAll/data';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';

module('Unit | Selectors | data-selectors');

const mappedColumnGroups = mapColumnGroupsForEventTable(EventColumnGroups);

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
        selectedColumnGroup: 'EMAIL'
      },
      queryNode: {
        queryTimeFormat: 'DB'
      }
    },
    recon: {
      visuals: {
        currentReconView: 'TEXT',
        defaultLogFormat: 'TEXT',
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

test('isSummaryColumnVisible', function(assert) {
  assert.equal(isSummaryColumnVisible({
    investigate: {
      eventResults: {
        visibleColumns: [{
          field: 'custom.meta-summary'
        }]
      }
    }
  }), true);

  assert.equal(isSummaryColumnVisible({
    investigate: {
      eventResults: {
        visibleColumns: [{
          field: 'custom.metasummary'
        }]
      }
    }
  }), true);

  assert.equal(isSummaryColumnVisible({
    investigate: {
      eventResults: {
        visibleColumns: [{
          field: 'foo'
        }]
      }
    }
  }), false);
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

test('validEventSortColumns returns as expected when sortable', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [{ id: 'concentrator', displayName: 'concentrator', serviceName: 'concentrator', version: 11.4 }]
      },
      queryStats: {
        devices: [{
          serviceId: 'concentrator',
          on: true,
          elapsedTime: 2
        }]
      },
      dictionaries: {
        language: [{ format: 'Text', metaName: 'foo', flags: -2147482605 }]
      },
      eventCount: {
        data: 5,
        threshold: 5
      }
    }
  };
  const {
    columns,
    notIndexedAtValue,
    notSingleton,
    notValid
  } = validEventSortColumns(state);

  assert.equal(columns.length, 1);
  assert.equal(columns[0], 'foo');
  assert.equal(notIndexedAtValue.length, 0);
  assert.equal(notSingleton.length, 0);
  assert.equal(notValid.length, 0);
});

test('validEventSortColumns returns as expected when notIndexedAtValue', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [{ id: 'concentrator', displayName: 'concentrator', serviceName: 'concentrator', version: 11.4 }]
      },
      queryStats: {
        devices: [{
          serviceId: 'concentrator',
          on: true,
          elapsedTime: 2
        }]
      },
      dictionaries: {
        language: [{ format: 'Text', metaName: 'foo', flags: -2147483375 }]
      },
      eventCount: {
        data: 5,
        threshold: 5
      }
    }
  };
  const {
    columns,
    notIndexedAtValue,
    notSingleton,
    notValid
  } = validEventSortColumns(state);

  assert.equal(columns.length, 0);
  assert.equal(notIndexedAtValue.length, 1);
  assert.equal(notIndexedAtValue[0], 'foo');
  assert.equal(notSingleton.length, 0);
  assert.equal(notValid.length, 0);
});

test('validEventSortColumns returns as expected when notIndexedAtValue and notSingleton', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [{ id: 'concentrator', displayName: 'concentrator', serviceName: 'concentrator', version: 11.4 }]
      },
      queryStats: {
        devices: [{
          serviceId: 'concentrator',
          on: true,
          elapsedTime: 2
        }]
      },
      dictionaries: {
        language: [{ format: 'Text', metaName: 'foo', flags: -2147483391 }]
      },
      eventCount: {
        data: 5,
        threshold: 5
      }
    }
  };
  const {
    columns,
    notIndexedAtValue,
    notSingleton,
    notValid
  } = validEventSortColumns(state);

  assert.equal(columns.length, 0);
  assert.equal(notIndexedAtValue.length, 0);
  assert.equal(notSingleton.length, 0);
  assert.equal(notValid.length, 1);
  assert.equal(notValid[0], 'foo');
});

test('validEventSortColumns returns as expected with time', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [{ id: 'concentrator', displayName: 'concentrator', serviceName: 'concentrator', version: 11.4 }]
      },
      queryStats: {
        devices: [{
          serviceId: 'concentrator',
          on: true,
          elapsedTime: 2
        }]
      },
      dictionaries: {
        language: [{ format: 'Time', metaName: 'time', flags: -2147482605 }]
      },
      eventCount: {
        data: 5,
        threshold: 5
      }
    }
  };
  const {
    columns,
    notIndexedAtValue,
    notSingleton,
    notValid
  } = validEventSortColumns(state);

  assert.equal(columns.length, 1);
  assert.equal(columns[0], 'time');
  assert.equal(notIndexedAtValue.length, 0);
  assert.equal(notSingleton.length, 0);
  assert.equal(notValid.length, 0);
});

test('validEventSortColumns returns as expected when missing languages', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [{ id: 'concentrator', displayName: 'concentrator', serviceName: 'concentrator', version: 11.4 }]
      },
      queryStats: {
        devices: [{
          serviceId: 'concentrator',
          on: true,
          elapsedTime: 2
        }]
      },
      dictionaries: {
        language: undefined
      },
      eventCount: {
        data: 5,
        threshold: 5
      }
    }
  };
  const {
    columns,
    notIndexedAtValue,
    notSingleton,
    notValid
  } = validEventSortColumns(state);

  assert.equal(columns.length, 0);
  assert.equal(notIndexedAtValue.length, 0);
  assert.equal(notSingleton.length, 0);
  assert.equal(notValid.length, 0);
});

test('validEventSortColumns returns as expected when the column is multivalued', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [{ id: 'concentrator', displayName: 'concentrator', serviceName: 'concentrator', version: 11.4 }]
      },
      queryStats: {
        devices: [{
          serviceId: 'doesNotExist',
          on: true,
          elapsedTime: 2
        }]
      },
      dictionaries: {
        language: [{ format: 'TextT', metaName: 'foo', flags: -2147482621 }]
      },
      eventCount: {
        data: 4,
        threshold: 5
      }
    }
  };
  const {
    columns,
    notIndexedAtValue,
    notSingleton,
    notValid
  } = validEventSortColumns(state);

  assert.equal(columns.length, 0);
  assert.equal(notIndexedAtValue.length, 0);
  assert.equal(notSingleton.length, 1);
  assert.equal(notValid.length, 0);
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
        selectedColumnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
      }
    }
  };
  const selectedColumnGroup = getSelectedColumnGroup(state);
  assert.equal(selectedColumnGroup.name, 'Summary List');
  assert.equal(selectedColumnGroup.columns.length, 5);
  assert.deepEqual(mappedColumnGroups[2], selectedColumnGroup);
});

test('Should fall back to Summary for wrong column group', function(assert) {
  const state = {
    investigate: {
      dictionaries: {
        language: DEFAULT_LANGUAGES
      },
      data: {
        selectedColumnGroup: 'XYZ'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
      }
    }
  };
  const selectedColumnGroup = getSelectedColumnGroup(state);
  assert.equal(selectedColumnGroup.name, 'Summary List');
  assert.equal(selectedColumnGroup.columns.length, 5);

  assert.deepEqual(mappedColumnGroups[2], selectedColumnGroup);
});

test('Should get selected column groups', function(assert) {
  const state = {
    investigate: {
      dictionaries: {
        language: DEFAULT_LANGUAGES
      },
      data: {
        selectedColumnGroup: 'WEB'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
      }
    }
  };
  const selectedColumnGroup = getSelectedColumnGroup(state);
  assert.equal(selectedColumnGroup.name, 'Web Analysis');
  assert.equal(selectedColumnGroup.columns.length, 53);
  assert.deepEqual(mappedColumnGroups[9], selectedColumnGroup);
});

test('Should get mutable columns for data table', function(assert) {
  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: [
          { metaName: 'time' },
          { metaName: 'medium' },
          { metaName: 'custom.theme' },
          { metaName: 'size' },
          { metaName: 'nwe.callback_id' },
          { metaName: 'sessionid' },
          { metaName: 'ip.dst' },
          { metaName: 'custom.meta-summary' }
        ]
      },
      data: {
        selectedColumnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
      }
    }
  });
  const columns = getColumns(state);
  assert.equal(columns.length, 5);
  assert.notOk(columns.isMutable, 'Columns should not be a mutable object.');
});

test('Should include custom.meta-summary, custom.meta-summary, and custom.theme', function(assert) {
  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: []
      },
      data: {
        selectedColumnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups: [{
          id: 'SUMMARY',
          name: 'Summary List',
          ootb: true,
          columns: [
            { field: 'custom.theme', title: 'Type' },
            { field: 'custom.metasummary', title: 'Type' },
            { field: 'custom.meta-summary', title: 'Type' }
          ]
        }]
      }
    }
  });
  const columns = getColumns(state);
  assert.equal(columns.length, 3);
});

test('columns should exclude meta not included in language dictionary', function(assert) {
  assert.expect(2);
  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: [
          { metaName: 'time' }
        ]
      },
      data: {
        selectedColumnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups: [{
          id: 'SUMMARY',
          name: 'Summary List',
          ootb: true,
          columns: [
            { field: 'time', title: 'Collection Time', width: 175 },
            { field: 'medium', title: 'Type' }
          ]
        }]
      }
    }
  });
  const columns = getColumns(state);
  assert.equal(columns.length, 1);
  assert.equal(columns[0].field, 'time');
});

test('columns should not include meta-details column', function(assert) {
  assert.expect(5);
  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: [
          { metaName: 'time' },
          { metaName: 'medium' },
          { metaName: 'custom.theme' },
          { metaName: 'size' },
          { metaName: 'nwe.callback_id' },
          { metaName: 'sessionid' },
          { metaName: 'ip.dst' },
          { metaName: 'custom.meta-details' }
        ]
      },
      data: {
        selectedColumnGroup: 'SUMMARY2'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
      }
    }
  });
  const columns = getColumns(state);
  assert.equal(columns.length, 4);
  columns.forEach((col) => {
    assert.ok(![
      'custom.logdata',
      'custom.source',
      'custom.destination',
      'custom.meta-details'
    ].includes(col.field), 'Should not have a restricted column in list');
  });
});

test('flattened list should include fields inside meta-summary and fields always required', function(assert) {
  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: [
          { metaName: 'medium' },
          { metaName: 'nwe.callback_id' },
          { metaName: 'sessionid' },
          { metaName: 'ip.dst' },
          { metaName: 'custom.meta-summary' }
        ]
      },
      data: {
        selectedColumnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
      }
    }
  });
  const columns = getFlattenedColumnList(state);

  assert.ok(columns.includes('medium'), 'must always include medium');
  assert.ok(columns.includes('session.split'), 'must always include session.split');
  assert.ok(columns.includes('nwe.callback_id'), 'must always include callback id');
  assert.ok(columns.includes('sessionid'), 'must always include sessionid');
  assert.ok(columns.includes('ip.dst'), 'fields from inside meta-summary are flattened into array');
});

test('flattened list of columns do not include summary fields if no meta-summary column', function(assert) {
  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: DEFAULT_LANGUAGES
      },
      data: {
        selectedColumnGroup: 'SUMMARY2'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
      }
    }
  });
  const columns = getFlattenedColumnList(state);
  assert.notOk(columns.includes('ip.dst'), 'fields from inside meta-summary should not be present');
});

test('flattened list of columns do not include dupe columns if exist in list and in', function(assert) {
  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: [
          { metaName: 'medium' },
          { metaName: 'nwe.callback_id' },
          { metaName: 'sessionid' },
          { metaName: 'ip.dst' }
        ]
      },
      data: {
        selectedColumnGroup: 'SUMMARY3'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
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
      dictionaries: {
        language: [
          { metaName: 'medium' },
          { metaName: 'nwe.callback_id' },
          { metaName: 'sessionid' },
          { metaName: 'ip.dst' }
        ]
      },
      data: {
        selectedColumnGroup: 'SUMMARY4'
      },
      columnGroup: {
        columnGroups: mappedColumnGroups
      }
    }
  });
  const columns = getFlattenedColumnList(state);

  assert.ok(columns.includes('medium'), 'must always include medium');
  assert.ok(columns.includes('nwe.callback_id'), 'must always include callback id');
  assert.ok(columns.includes('sessionid'), 'must always include sessionid');
  assert.ok(columns.includes('ip.dst'), 'fields from inside meta-summary are flattened into array');
});

test('hasMetaSummaryColumn should return true if it has metasummary', function(assert) {
  const columnGroups = [{
    id: 'SUMMARY',
    name: 'Summary List',
    ootb: true,
    columns: [
      { field: 'custom.theme', title: 'Theme' },
      { field: 'size', title: 'Size' },
      { field: 'custom.meta-summary', title: 'Summary', width: null }
    ]
  }, {
    id: 'Email',
    name: 'Email Analysis',
    ootb: true,
    columns: [
      { field: 'some' },
      { field: 'foo' }
    ]
  }];

  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: [
          { count: 0, format: 'Text', metaName: 'custom.meta-summary', flags: 2, displayName: 'A', formattedName: 'a (A)' },
          { count: 0, format: 'Text', metaName: 'size', flags: 2, displayName: 'B', formattedName: 'b (B)' },
          { count: 0, format: 'Text', metaName: 'custom.theme', flags: 3, displayName: 'C', formattedName: 'c (C)' }
        ]
      },
      data: {
        selectedColumnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups
      }
    }
  });

  const found = hasMetaSummaryColumn(state);
  assert.ok(found, 'Found the meta column');
});

test('hasMetaSummaryColumn should return false if it does not have metasummary', function(assert) {
  const columnGroups = [{
    id: 'SUMMARY',
    name: 'Summary List',
    ootb: true,
    columns: [
      { field: 'custom.theme', title: 'Theme' },
      { field: 'size', title: 'Size' }
    ]
  }, {
    id: 'Email',
    name: 'Email Analysis',
    ootb: true,
    columns: [
      { field: 'some' },
      { field: 'foo' }
    ]
  }];

  const state = Immutable.from({
    investigate: {
      dictionaries: {
        language: DEFAULT_LANGUAGES
      },
      data: {
        selectedColumnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups
      }
    }
  });

  const found = hasMetaSummaryColumn(state);
  assert.notOk(found, 'Did not find the meta column');
});
