import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-groups/group-wizard/define-group-step', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group-wizard/define-group-step}}`);
    assert.equal(findAll('.define-group-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.andOrOperator').length, 1, 'andOrOperator control appears in the DOM');
    assert.equal(findAll('.remove-criteria').length, 1, 'Remove critrrie control appears in the DOM');
    assert.equal(findAll('.tooltip').length, 1, 'Tooltip appears in the DOM');
  });
  test('Pop tooltip', async function(assert) {
    new ReduxDataHelper(setState).groupWiz().build();
    await render(hbs`{{usm-groups/group-wizard/define-group-step}}`);
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(), 'Select one or more of the operating system types.', 'Tooltip pops with correct text');
  });
});
