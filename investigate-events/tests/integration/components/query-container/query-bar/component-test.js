import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, fillIn, find, findAll, render, settled } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import SELECTORS from '../selectors';
import PILL_SELECTORS from '../pill-selectors';
import { createBasicPill } from '../pill-util';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | query-bar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it displays the correct number of query bar links and starts on next gen mode', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();

    this.set('executeQuery', () => {});

    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);

    assert.equal(findAll(SELECTORS.queryFormatToggleLinks).length, 2, 'Expected 2 query bars');
    assert.equal(findAll(SELECTORS.nextGenQueryBar).length, 1, 'Expected to see Next Gen Query Bar');
  });

  test('Can toggle between views', async function(assert) {
    // const done = assert.async();
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      {{query-container/query-bar executeQuery=executeQuery}}
    `);
    await click(SELECTORS.queryFormatFreeFormToggle);

    return settled().then(async () => {
      assert.equal(findAll(SELECTORS.freeFormQueryBar).length, 1, 'Expected to see Free Form Query Bar');
      assert.equal(findAll(SELECTORS.freeFormQueryBarFocusedInput).length, 1, 'Expected focus on free-form');
      assert.equal(
        find(SELECTORS.freeFormQueryBarInput).placeholder,
        'Enter multiple complex statements consisting of a Meta Key, Operator, and Value (optional)',
        'Expected a placeholder');

      await click(SELECTORS.queryFormatNextGenToggle);
      return settled().then(() => {
        assert.ok(find(SELECTORS.nextGenQueryBar), 'Expected to see Next Gen Query Bar');
        assert.ok(find(SELECTORS.nextGenQueryBarFocusedInput), 'Expected focus on next gen');
      });
    });
  });

  test('Toggling from pills to free form copies pill text in', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    // Create pill
    await createBasicPill();

    // Click over to free form
    await click(SELECTORS.queryFormatFreeFormToggle);
    assert.equal(find(SELECTORS.freeFormQueryBarInput).value, 'a = \'x\'', 'pill is converted to free form');
  });

  test('Toggling from free form with pill-like input to pills will create non-complex pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .queryView('freeForm')
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    // Create pill
    await click(PILL_SELECTORS.freeFormInput);
    await fillIn(PILL_SELECTORS.freeFormInput, 'a = 1');
    await blur(PILL_SELECTORS.freeFormBarContainer);

    await click(SELECTORS.queryFormatNextGenToggle);

    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 2, 'The created pill and the template are two pills present');
    assert.equal(findAll(PILL_SELECTORS.complexPill).length, 0, 'that is not a complex pill');
    assert.equal(findAll(PILL_SELECTORS.queryPill)[0].textContent.replace(/\s/g, ''), 'a=1', 'pill text is correct');
  });

  test('Toggling from free form with complex input to pills will create complex pills', async function(assert) {
    new ReduxDataHelper(setState)
      .language()
      .pillsDataEmpty(true)
      .hasRequiredValuesToQuery(true)
      .queryView('freeForm')
      .build();
    this.set('executeQuery', () => {});

    await render(hbs`
      <div class='rsa-investigate-query-container'>
        {{query-container/query-bar executeQuery=executeQuery}}
      </div>
    `);

    // Create pill
    await click(PILL_SELECTORS.freeFormInput);
    await fillIn(PILL_SELECTORS.freeFormInput, '( adslkjalksdj && asdasdsad)');
    await blur(PILL_SELECTORS.freeFormBarContainer);

    await click(SELECTORS.queryFormatNextGenToggle);

    assert.equal(findAll(PILL_SELECTORS.queryPill).length, 1, 'The template is the only query pill present');
    assert.equal(findAll(PILL_SELECTORS.complexPill).length, 1, 'there is a complex pill');
    assert.equal(findAll(PILL_SELECTORS.complexPill)[0].textContent.replace(/\s/g, ''), '##(adslkjalksdj&&asdasdsad)##', 'pill text is correct');
  });

});