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
  getIsAddToAlertsUnavailable
} from 'respond/selectors/alert-to-incident';

module('Unit | Mixin | Alerts to Incident Selectors');

const alertIncidentAssociation = {
  incidentSearchText: 'suspected',
  incidentSearchSortBy: 'created',
  incidentSearchSortIsDescending: false,
  incidentSearchStatus: 'streaming',
  incidentSearchResults: [{ id: 'INC-123' }, { id: 'INC-321' }],
  selectedIncident: { id: 'INC-1' },
  stopSearchStream: null,
  isAddAlertsInProgress: false
};

const state = {
  respond: {
    alertIncidentAssociation
  }
};

test('Basic selectors', function(assert) {
  assert.equal(getIncidentSearchStatus(state), 'streaming', 'The returned value from the getIncidentSearchStatus selector is as expected');
  assert.equal(getIncidentSearchResults(state), alertIncidentAssociation.incidentSearchResults, 'The returned value from the getIncidentSearchResults selector is as expected');
  assert.equal(getSelectedIncident(state), alertIncidentAssociation.selectedIncident, 'The returned value from the getSelectedIncident selector is as expected');
  assert.equal(getIncidentSearchSortBy(state), 'created', 'The returned value from the getIncidentSearchSortBy selector is as expected');

  assert.equal(getIncidentSearchSortIsDescending(state), false, 'The returned value from the getIncidentSearchSortIsDescending selector is as expected');
  assert.equal(getIncidentSearchText(state), 'suspected', 'The returned value from the getIncidentSearchText selector is as expected');
  assert.equal(hasSearchQuery(state), true, 'The returned value from the hasSearchQuery selector is as expected');
  assert.equal(getIsAddAlertsInProgress(state), false, 'The returned value from the getIsAddAlertsInProgress selector is as expected');
  assert.equal(getIsAddToAlertsUnavailable(state), false, 'The returned value from the getIsAddToAlertsUnavailable selector is as expected');
});

test('hasSearchQuery is false if the length of the search string is less than three characters', function(assert) {
  const state = {
    respond: {
      alertIncidentAssociation: {
        ...alertIncidentAssociation,
        incidentSearchText: 'su'
      }
    }
  };
  assert.equal(hasSearchQuery(state), false,
    'The return value from the hasSearchQuery selector is false when search text is less than three characters');
});

test('getIsAddToAlertsUnavailable is true if there is no selected incident', function(assert) {
  const state = {
    respond: {
      alertIncidentAssociation: {
        ...alertIncidentAssociation,
        selectedIncident: null
      }
    }
  };
  assert.equal(getIsAddToAlertsUnavailable(state), true,
    'The return value from the getIsAddToAlertsUnavailable selector is true when no selected incident');
});

test('getIsAddToAlertsUnavailable is true if isAddAlertsInProgress is true', function(assert) {
  const state = {
    respond: {
      alertIncidentAssociation: {
        ...alertIncidentAssociation,
        isAddAlertsInProgress: true
      }
    }
  };
  assert.equal(getIsAddToAlertsUnavailable(state), true,
    'The return value from the getIsAddToAlertsUnavailable selector is true when isAddAlertsInProgress is true');
});
