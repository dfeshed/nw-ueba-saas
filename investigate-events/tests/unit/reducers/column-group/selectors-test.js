import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { hasColumnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import EventColumnGroups from '../../../data/subscriptions/investigate-columns/data';
import { DEFAULT_LANGUAGES } from '../../../helpers/redux-data-helper';

module('Unit | Selectors | column-group');

test('Should set hasColumnGroups', function(assert) {
  assert.ok(
    hasColumnGroups(
      Immutable.from({
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
