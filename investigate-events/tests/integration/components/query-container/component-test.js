import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { render, find, findAll, click, settled } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import config from 'ember-get-config';

const nextGenFeature = config.featureFlags.nextGen;

let setState;

module('Integration | Component | query-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it disables the submit button when required values are missing', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(false)
      .build();
    await render(hbs`{{query-container}}`);
    assert.ok(find('.execute-query-button').classList.contains('is-disabled'), 'Expected is-disabled CSS class on the submit button.');
  });

  test('it enables the submit button when required values are present', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    await render(hbs`{{query-container}}`);
    assert.notOk(find('.execute-query-button').classList.contains('is-disabled'), 'Expected is-disabled CSS class on the submit button.');
  });

  test('it displays the correct number of query bar links', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    await render(hbs`{{query-container}}`);
    if (nextGenFeature) {
      assert.equal(findAll('.query-bar-select-actions a').length, 3, 'Expected 3 query bars');
    } else {
      assert.equal(findAll('.query-bar-select-actions a').length, 2, 'Expected 2 query bars');
    }
    assert.ok(find('.rsa-investigate-query-container.guided'), 'Expected to see Guided Query Bar');
  });

  test('it displays free form query bar when clicked', async function(assert) {
    new ReduxDataHelper(setState)
      .hasRequiredValuesToQuery(true)
      .build();
    await render(hbs`{{query-container}}`);
    assert.equal(find('.rsa-query-meta .rsa-query-fragment.edit-active input').placeholder, 'Enter individual statements consisting of a Meta Key, Operator, and Value (optional)', 'Expected a placeholder');
    assert.ok(find('.rsa-investigate-query-container.guided'), 'Expected to see Guided Query Bar');
    assert.notOk(find('.rsa-query-meta .rsa-query-fragment input:focus'), 'Should not have focus the first time it renders');

    await click('.query-bar-select-actions .freeForm-link');
    return settled().then(() => {
      assert.ok(find('.rsa-investigate-query-container.freeForm'), 'Expected to see Free Form Query Bar');
      assert.ok(find('.rsa-investigate-free-form-query-bar input:focus'), 'Expected focus on free-form');
      assert.equal(find('.rsa-investigate-free-form-query-bar input').placeholder, 'Enter multiple complex statements consisting of a Meta Key, Operator, and Value (optional)', 'Expected a placeholder');

      click('.query-bar-select-actions .guided-link');
      return settled().then(() => {
        assert.ok(find('.rsa-investigate-query-container.guided'), 'Expected to see Guided Query Bar');
        assert.ok(find('.rsa-query-meta .rsa-query-fragment input:focus'), 'Expected focus on guided');
      });
    });

  });

  test('it displays nextGen query bar when clicked', async function(assert) {
    if (nextGenFeature) {
      new ReduxDataHelper(setState)
        .hasRequiredValuesToQuery(true)
        .build();
      await render(hbs`{{query-container}}`);
      assert.equal(find('.rsa-query-meta .rsa-query-fragment.edit-active input').placeholder, 'Enter individual statements consisting of a Meta Key, Operator, and Value (optional)', 'Expected a placeholder');
      assert.ok(find('.rsa-investigate-query-container.guided'), 'Expected to see Guided Query Bar');

      await click('.query-bar-select-actions .nextGen-link');
      return settled().then(() => {
        assert.ok(find('.rsa-investigate-query-container.nextGen'), 'Expected to see NextGen Query Bar');
        assert.equal(findAll('.query-pills').length, 1, 'Expected to see Query Pills component');
      });
    } else {
      assert.ok(true);
    }
  });
});