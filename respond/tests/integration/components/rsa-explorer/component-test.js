import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, findAll, find, render } from '@ember/test-helpers';
import engineResolverFor from '../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import waitForReduxStateChange from '../../../helpers/redux-async-helpers';
import remediationTaskCreators from 'respond/actions/creators/remediation-task-creators';
import { patchReducer } from '../../../helpers/vnext-patch';
import sinon from 'sinon';
import Immutable from 'seamless-immutable';

let setState, redux;

module('Integration | Component | Explorer', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    setState = (state = {}) => {
      const fullState = { respond: { remediationTasks: state } };
      patchReducer(this, Immutable.from(fullState));
      redux = this.owner.lookup('service:redux');
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The Explorer component renders to the DOM', async function(assert) {
    this.set('columns', []);
    this.set('creators', remediationTaskCreators);
    await render(hbs`{{rsa-explorer columns=columns reduxSpace='respond.remediationTasks' creators=creators}}`);
    assert.equal(findAll('.rsa-respond-explorer').length, 1, 'The Explorer component should be found in the DOM');
  });

  test('The yielded toolbar component renders to the DOM with block content', async function(assert) {
    this.set('columns', []);
    this.set('creators', remediationTaskCreators);
    await render(hbs`
      {{#rsa-explorer columns=columns reduxSpace='respond.remediationTasks' creators=creators as |explorer|}}
        {{#explorer.toolbar}}
          <div class="block-content"></div>
        {{/explorer.toolbar}}
      {{/rsa-explorer}}`);
    assert.equal(findAll('.rsa-respond-explorer .rsa-explorer-toolbar .more-filters-button').length, 1, 'The Explorer toolbar component renders');
    assert.equal(findAll('.rsa-respond-explorer .rsa-explorer-toolbar .block-content').length, 1, 'The toolbar block content renders');
  });

  test('The yielded filters component renders to the DOM with block content', async function(assert) {
    this.set('columns', []);
    this.set('creators', remediationTaskCreators);
    await render(hbs`
      {{#rsa-explorer columns=columns reduxSpace='respond.remediationTasks' creators=creators as |explorer|}}
        {{#explorer.filters}}
          <div class="block-content"></div>
        {{/explorer.filters}}
      {{/rsa-explorer}}`);
    assert.equal(findAll('.rsa-respond-explorer .explorer-filters .body').length, 1, 'The Explorer filters component renders');
    assert.equal(findAll('.rsa-respond-explorer .explorer-filters footer .rsa-form-button').length, 1, 'The reset button renders');
    assert.equal(findAll('.rsa-respond-explorer .explorer-filters .body .block-content').length, 1, 'The filters block content renders');
  });

  test('The yielded table component renders to the DOM with block content', async function(assert) {
    this.set('columns', [{
      field: 'name',
      title: 'respond.remediationTasks.list.name'
    }]);
    this.set('creators', remediationTaskCreators);
    await render(hbs`
      {{#rsa-explorer columns=columns reduxSpace='respond.remediationTasks' creators=creators as |explorer|}}
        {{#explorer.table}}

        {{/explorer.table}}
      {{/rsa-explorer}}`);
    assert.equal(findAll('.rsa-respond-explorer .rsa-explorer-table .rsa-data-table').length, 1, 'The Explorer table component renders');
    assert.equal(find('.rsa-respond-explorer .rsa-explorer-table .rsa-data-table .rsa-data-table-header-cell').textContent.trim(), 'Name', 'Explorer table column renders');
  });

  test('The yielded inspector component renders to the DOM with block content', async function(assert) {
    this.set('columns', [{
      field: 'name',
      title: 'respond.remediationTasks.list.name'
    }]);
    this.set('creators', remediationTaskCreators);
    await render(hbs`
      {{#rsa-explorer columns=columns reduxSpace='respond.remediationTasks' creators=creators as |explorer|}}
        {{#explorer.inspector as |inspector| }}
          {{#inspector.inspectorContent}}
            <div class="block-content"></div>
          {{/inspector.inspectorContent}}
        {{/explorer.inspector}}
      {{/rsa-explorer}}`);

    assert.equal(findAll('.rsa-respond-explorer .rsa-explorer-inspector').length, 1, 'The Explorer inspector component renders');
    assert.equal(findAll('.rsa-respond-explorer .rsa-explorer-inspector .block-content').length, 1, 'Explorer inspector block content renders');
  });

  test('The select all header checkbox adds all items updates isSelectAll in app state', async function(assert) {
    assert.expect(4);
    setState({ items: [{}, {}, {}] });
    const actionSpy = sinon.spy(remediationTaskCreators, 'toggleSelectAll');
    this.set('columns', [{
      title: 'respond.remediationTasks.list.select',
      class: 'rsa-form-row-checkbox',
      width: '3%',
      field: 'selectItem',
      dataType: 'checkbox'
    }]);
    this.set('creators', remediationTaskCreators);
    await render(hbs`
    {{#rsa-explorer columns=columns reduxSpace='respond.remediationTasks' creators=creators as |explorer|}}
      {{#explorer.table}}
        {{#if (eq column.dataType 'checkbox') }}
          <label class="rsa-form-checkbox-label {{if (contains item.id explorer.instance.itemsSelected) 'checked'}}">
            {{rsa-form-checkbox checked=(contains item.id explorer.instance.itemsSelected) change=(action explorer.select item)}}
          </label>
          {{/if}}
      {{/explorer.table}}
    {{/rsa-explorer}}`);
    const selectAllCheckboxSelector = '.rsa-respond-explorer .rsa-explorer-table .rsa-data-table .rsa-data-table-header-cell input[type=checkbox]';

    // Header column should have a checkbox for select all
    assert.equal(findAll(selectAllCheckboxSelector).length, 1, 'Select all column has a checkbox');

    // Check the checkbox and expect the isSelectAll state to be true and itemsSelected to have six items in the array
    const selectAll = waitForReduxStateChange(redux, 'respond.remediationTasks.isSelectAll');
    await click(selectAllCheckboxSelector);
    await selectAll;
    const { remediationTasks } = redux.getState().respond;
    assert.ok(actionSpy.calledOnce);
    assert.ok(remediationTasks.isSelectAll);
    assert.equal(remediationTasks.itemsSelected.length, 3, 'The itemsSelected app state property has a length of 6');
    actionSpy.restore();
  });

  test('Clicking on a table header cell title invokes the sortBy', async function(assert) {
    const actionSpy = sinon.spy(remediationTaskCreators, 'sortBy');
    this.set('columns', [{
      field: 'name',
      title: 'respond.remediationTasks.list.name'
    }]);
    this.set('creators', remediationTaskCreators);
    await render(hbs`
      {{#rsa-explorer columns=columns reduxSpace='respond.remediationTasks' creators=creators  as |explorer|}}
        {{#explorer.table}}

        {{/explorer.table}}
      {{/rsa-explorer}}`);
    assert.equal(findAll('.rsa-respond-explorer .rsa-explorer-table .rsa-data-table').length, 1, 'The Explorer table component renders');
    assert.equal(find('.rsa-respond-explorer .rsa-explorer-table .rsa-data-table .rsa-data-table-header-cell').textContent.trim(), 'Name', 'Explorer table column renders');
    await click('.rsa-respond-explorer .rsa-explorer-table .rsa-data-table .rsa-data-table-header-cell .header-title');
    assert.ok(actionSpy.calledOnce);
    actionSpy.restore();
  });
});
