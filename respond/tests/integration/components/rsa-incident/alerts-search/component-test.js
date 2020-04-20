import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import { findAll, find, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState, i18n;

const exampleIncidentState = {
  id: 'INC-123',
  info: {
    status: 'NEW'
  },
  defaultSearchTimeFrameName: 'LAST_TWENTY_FOUR_HOURS',
  defaultSearchEntityType: 'IP',
  searchEntity: { id: '192.168.1.167', type: 'IP' },
  searchTimeFrameName: 'ALL_TIME',
  searchStatus: 'complete',
  searchResults: [
    {
      id: '5ad8d4e763a7953bb0cf2f6a',
      incidentId: 'INC-123',
      partOfIncident: true,
      timestamp: 1524159719000,
      alert: {
        source: 'Event Stream Analysis',
        name: 'Possible Phishing',
        numEvents: 3,
        severity: 80
      }
    },
    {
      id: '5ad8e11a63a7953bb0cf2f6b',
      incidentId: null,
      partOfIncident: false,
      timestamp: 1524159719000,
      alert: {
        source: 'Reporting Engine',
        name: 'User attempted login 100 times in one hour',
        numEvents: 1,
        severity: 90
      }
    },
    {
      id: '5ad8e11a63a7953bb0cf2f6c',
      incidentId: 'INC-321',
      partOfIncident: true,
      timestamp: 1524159719000,
      alert: {
        source: 'Endpoint',
        name: 'Malware detected',
        numEvents: 1,
        severity: 90
      }
    }
  ],
  addRelatedIndicatorsStatus: null
};

const selectors = {
  alerts: '.rsa-alerts-search-results__body .alert',
  firstAlert: '.rsa-data-table-body-row:nth-of-type(1)',
  secondAlert: '.rsa-data-table-body-row:nth-of-type(2)',
  thirdAlert: '.rsa-data-table-body-row:nth-of-type(3)'
};

module('Integration | Component | Related Alerts Search', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { respond: { incident: state } };
      patchReducer(this, Immutable.from(fullState));
      i18n = this.owner.lookup('service:i18n');
    };
  });

  test('it renders the alert search results', async function(assert) {
    setState(exampleIncidentState);
    await render(hbs`{{rsa-incident/alerts-search useLazyRendering=false}}`);
    assert.equal(findAll(selectors.alerts).length, 3, 'There are three search results');
    assert.equal(find(`${selectors.firstAlert} .severity`).textContent.trim(), 80, 'The severity value appears in the result');
    assert.equal(find(`${selectors.firstAlert} .alert-name`).textContent.trim(), 'Possible Phishing', 'The alert name appears in the result');
    assert.equal(find(`${selectors.firstAlert} .event-count`).textContent.trim(), '3 events', 'The event count appears in the result');
    assert.equal(findAll(`${selectors.firstAlert} .part-of-this-incident .rsa-form-button-wrapper.is-disabled`).length, 1,
      'A disabled button appears since this alert is already part of this incident');
  });

  test('it displays an add to incident button when the alert is not part of an incident', async function(assert) {
    setState(exampleIncidentState);
    await render(hbs`{{rsa-incident/alerts-search useLazyRendering=false}}`);
    assert.equal(findAll(`${selectors.secondAlert} .not-part-of-incident .rsa-form-button-wrapper:not(.is-disabled)`).length, 1,
      'An enabled button appears since this alert is not part of any incident');
  });

  test('it displays an hyperlink when the alert is already part of another incident', async function(assert) {
    setState(exampleIncidentState);
    await render(hbs`{{rsa-incident/alerts-search useLazyRendering=false}}`);
    assert.equal(find(`${selectors.thirdAlert} .part-of-other-incident a`).textContent.trim(), 'INC-321',
      'When the alert is part of another incident it appears as a hyperlink with the incident id as text');
  });

  test('it disables the add-to-incident button when the incident is closed', async function(assert) {
    setState({
      ...exampleIncidentState,
      info: {
        status: 'CLOSED'
      }
    });
    await render(hbs`{{rsa-incident/alerts-search useLazyRendering=false}}`);
    assert.equal(findAll(`${selectors.secondAlert} .not-part-of-incident .rsa-form-button-wrapper.is-disabled`).length, 1,
      'A disabled button appears since this incident is closed');
    assert.equal(find(`${selectors.secondAlert} .not-part-of-incident .disabled-control-message`).textContent.trim(),
      i18n.t('respond.incident.search.actions.closedIncident'),
      'A message is displayed explaining why the button is disabled');
  });
});

