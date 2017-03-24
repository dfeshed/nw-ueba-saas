import $ from 'jquery';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import * as UIStateActions from 'respond/actions/ui-state-creators';
import sinon from 'sinon';

// NOOP function to replace Ember.K
const NOOP = () => {};

let dispatchSpy;

moduleForComponent('rsa-respond-incidents/incidents-toolbar', 'Integration | Component | Respond Incidents Toolbar', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component:rsa-respond-incidents/incidents-toolbar', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    const redux = this.get('redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('The Incidents toolbar renders to the DOM', function(assert) {
  assert.expect(1);
  this.render(hbs`{{rsa-respond-incidents/incidents-toolbar}}`);
  assert.equal(this.$('.rsa-respond-incidents-toolbar').length, 1, 'The Incidents toolbar should be found in the DOM');
});

test('The incident action update buttons appear but only when isInSelectMode is true', function(assert) {
  this.set('isInSelectMode', false);

  this.on('toggleIsInSelectMode', NOOP);
  this.on('toggleFilterPanel', NOOP);

  this.render(hbs`
    {{rsa-respond-incidents/incidents-toolbar
      isMoreFiltersActive=(readonly isFilterPanelOpen)
      isInSelectMode=(readonly isInSelectMode)
      toggleIsInSelectMode=(action "toggleIsInSelectMode")
      toggleFilterPanel=(action "toggleFilterPanel")
    }}`);

  assert.equal($('.incident-action-button').length, 0, 'There are three incident actions buttons in the DOM');
  this.set('isInSelectMode', true);
  assert.equal($('.incident-action-button').length, 3, 'There are three incident actions buttons in the DOM');
});

test('The Deselect All button appears if incidents are selected', function(assert) {
  this.set('isInSelectMode', true);
  this.set('incidentsSelected', []);
  this.on('toggleIsInSelectMode', NOOP);
  this.on('toggleFilterPanel', NOOP);

  this.render(hbs`
    {{rsa-respond-incidents/incidents-toolbar
      isMoreFiltersActive=(readonly isFilterPanelOpen)
      isInSelectMode=(readonly isInSelectMode)
      incidentsSelected=(readonly incidentsSelected)
      toggleIsInSelectMode=(action "toggleIsInSelectMode")
      toggleFilterPanel=(action "toggleFilterPanel")
    }}`);

  assert.equal($('.deselect-all .rsa-form-button').length, 0, 'There is no deselect all button in the toolbar');
  this.set('incidentsSelected', ['INC-123']);
  assert.equal($('.deselect-all .rsa-form-button').length, 1, 'There is one deselect all button in the toolbar');

  const actionSpy = sinon.spy(UIStateActions, 'clearSelectedIncidents');
  this.$('.deselect-all .rsa-form-button').click();
  assert.ok(dispatchSpy.callCount);
  assert.ok(actionSpy.calledOnce);
  actionSpy.restore();
});