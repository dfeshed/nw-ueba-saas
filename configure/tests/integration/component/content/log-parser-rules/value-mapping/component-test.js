import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | value mapping', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('value mapping exists', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0).build();
    await render(hbs`{{content/log-parser-rules/value-mapping}}`);
    assert.ok(find('.value-mapping'), 'value mapping is not showing');
  });

  test('Show mapping metaKey value for rule ipv4', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0).build();
    await render(hbs`{{content/log-parser-rules/value-mapping}}`);
    assert.equal(this.$('.value-mapping .firstItem .metaKey').text().trim(), 'ipv4', 'Mapping metaKey value is not showing or not ipv4');
  });

  test('Change mapping metaKey value to rule ipv6', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1).build();
    await render(hbs`{{content/log-parser-rules/value-mapping}}`);
    assert.equal(this.$('.value-mapping .firstItem .metaKey').text().trim(), 'ipv6', 'Mapping metaKey value is not showing or not ipv6');
  });
});