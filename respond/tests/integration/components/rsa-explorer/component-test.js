import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import waitForReduxStateChange from '../../../helpers/redux-async-helpers';
import remediationTaskCreators from 'respond/actions/creators/remediation-task-creators';
import sinon from 'sinon';

let dispatchSpy, redux;

moduleForComponent('rsa-explorer', 'Integration | Component | Respond Explorer', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    redux = this.get('redux');

    dispatchSpy = sinon.spy(redux, 'dispatch');
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

test('The Explorer component renders to the DOM', function(assert) {
  this.set('columns', []);
  this.render(hbs`{{rsa-explorer columns=columns namespace='remediation-tasks'}}`);
  assert.equal(this.$('.rsa-respond-explorer').length, 1, 'The Explorer component should be found in the DOM');
});

test('The Explorer initialize/getItems action is dispatched on init', function(assert) {
  assert.expect(2);
  const actionSpy = sinon.spy(remediationTaskCreators, 'getItems');
  this.set('columns', []);
  this.render(hbs`{{rsa-explorer columns=columns namespace='remediation-tasks'}}`);
  assert.ok(dispatchSpy.calledOnce);
  assert.ok(actionSpy.calledOnce);
  actionSpy.restore();
});

test('The yielded toolbar component renders to the DOM with block content', function(assert) {
  this.set('columns', []);
  this.render(hbs`
  {{#rsa-explorer columns=columns namespace='remediation-tasks' as |explorer|}}    
    {{#explorer.toolbar}}
      <div class="block-content"></div>
    {{/explorer.toolbar}}
  {{/rsa-explorer}}`);
  assert.equal(this.$('.rsa-respond-explorer .rsa-explorer-toolbar .more-filters-button').length, 1, 'The Explorer toolbar component renders');
  assert.equal(this.$('.rsa-respond-explorer .rsa-explorer-toolbar .block-content').length, 1, 'The toolbar block content renders');
});

test('The yielded toolbar component renders to the DOM with block content', function(assert) {
  this.set('columns', []);
  this.render(hbs`
  {{#rsa-explorer columns=columns namespace='remediation-tasks' as |explorer|}}        
    {{#explorer.filters}}
      <div class="block-content"></div>
    {{/explorer.filters}}
  {{/rsa-explorer}}`);
  assert.equal(this.$('.rsa-respond-explorer .explorer-filters .body').length, 1, 'The Explorer filters component renders');
  assert.equal(this.$('.rsa-respond-explorer .explorer-filters footer .rsa-form-button').length, 1, 'The reset button renders');
  assert.equal(this.$('.rsa-respond-explorer .explorer-filters .body .block-content').length, 1, 'The filters block content renders');
});

test('The yielded table component renders to the DOM with block content', function(assert) {
  this.set('columns', [{
    field: 'name',
    title: 'respond.remediationTasks.list.name'
  }]);
  this.render(hbs`
  {{#rsa-explorer columns=columns namespace='remediation-tasks' as |explorer|}}        
    {{#explorer.table}}
      
    {{/explorer.table}}
  {{/rsa-explorer}}`);
  assert.equal(this.$('.rsa-respond-explorer .rsa-explorer-table .rsa-data-table').length, 1, 'The Explorer table component renders');
  assert.equal(this.$('.rsa-respond-explorer .rsa-explorer-table .rsa-data-table .rsa-data-table-header-cell').text().trim(), 'Name', 'Explorer table column renders');
});

test('The yielded table component renders to the DOM with block content', function(assert) {
  this.set('columns', [{
    field: 'name',
    title: 'respond.remediationTasks.list.name'
  }]);
  this.render(hbs`
  {{#rsa-explorer columns=columns namespace='remediation-tasks' as |explorer|}}        
    {{#explorer.inspector as |section| }}
      {{#if (eq section 'inspector-body')}}
        <div class="block-content"></div>
      {{/if}}
    {{/explorer.inspector}}
  {{/rsa-explorer}}`);

  assert.equal(this.$('.rsa-respond-explorer .rsa-explorer-inspector').length, 1, 'The Explorer inspector component renders');
  assert.equal(this.$('.rsa-respond-explorer .rsa-explorer-inspector .block-content').length, 1, 'Explorer inspector block content renders');
});

test('The Explorer fetches items and stores into state property "items"', function(assert) {
  assert.expect(1);
  this.set('columns', []);

  this.render(hbs`{{rsa-explorer columns=columns namespace='remediation-tasks'}}`);
  const getItems = waitForReduxStateChange(redux, 'respond.remediationTasks.items');
  getItems.then((data) => {
    assert.equal(data.length, 6, 'The explorer has six items in state');
  });
});

test('The select all header checkobx adds all items updates isSelectAll in app state', function(assert) {
  const actionSpy = sinon.spy(remediationTaskCreators, 'toggleSelectAll');
  this.set('columns', [{
    title: 'respond.remediationTasks.list.select',
    class: 'rsa-form-row-checkbox',
    width: '3%',
    field: 'selectItem',
    dataType: 'checkbox'
  }]);
  this.render(hbs`
  {{#rsa-explorer columns=columns namespace='remediation-tasks' as |explorer|}}        
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
  assert.equal(this.$(selectAllCheckboxSelector).length, 1, 'Select all column has a checkbox');

  // Wait until the items are added
  const getItems = waitForReduxStateChange(redux, 'respond.remediationTasks.items');
  getItems.then(() => {
    // Check the checkbox and expect the isSelectAll state to be true and itemsSelected to have six items in the array
    const selectAll = waitForReduxStateChange(redux, 'respond.remediationTasks.isSelectAll');
    this.$(selectAllCheckboxSelector).click();
    selectAll.then(() => {
      const redux = this.get('redux');
      const { remediationTasks } = redux.getState().respond;
      assert.ok(actionSpy.calledOnce);
      assert.ok(remediationTasks.isSelectAll);
      assert.equal(remediationTasks.itemsSelected.length, 6, 'The itemsSelected app state property has a length of 6');
    });
  });
});