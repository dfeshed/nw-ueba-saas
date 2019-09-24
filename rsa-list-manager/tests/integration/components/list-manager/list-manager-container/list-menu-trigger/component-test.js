import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import Component from '@ember/component';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | list-menu-trigger', function(hooks) {
  setupRenderingTest(hooks);
  this.actions = {};
  this.send = (actionName, ...args) => this.actions[actionName].apply(this, args);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  const buttonGroupSelector = '.rsa-button-group';
  const splitDropdown = '.rsa-split-dropdown';
  const listCaption = '.list-caption';
  const stateLocation1 = 'listManager';
  const listName1 = 'My Items';

  const items = [
    { id: 3, name: 'eba', subItems: [ 'a', 'b', 'c' ] },
    { id: 1, name: 'foo', subItems: [ 'a', 'b' ] },
    { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] },
    { id: 4, name: 'Baz', subItems: [ 'c' ] }
  ];

  test('The list-menu-trigger component renders to the DOM', async function(assert) {
    new ReduxDataHelper(setState).list(items).listName(listName1).build();
    this.set('stateLocation', stateLocation1);

    await render(hbs`{{#list-manager/list-manager-container/list-menu-trigger
      stateLocation=stateLocation
    }}
    {{/list-manager/list-manager-container/list-menu-trigger}}`);

    assert.ok(find(buttonGroupSelector), 'Shall have rsa-button-group class');
    assert.ok(find(splitDropdown), 'Shall have button with rsa-split-dropdown class');
    assert.ok(find(listCaption), 'Shall have list caption with list-caption class');
    assert.equal(find(listCaption).textContent.trim(), listName1, 'Shall have correct list caption');
  });

  test('Clicking list caption opens the list and triggers listOpened', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState).list(items).listName(listName1).build();

    const FakeComponent = Component.extend({
      layout: hbs`
        {{list-manager/list-manager-container/list-menu-trigger
            stateLocation=stateLocation
            listOpened=(action 'listOpened')
          }}
        `,
      actions: {
        listOpened() {
          assert.ok(true, 'listOpened shall be triggered when list is opened');
        }
      }
    });
    this.owner.register('component:test-list-menu-trigger', FakeComponent);
    this.set('stateLocation', stateLocation1);
    await render(hbs`{{test-list-menu-trigger stateLocation=stateLocation}}`);

    assert.ok(find(listCaption), 'Shall have list caption with list-caption class');
    const state1 = this.owner.lookup('service:redux').getState();
    assert.notOk(state1.listManager.isExpanded, 'isExpanded is initially false');

    await click(`${listCaption} button`);

    const state2 = this.owner.lookup('service:redux').getState();
    assert.ok(state2.listManager.isExpanded, 'clicking list caption sets isExpanded to true');
  });

  test('Clicking split dropdown opens the list and triggers listOpened', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState).list(items).listName(listName1).build();

    const FakeComponent = Component.extend({
      layout: hbs`
        {{list-manager/list-manager-container/list-menu-trigger
            stateLocation=stateLocation
            listOpened=(action 'listOpened')
          }}
        `,
      actions: {
        listOpened() {
          assert.ok(true, 'listOpened shall be triggered when list is opened');
        }
      }
    });
    this.owner.register('component:test-list-menu-trigger', FakeComponent);
    this.set('stateLocation', stateLocation1);
    await render(hbs`{{test-list-menu-trigger stateLocation=stateLocation}}`);

    const state1 = this.owner.lookup('service:redux').getState();
    assert.notOk(state1.listManager.isExpanded, 'isExpanded is initially false');

    await click(`${splitDropdown} button`);

    const state2 = this.owner.lookup('service:redux').getState();
    assert.ok(state2.listManager.isExpanded, 'clicking split dropdown sets isExpanded to true');
  });
});
