import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { profiles, enrichedProfiles } from 'investigate-events/reducers/investigate/profile/selectors';
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
const aliases1 = {
  'udp.srcport': {
    '7': 'echo',
    '9': 'discard',
    '13': 'daytime',
    '17': 'qotd'
  }
};
const language1 = [{
  format: 'Text',
  metaName: 'access.point',
  flags: 2147484691,
  displayName: 'Access Point',
  formattedName: 'access.point (Access Point)'
}];
const state1 = {
  investigate: {
    dictionaries: {
      aliases: aliases1,
      language: language1
    },
    profile: {
      profiles: profiles1
    }
  }
};

test('profiles selects profiles', function(assert) {
  assert.deepEqual(
    profiles(
      Immutable.from(state1)
    ), profiles1, 'profiles selects profiles');
});

test('enrichedProfiles returns profiles with isEditable property', function(assert) {
  assert.expect(profiles1.length);
  const result = enrichedProfiles(Immutable.from(state1));
  result.forEach((item) => {
    assert.ok(item.hasOwnProperty('isEditable'), 'each profile shall have isEditable property');
  });
});

test('enrichedProfiles returns profiles with isEditable property set correctly', function(assert) {
  const result = enrichedProfiles(Immutable.from(state1));
  result.forEach((item) => {
    assert.ok(item.hasOwnProperty('isEditable'), 'each profile shall have isEditable property');
  });

  assert.ok(result[result.length - 1].isEditable, 'isEditable shall be true if not OOTB');
  assert.notOk(result[result.length - 2].isEditable, 'isEditable shall be false if OOTB');
  assert.notOk(result[0].isEditable, 'isEditable shall be false if OOTB');
});

test('enrichedProfiles returns profiles with preQueryPillsData property', function(assert) {
  assert.expect(profiles1.length);
  const result = enrichedProfiles(Immutable.from(state1));
  result.forEach((item) => {
    assert.ok(item.hasOwnProperty('preQueryPillsData'), 'each profile shall have preQueryPillsData property');
  });
});
