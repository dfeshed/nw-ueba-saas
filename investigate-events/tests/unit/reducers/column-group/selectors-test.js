import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { hasColumnGroups, columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import EventColumnGroups from '../../../data/subscriptions/column-group';
import { DEFAULT_LANGUAGES } from '../../../helpers/redux-data-helper';

const listManagers = {
  columnGroups: {
    list: EventColumnGroups
  }
};

module('Unit | Selectors | column-group', function(hooks) {
  setupTest(hooks);

  test('should set columnGroups from listManager if available', function(assert) {
    assert.expect(EventColumnGroups.length);
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
    for (let i = 0; i < result.length; i++) {
      assert.equal(result[i].id, listManagers.columnGroups.list[i].id);
    }
  });

  test('should use original columnGroups if listManager\'s list uavailable', function(assert) {
    assert.expect(EventColumnGroups.length);
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
    for (let i = 0; i < result.length; i++) {
      assert.equal(result[i].id, EventColumnGroups[i].id);
    }
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

  test('Should mark columnsGroups in profiles as undeletable', function(assert) {
    const state = Immutable.from({
      listManagers,
      investigate: {
        columnGroup: {
          columnGroups: [EventColumnGroups]
        },
        profile: {
          profiles: [
            {
              id: 'FOO',
              name: 'FOO Profile',
              columnGroup: { // Matches up with columnGroup defined for EventColumnGroups[0]
                id: 'EMAIL1',
                name: 'Custom 1'
              }
            }
          ]
        }
      }
    });
    const result = columnGroups(state);
    assert.notOk(result[0].isDeletable, 'IS NOT deletable because it is in a profile');
    assert.ok(result[1].isDeletable, 'IS deletable because it is not in a profile');
  });
});