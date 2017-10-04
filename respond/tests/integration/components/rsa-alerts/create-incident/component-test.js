import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import engineResolverFor from '../../../../helpers/engine-resolver';
import * as incidentsCreators from 'respond/actions/creators/incidents-creators';
import wait from 'ember-test-helpers/wait';

let redux, dispatchSpy;

moduleForComponent('rsa-alerts/create-incident', 'Integration | Component | Respond Alerts Create Incident', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('redux');
    redux = this.get('redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('The component appears in the DOM', function(assert) {
  this.render(hbs`{{rsa-alerts/create-incident}}`);
  assert.equal(this.$('.rsa-create-incident').length, 1, 'The component appears in the DOM');
});

test('Apply button is disabled when there is no name', function(assert) {
  this.render(hbs`{{rsa-alerts/create-incident}}`);
  assert.equal(this.$('.apply.is-disabled').length, 1, 'The APPLY button is disabled when there is no incidentName');
});

test('Apply button is enabled when there is an incident name', function(assert) {
  this.render(hbs`{{rsa-alerts/create-incident incidentName="Suspected C&C"}}`);
  assert.equal(this.$('.apply:not(.is-disabled)').length, 1, 'The APPLY button is not disabled when there is an incidentName');
});

test('Apply button is disabled when there is an incident name but creation is in progress', function(assert) {
  this.render(hbs`{{rsa-alerts/create-incident incidentName="Suspected C&C" isCreationInProgress=true}}`);
  assert.equal(this.$('.apply.is-disabled').length, 1,
    'The APPLY button is disabled when there is an incidentName but isCreationInProgress is true');
});

test('Clicking Apply will execute the create incident action creator', function(assert) {
  const actionSpy = sinon.spy(incidentsCreators, 'createIncidentFromAlerts');
  this.render(hbs`{{rsa-alerts/create-incident incidentName="Suspected C&C"}}`);
  this.$('.apply .rsa-form-button').click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The createIncidentsFromAlerts action was called once');
    assert.ok(dispatchSpy.calledOnce, 'Once dispatch of an action was made');
  });
});