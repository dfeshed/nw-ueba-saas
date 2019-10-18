import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { hasColumnGroups, columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import EventColumnGroups from '../../../data/subscriptions/column-group';
import { DEFAULT_LANGUAGES } from '../../../helpers/redux-data-helper';

module('Unit | Selectors | column-group');

const listManagers = {
  columnGroups: {
    list: EventColumnGroups
  }
};

test('should set columnGroups from listManager if available', function(assert) {
  const initialState = Immutable.from({
    listManagers,
    investigate: {
      data: {
        columnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups: [EventColumnGroups[0]]
      }
    }
  });
  const result = columnGroups(initialState);
  assert.deepEqual(result, listManagers.columnGroups.list);
});

test('should use original columnGroups if listManager\'s list uavailable', function(assert) {
  const initialState = Immutable.from({
    listManagers: {},
    investigate: {
      data: {
        columnGroup: 'SUMMARY'
      },
      columnGroup: {
        columnGroups: EventColumnGroups
      }
    }
  });
  const result = columnGroups(initialState);
  assert.deepEqual(result, EventColumnGroups);
});

test('Should set hasColumnGroups', function(assert) {
  assert.ok(
    hasColumnGroups(
      Immutable.from({
        listManagers,
        investigate: {
          data: {
            columnGroup: 'SUMMARY'
          },
          columnGroup: {
            columnGroups: EventColumnGroups
          }
        }
      })
    ), 'hasColumnGroups shall be true');

  assert.notOk(
    hasColumnGroups(
      Immutable.from({
        listManagers: {
          columnGroups: {}
        },
        investigate: {
          dictionaries: {
            language: DEFAULT_LANGUAGES
          },
          data: {},
          columnGroup: {}
        }
      })
    ), 'hasColumnGroups shall be false');
});
