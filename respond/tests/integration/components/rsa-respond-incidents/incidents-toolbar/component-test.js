import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

const { $, K } = Ember;

moduleForComponent('rsa-respond-incidents/incidents-toolbar', 'Integration | Component | Respond Incidents Toolbar', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('The Incidents toolbar renders to the DOM', function(assert) {
  assert.expect(1);
  this.render(hbs`{{rsa-respond-incidents/incidents-toolbar}}`);
  assert.equal(this.$('.rsa-respond-incidents-toolbar').length, 1, 'The Incidents toolbar should be found in the DOM');
});

test('The incident action update buttons appear but only when isInSelectMode is true', function(assert) {
  this.set('isInSelectMode', false);
  this.on('toggleIsInSelectMode', K);
  this.on('toggleFilterPanel', K);

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