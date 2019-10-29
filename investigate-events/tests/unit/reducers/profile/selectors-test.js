import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import {
  isProfileExpanded,
  profiles,
  enrichedProfiles,
  enrichedProfile
} from 'investigate-events/reducers/investigate/profile/selectors';
import { DEFAULT_PROFILES } from '../../../helpers/redux-data-helper';
import EventColumnGroups from '../../../data/subscriptions/column-group';

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

const languageAndAliases1 = { language: language1, aliases: aliases1 };

const state1 = {
  investigate: {
    dictionaries: {
      aliases: aliases1,
      language: language1
    },
    columnGroup: {
      columnGroups: EventColumnGroups
    },
    profile: {
      profiles: profiles1
    }
  }
};

module('Unit | Selectors | profile', function(hooks) {
  setupTest(hooks);

  test('profiles selects profiles from investigate.profile if list has not been updated in listManagers', function(assert) {
    assert.deepEqual(
      profiles(
        Immutable.from(state1)
      ), profiles1, 'profiles selects profiles');
  });

  test('profiles selects profiles from listManagers.profiles if list has been updated in listManagers', function(assert) {
    const profiles2 = [ ...profiles1, { name: 'new profile' } ];
    const state2 = {
      ...state1,
      listManagers: {
        profiles: {
          list: profiles2
        }
      }
    };
    assert.deepEqual(
      profiles(Immutable.from(state2)), profiles2, 'profiles shall select updated profiles from listManagers.profile');
  });

  test('profiles returns undefined if both investigate.profile and listManagers.profiles have undefined list', function(assert) {
    const state2 = { ...state1,
      listManagers: {
        profiles: {
          list: undefined
        }
      }
    };
    state2.investigate.profile.profiles = undefined;
    assert.equal(
      profiles(
        Immutable.from(state2)
      ), undefined, 'profiles returns undefined if both investigate.profile and listManagers.profiles have undefined list');
  });

  test('enrichedProfile returns profile with isEditable and preQueryPillsData properties', function(assert) {
    const result = enrichedProfile(DEFAULT_PROFILES[0], languageAndAliases1);
    assert.ok(result.hasOwnProperty('isEditable'), 'profile shall have isEditable property');
    assert.ok(result.hasOwnProperty('preQueryPillsData'), 'profile shall have isEditable property');
  });

  test('enrichedProfile returns profile with SUMMARY column group if columnGroup is missing', function(assert) {
    const profile1 = { ...DEFAULT_PROFILES[0], columnGroup: undefined };
    const result = enrichedProfile(profile1, languageAndAliases1, EventColumnGroups);
    assert.ok(result.hasOwnProperty('columnGroup'), 'profile shall have columnGroup property');
    assert.equal(result.columnGroup.id, 'SUMMARY', 'profile shall have SUMMARY column group by default');
  });

  test('enrichedProfiles returns undefined if profiles is undefined', function(assert) {
    const state2 = { ...state1 };
    state2.investigate.profile.profiles = undefined;
    const result = enrichedProfiles(Immutable.from(state2));
    assert.notOk(result, 'shall return undefined if profiles is undefined');
  });

  test('enrichedProfiles returns profiles with isEditable property', function(assert) {
    const state2 = {
      investigate: {
        dictionaries: {
          aliases: aliases1,
          language: language1
        },
        columnGroup: {
          columnGroups: EventColumnGroups
        },
        profile: {
          profiles: profiles1
        }
      }
    };
    const result = enrichedProfiles(Immutable.from(state2));
    assert.ok(result, 'shall return enriched profiles');
    result?.forEach((item) => {
      assert.ok(item.hasOwnProperty('isEditable'), 'each profile shall have isEditable property');
    });
  });

  test('enrichedProfiles returns profiles with isEditable property set correctly', function(assert) {
    const state2 = {
      investigate: {
        dictionaries: {
          aliases: aliases1,
          language: language1
        },
        columnGroup: {
          columnGroups: EventColumnGroups
        },
        profile: {
          profiles: profiles1
        }
      }
    };
    const result = enrichedProfiles(Immutable.from(state2));
    assert.ok(result, 'shall return enriched profiles');
    result?.forEach((item) => {
      assert.ok(item.hasOwnProperty('isEditable'), 'each profile shall have isEditable property');
    });

    assert.ok(result[result.length - 1].isEditable, 'isEditable shall be true if not OOTB');
    assert.notOk(result[result.length - 2].isEditable, 'isEditable shall be false if OOTB');
    assert.notOk(result[0].isEditable, 'isEditable shall be false if OOTB');
  });

  test('enrichedProfiles returns profiles with preQueryPillsData property', function(assert) {
    const state2 = {
      investigate: {
        dictionaries: {
          aliases: aliases1,
          language: language1
        },
        columnGroup: {
          columnGroups: EventColumnGroups
        },
        profile: {
          profiles: profiles1
        }
      }
    };
    const result = enrichedProfiles(Immutable.from(state2));
    assert.ok(result, 'shall return enriched profiles');
    result?.forEach((item) => {
      assert.ok(item.hasOwnProperty('preQueryPillsData'), 'each profile shall have preQueryPillsData property');
    });
  });

  test('isProfileExpanded returns true when profile is expanded', function(assert) {
    let state = {
      listManagers: {
        profiles: {
          isExpanded: true
        }
      }
    };
    assert.ok(isProfileExpanded(state), 'Did not find it expanded');
    state = {
      listManagers: {
        profiles: {
          isExpanded: false
        }
      }
    };
    assert.notOk(isProfileExpanded(state), 'Found it expanded');
  });
});