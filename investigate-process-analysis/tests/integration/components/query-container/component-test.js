import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ContextualHelp from 'component-lib/services/contextual-help';

module('Integration | Component | Query Container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders service selector and time selector and a button ', async function(assert) {
    await render(hbs`{{query-container}}`);
    assert.equal(findAll('.rsa-investigate-query-container__time-selector').length, 1, 'Expected to render time selector');
    assert.equal(findAll('.rsa-investigate-query-container__service-selector').length, 1, 'Expected to render service selector');
  });

  test('it renders the contextual help button', async function(assert) {
    await render(hbs`{{query-container}}`);
    this.owner.register('service:contextualHelp', ContextualHelp.extend({
      goToHelp(module, topic) {
        assert.equal(module, 'investigation');
        assert.equal(topic, 'invProcessAnalysis');
      }
    }));
    await click('.query-container .rsa-icon-help-circle-lined');
    return settled();
  });
});
