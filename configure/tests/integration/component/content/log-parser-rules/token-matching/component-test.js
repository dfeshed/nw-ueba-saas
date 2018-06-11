import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, settled, click, fillIn, triggerEvent } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

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
    new ReduxDataHelper(setState).parserRulesFormatData(0).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    assert.ok(find('.token-matching'), 'token matching is not showing');
  });

  test('Show token matching value for rule ipv4', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    assert.equal(this.$('.token-matching .firstItem input').val(), 'ipv4=', 'Token matching value is not showing or not ipv4=');
  });

  test('Change token matching value to rule ipv6', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    assert.equal(this.$('.token-matching .firstItem input').val(), 'ipv6=', 'Token matching value is not showing or not ipv6=');
  });

  test('Add a new rule token', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    fillIn('.token-matching .add-token input', '123');
    assert.ok(find('.token-matching .add-token button'), 'Add button is not showing');
    await click('.token-matching .add-token button');
    return settled().then(() => {
      assert.equal(this.$('.token-matching .firstItem input').val(), '123', 'Token 123 was not added');
    });
  });

  test('Delete a rule token', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    await click('.token-matching .firstItem button');
    return settled().then(() => {
      assert.equal(this.$('.token-matching .firstItem input').val(), 'second token', 'First token was not deleted');
    });
  });

  test('Edit a rule token', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    fillIn('.token-matching .firstItem input', '123');
    triggerEvent('.token-matching .firstItem input', 'focusOut');
    return settled().then(() => {
      assert.equal(this.$('.token-matching .firstItem input').val(), '123', 'firstItem was not edited');
    });
  });

  test('Add a rule token that exists', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    fillIn('.token-matching .add-token input', 'last');
    await click('.token-matching .add-token button');
    return settled().then(() => {
      assert.equal(this.$('.token-matching .firstItem input').val(), 'ipv6=', 'Token last was added');
    });
  });

  test('Add an empty rule token', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    fillIn('.token-matching .add-token input', '');
    await click('.token-matching .add-token button');
    return settled().then(() => {
      assert.equal(this.$('.token-matching .firstItem input').val(), 'ipv6=', 'Token empty was added');
    });
  });

});