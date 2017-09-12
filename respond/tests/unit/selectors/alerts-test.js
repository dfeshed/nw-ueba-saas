import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getAlerts, getSelectedAlerts, hasSelectedAlertsBelongingToIncidents, getAlertNames } from 'respond/selectors/alerts';
import data from '../../data/data';

const { storyline: items } = data;

module('Unit | Mixin | Alerts Selectors');

test('getAlerts() selector returns the expect value from state', function(assert) {
  const state = {
    respond: {
      alerts: {
        items
      }
    }
  };
  const result = getAlerts(state);
  assert.equal(result, items, 'The returned value from the selector is as expected');
});

test('getSelectedAlerts() selector returns the expect value from state', function(assert) {
  const itemsSelected = ['5833fedca7c89226086a0912', '5833fedca7c89226086a0911', '5833fee2a7c89226086a0956'];
  const state = {
    respond: {
      alerts: {
        itemsSelected
      }
    }
  };
  const result = getSelectedAlerts(state);
  assert.equal(result, itemsSelected, 'The returned value from the selector is as expected');
});

test('hasSelectedAlertsBelongingToIncidents() is false when none of the selected alerts have partOfIncident === true', function(assert) {

  const state = {
    respond: {
      alerts: {
        items,
        itemsSelected: ['5833fedca7c89226086a0912', '5833fedca7c89226086a0911', '5833fee2a7c89226086a0956']
      }
    }
  };

  const result = hasSelectedAlertsBelongingToIncidents(state);
  assert.equal(result, false, 'None of the selected alerts are part of an incident');
});

test('hasSelectedAlertsBelongingToIncidents() is true when at least of the selected alerts has partOfIncident === true', function(assert) {
  const alertIdForAlertAssociatedWithIncident = '586ecf95ecd25950034e1312';

  const state = {
    respond: {
      alerts: {
        items,
        itemsSelected: ['5833fedca7c89226086a0912', '5833fedca7c89226086a0911', alertIdForAlertAssociatedWithIncident]
      }
    }
  };

  const result = hasSelectedAlertsBelongingToIncidents(state);
  assert.equal(result, true, 'At least one of the selected alerts is part of an incident');
});

test('getAlertNames() returns sorted array', function(assert) {
  const state = {
    respond: {
      dictionaries: {
        alertNames: ['Whale', 'beetle', 'Aardvark', '12Monkeys', 'human']
      }
    }
  };
  const result = getAlertNames(Immutable.from(state));
  assert.equal(result.join(','), '12Monkeys,Aardvark,beetle,human,Whale', 'The alert names are returned sorted by name');
});