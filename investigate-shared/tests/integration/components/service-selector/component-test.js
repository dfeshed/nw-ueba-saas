import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const SERVICES_UNAVAILABLE = 'Services Unavailable';
const SERVICE_ERROR_MESSAGE = 'Unexpected error loading the list of Brokers, Concentrators, and other services to investigate. This may be due to a configuration or connectivity issue.';
const LOADING_DATA = 'Loading data...';
const LOADING_SERVICES = 'Loading Services';
const NO_DATA = 'The selected service does not have any data';
const LOADING_SUMMARY = 'Loading Summary';


module('Integration | Component | Service Selector', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it indicates when retrieving service data', async function(assert) {
    const services = {
      isServicesLoading: true
    };
    this.set('services', services);

    await render(hbs`{{service-selector services=services}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'expected loader');
    assert.equal(find('.js-test-service').textContent.trim(), LOADING_SERVICES, 'incorrect label');
    assert.equal(find('.rsa-form-button-wrapper.service-selector').title, LOADING_DATA, 'incorrect tooltip');
  });

  test('it indicates when there is an error loading services', async function(assert) {
    const services = {
      isServicesRetrieveError: true
    };
    this.set('services', services);
    await render(hbs`{{service-selector services=services}}`);
    assert.equal(findAll('.disclaimer').length, 1, 'expected disclaimer CSS class');
    assert.equal(find('.js-test-service').textContent.trim(), SERVICES_UNAVAILABLE, 'incorrect label');
    assert.equal(find('.rsa-form-button-wrapper.service-selector').title, SERVICE_ERROR_MESSAGE, 'incorrect tooltip');
  });

  test('it indicates when there is no data for the service', async function(assert) {
    const selectedService = 'svs1';
    const services = {
      serviceData: [{ id: selectedService, displayName: selectedService, name: selectedService, version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isServicesRetrieveError: false
    };
    this.set('serviceId', selectedService);
    this.set('services', services);
    await render(hbs`{{service-selector serviceId=serviceId services=services}}`);
    assert.equal(findAll('.disclaimer').length, 1, 'expected disclaimer CSS class');
    assert.equal(find('.js-test-service').textContent.trim(), selectedService, 'incorrect label');
    assert.equal(find('.rsa-form-button-wrapper.service-selector').title, NO_DATA, 'incorrect tooltip');
  });

  test('it indicates when retrieving summary data', async function(assert) {
    const services = {
      isSummaryLoading: true
    };
    this.set('services', services);
    await render(hbs`{{service-selector services=services}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'expected loader');
    assert.equal(find('.js-test-service').textContent.trim(), LOADING_SUMMARY, 'incorrect label');
    assert.equal(find('.rsa-form-button-wrapper.service-selector').title, LOADING_DATA, 'incorrect tooltip');
  });

  test('it indicates when there is an error loading a service\'s summary', async function(assert) {
    const service = 'svs3';
    const message = 'foo bar';
    const services = {
      serviceData: [{ id: service, displayName: service, name: service, version: '11.1.0.0' }],
      summaryData: { startTime: 1 },
      isSummaryRetrieveError: true,
      summaryErrorMessage: message
    };
    this.set('services', services);
    this.set('serviceId', service);
    await render(hbs`{{service-selector services=services}}`);
    assert.equal(findAll('.disclaimer').length, 1, 'expected disclaimer CSS class');
    assert.equal(find('.js-test-service').textContent.trim(), service, 'incorrect label');
    assert.equal(find('.rsa-form-button-wrapper.service-selector').title, message, 'incorrect tooltip');
  });

});
