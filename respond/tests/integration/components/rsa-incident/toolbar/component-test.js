import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { toggleTasksAndJournalPanel } from 'respond/actions/creators/incidents-creators';

let redux;

module('Integration | Component | Incident Toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    redux = this.owner.lookup('service:redux');
    // make sure sidebar is closed
    if (redux.getState().respond.incident.isShowingTasksAndJournal === true) {
      redux.dispatch(toggleTasksAndJournalPanel());
    }
  });

  test('it renders', async function(assert) {
    await render(hbs`{{rsa-incident/toolbar}}`);
    assert.ok(find('.rsa-incident-toolbar'), 'Toolbar is rendered');
    assert.ok(find('.js-test-journal'), 'Journal & Tasks button is rendered');
    assert.ok(find('.viz'), 'Nodal Graph button is rendered');
    assert.ok(find('.datasheet'), 'Events List button is rendered');
  });

  test('clicking Journal button triggers toggle action', async function(assert) {
    await render(hbs`{{rsa-incident/toolbar}}`);
    await click(find('.js-test-journal .rsa-form-button'));
    assert.notOk(find('.js-test-journal'), 'the journal button is now hidden');
  });
});
