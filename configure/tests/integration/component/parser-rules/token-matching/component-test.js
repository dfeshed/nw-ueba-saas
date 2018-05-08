import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | token matching', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('token matching exists', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv4').build();
    await render(hbs`{{parser-rules/token-matching}}`);
    assert.ok(find('.token-matching'), 'token matching is not showing');
  });

  test('Show token matching value for rule ipv4', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv4').build();
    await render(hbs`{{parser-rules/token-matching}}`);
    assert.equal(this.$('.token-matching .firstItem td').text().trim(), 'ipv4=', 'Token matching value is not showing or not ipv4=');
  });

  test('Change token matching value to rule ipv6', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv6').build();
    await render(hbs`{{parser-rules/token-matching}}`);
    assert.equal(this.$('.token-matching .firstItem td').text().trim(), 'ipv6=', 'Token matching value is not showing or not ipv6=');
  });
});