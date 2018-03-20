import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const SERVICES_UNAVAILABLE = 'Services Unavailable';
const SERVICE_ERROR_MESSAGE = 'Unexpected error loading the list of Brokers, Concentrators, and other services to investigate. This may be due to a configuration or connectivity issue.';
const LOADING_DATA = 'Loading data...';
const LOADING_SERVICES = 'Loading Services';
const NO_DATA = 'The selected service does not have any data';
const LOADING_SUMMARY = 'Loading Summary';

let setState;

moduleForComponent('service-selector', 'Integration | Component | service-selector', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize(this);
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it indicates when retrieving service data', function(assert) {
  new ReduxDataHelper(setState)
    .isServicesLoading(true)
    .build();
  this.render(hbs`{{query-container/service-selector}}`);
  assert.equal(this.$('.rsa-loader').length, 1, 'expected loader');
  assert.equal(this.$('.js-test-service').text().trim(), LOADING_SERVICES, 'incorrect label');
  assert.equal(this.$('.rsa-form-button-wrapper.service-selector').attr('title'), LOADING_DATA, 'incorrect tooltip');
});

test('it indicates when there is an error loading services', function(assert) {
  new ReduxDataHelper(setState)
    .isServicesRetrieveError(true)
    .build();
  this.render(hbs`{{query-container/service-selector}}`);
  assert.equal(this.$('.disclaimer').length, 1, 'expected disclaimer CSS class');
  assert.equal(this.$('.js-test-service').text().trim(), SERVICES_UNAVAILABLE, 'incorrect label');
  assert.equal(this.$('.rsa-form-button-wrapper.service-selector').attr('title'), SERVICE_ERROR_MESSAGE, 'incorrect tooltip');
});

test('it indicates when there is no data for the service', function(assert) {
  const service = 'svs1';
  new ReduxDataHelper(setState)
    .hasSummaryData(false, service)
    .build();
  this.render(hbs`{{query-container/service-selector}}`);
  assert.equal(this.$('.disclaimer').length, 1, 'expected disclaimer CSS class');
  assert.equal(this.$('.js-test-service').text().trim(), service, 'incorrect label');
  assert.equal(this.$('.rsa-form-button-wrapper.service-selector').attr('title'), NO_DATA, 'incorrect tooltip');
});

test('it indicates when retrieving summary data', function(assert) {
  new ReduxDataHelper(setState)
    .isSummaryLoading(true)
    .build();
  this.render(hbs`{{query-container/service-selector}}`);
  assert.equal(this.$('.rsa-loader').length, 1, 'expected loader');
  assert.equal(this.$('.js-test-service').text().trim(), LOADING_SUMMARY, 'incorrect label');
  assert.equal(this.$('.rsa-form-button-wrapper.service-selector').attr('title'), LOADING_DATA, 'incorrect tooltip');
});

test('it indicates when there is an error loading a service\'s summary', function(assert) {
  const service = 'svs3';
  const message = 'foo bar';
  const errorMessage = `some.class.path:${message}`;
  new ReduxDataHelper(setState)
    .isSummaryDataInvalid(true, service, errorMessage)
    .build();
  this.render(hbs`{{query-container/service-selector}}`);
  assert.equal(this.$('.disclaimer').length, 1, 'expected disclaimer CSS class');
  assert.equal(this.$('.js-test-service').text().trim(), service, 'incorrect label');
  assert.equal(this.$('.rsa-form-button-wrapper.service-selector').attr('title'), message, 'incorrect tooltip');
});