import { module, test } from 'qunit';
import {
  getIncidentSearchStatus,
  getIncidentSearchResults,
  getSelectedIncident,
  getIncidentSearchSortBy,
  getIncidentSearchSortIsDescending,
  getIncidentSearchText,
  hasSearchQuery,
  getIsAddAlertsInProgress,
  getIsIncidentNotSelected
} from 'respond-shared/selectors/add-to-incident/selectors';

module('Unit | Mixin | Alerts to Incident Selectors');

const incidentSearchParams = {
  incidentSearchText: 'suspected',
  incidentSearchSortBy: 'created',
  incidentSearchSortIsDescending: false,
  incidentSearchStatus: 'streaming',
  incidentSearchResults: [{ id: 'INC-123' }, { id: 'INC-321' }],
  selectedIncident: { id: 'INC-1' },
  stopSearchStream: null,
  isIncidentNotSelected: false,
  isAddToIncidentInProgress: false
};

const state = {
  respondShared: {
    incidentSearchParams
  }
};

test('Basic selectors', function(assert) {
  assert.equal(getIncidentSearchStatus(state), 'streaming', 'The returned value from the getIncidentSearchStatus selector is as expected');
  assert.equal(getIncidentSearchResults(state), incidentSearchParams.incidentSearchResults, 'The returned value from the getIncidentSearchResults selector is as expected');
  assert.equal(getSelectedIncident(state), incidentSearchParams.selectedIncident, 'The returned value from the getSelectedIncident selector is as expected');
  assert.equal(getIncidentSearchSortBy(state), 'created', 'The returned value from the getIncidentSearchSortBy selector is as expected');

  assert.equal(getIncidentSearchSortIsDescending(state), false, 'The returned value from the getIncidentSearchSortIsDescending selector is as expected');
  assert.equal(getIncidentSearchText(state), 'suspected', 'The returned value from the getIncidentSearchText selector is as expected');
  assert.equal(hasSearchQuery(state), true, 'The returned value from the hasSearchQuery selector is as expected');
  assert.equal(getIsAddAlertsInProgress(state), false, 'The returned value from the getIsAddAlertsInProgress selector is as expected');
  assert.equal(getIsIncidentNotSelected(state), false, 'The returned value from the getIsAddToAlertsUnavailable selector is as expected');
});

test('hasSearchQuery is false if the length of the search string is less than three characters', function(assert) {
  const state = {
    respondShared: {
      incidentSearchParams: {
        ...incidentSearchParams,
        incidentSearchText: 'su'
      }
    }
  };
  assert.equal(hasSearchQuery(state), false,
    'The return value from the hasSearchQuery selector is false when search text is less than three characters');
});

test('getIsAddToAlertsUnavailable is true if there is no selected incident', function(assert) {
  const state = {
    respondShared: {
      incidentSearchParams: {
        ...incidentSearchParams,
        selectedIncident: null
      }
    }
  };
  assert.equal(getIsIncidentNotSelected(state), true,
    'The return value from the getIsAddToAlertsUnavailable selector is true when no selected incident');
});

test('getIsAddToAlertsUnavailable is true if isAddToIncidentInProgress is true', function(assert) {
  const state = {
    respondShared: {
      incidentSearchParams: {
        ...incidentSearchParams,
        isAddToIncidentInProgress: true
      }
    }
  };
  assert.equal(getIsIncidentNotSelected(state), true,
    'The return value from the getIsAddToAlertsUnavailable selector is true when isAddToIncidentInProgress is true');
});