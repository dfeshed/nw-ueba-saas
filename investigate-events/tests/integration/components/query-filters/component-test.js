import { module, test } from 'qunit';
import { set } from '@ember/object';
import { run } from '@ember/runloop';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { triggerKeyUp } from 'ember-keyboard';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../helpers/vnext-patch';
import { render, find, findAll, settled } from '@ember/test-helpers';

let setState;

module('Integration | Component | query-filters', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('it renders with no fragments by defauls', async function(assert) {
    new ReduxDataHelper(setState).setQueryFiltersMeta(false).build();

    await render(hbs`{{query-filters}}`);

    assert.equal(findAll('.rsa-query-meta').length, 1, 'Expected 1 .rsa-query-meta');
    assert.equal(findAll('.rsa-query-meta .rsa-query-fragment').length, 1, 'Expected 1 .rsa-query-fragment');
    assert.equal(findAll('.rsa-query-meta .rsa-query-fragment.edit-active').length, 1, 'Expected 1 .rsa-query-fragment.edit-active');
    assert.equal(find('.rsa-query-meta .rsa-query-fragment.edit-active input').placeholder, 'Enter a Meta Key, Operator, and Value (optional)', 'Expected a placeholder');
    assert.equal(findAll('.rsa-query-meta input:focus').length, 0, 'Expected .rsa-query-meta to not have focus');
  });

  test('focusing expands dropdown immediately', async function(assert) {
    new ReduxDataHelper(setState).setQueryFiltersMeta(false).build();

    await render(hbs`{{query-filters}}`);

    find('.rsa-query-meta input').focus();

    return settled().then(() => {
      assert.equal(findAll('.rsa-query-meta .ember-power-select-trigger[aria-expanded="true"]').length, 1, 'Expected .ember-power-select-trigger[aria-expanded=true]');
    });
  });

  test('it renders fragments', async function(assert) {
    new ReduxDataHelper(setState).setQueryFiltersMeta(true).build();

    await render(hbs`{{query-filters}}`);

    return settled().then(() => {
      assert.equal(findAll('.rsa-query-meta .rsa-query-fragment').length, 4, 'Expected 4 .rsa-query-fragment');
      assert.equal(findAll('.rsa-query-meta .rsa-query-fragment.edit-active').length, 1, 'Expected 1 .rsa-query-fragment.edit-active');
      assert.equal(find('.rsa-query-meta .rsa-query-fragment.edit-active input').placeholder, '', 'Expected no placeholder');
    });
  });

  test('it allows fragment editing via keyboard', async function(assert) {
    new ReduxDataHelper(setState).setQueryFiltersMeta(true).build();


    await render(hbs`{{query-filters}}`);

    const $queryBuilder = this.$('.rsa-query-meta');
    const $fragment = this.$('.rsa-query-fragment').first();

    $fragment.find('.meta').click();
    assert.ok($fragment[0].classList.contains('selected'));

    triggerKeyUp('Enter', $queryBuilder[0]);
    assert.ok($fragment[0].classList.contains('edit-active'));
  });

  test('The placeholder properly updates when locale is changed', async function(assert) {
    assert.expect(2);

    new ReduxDataHelper(setState).setQueryFiltersMeta(false).build();

    await render(hbs`{{query-filters}}`);

    const placeholder = 'メタ キー、演算子、値を入力（オプション）';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'queryBuilder.placeholder': placeholder });

    const selector = '.rsa-query-meta .rsa-query-fragment.edit-active input';
    assert.equal(find(selector).placeholder, 'Enter a Meta Key, Operator, and Value (optional)');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async () => {
      assert.equal(find(selector).placeholder, placeholder);
    });
  });
});
