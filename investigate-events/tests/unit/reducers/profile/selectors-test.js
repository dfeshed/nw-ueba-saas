import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { profiles, profilesWithIsEditable } from 'investigate-events/reducers/investigate/profile/selectors';
import { DEFAULT_PROFILES } from '../../../helpers/redux-data-helper';

module('Unit | Selectors | profile');

const profiles1 = [
  ...DEFAULT_PROFILES,
  {
    name: 'New Web Analysis',
    metaGroup: {
      name: 'RSA Web Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Web Analysis'
    },
    preQuery: 'service=80,8080,443',
    contentType: 'CUSTOM'
  }
];

test('profiles selects profiles', function(assert) {
  assert.deepEqual(
    profiles(
      Immutable.from({
        investigate: {
          profile: {
            profiles: DEFAULT_PROFILES
          }
        }
      })
    ), DEFAULT_PROFILES, 'profiles selects profiles');
});

test('profilesWithIsEditable returns profiles with isEditable property', function(assert) {
  assert.expect(DEFAULT_PROFILES.length);
  const result = profilesWithIsEditable(
    Immutable.from({
      investigate: {
        profile: {
          profiles: DEFAULT_PROFILES
        }
      }
    })
  );
  result.forEach((item) => {
    assert.ok(item.hasOwnProperty('isEditable'), 'each profile shall have isEditable property');
  });
});

test('profilesWithIsEditable returns profiles with isEditable property', function(assert) {
  assert.expect(DEFAULT_PROFILES.length);
  const result = profilesWithIsEditable(
    Immutable.from({
      investigate: {
        profile: {
          profiles: DEFAULT_PROFILES
        }
      }
    })
  );
  result.forEach((item) => {
    assert.ok(item.hasOwnProperty('isEditable'), 'each profile shall have isEditable property');
  });
});

test('profilesWithIsEditable returns profiles with isEditable property set correctly', function(assert) {
  const result = profilesWithIsEditable(
    Immutable.from({
      investigate: {
        profile: {
          profiles: profiles1
        }
      }
    })
  );
  result.forEach((item) => {
    assert.ok(item.hasOwnProperty('isEditable'), 'each profile shall have isEditable property');
  });

  assert.ok(result[result.length - 1].isEditable, 'isEditable shall be true if not OOTB');
  assert.notOk(result[result.length - 2].isEditable, 'isEditable shall be false if OOTB');
  assert.notOk(result[0].isEditable, 'isEditable shall be false if OOTB');
});
