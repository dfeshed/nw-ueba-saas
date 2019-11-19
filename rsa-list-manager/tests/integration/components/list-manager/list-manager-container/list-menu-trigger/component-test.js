import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { lookup } from 'ember-dependency-lookup';
import SELECTORS from '../../selectors';

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

  // selectors
  const {
    rsaButtonGroup,
    rsaSplitDropdown,
    listCaption,
    listCaptionIsDisabled,
    rsaSplitDropdownIsDisabled
  } = SELECTORS;

  // variables
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

    assert.ok(find(rsaButtonGroup), 'Shall have rsa-button-group class');
    assert.ok(find(rsaSplitDropdown), 'Shall have button with rsa-split-dropdown class');
    assert.ok(find(listCaption), 'Shall have list caption with list-caption class');
    assert.notOk(find(rsaSplitDropdownIsDisabled), 'Should not have button with rsa-split-dropdown class disabled');
    assert.notOk(find(listCaptionIsDisabled), 'Should not have list caption with list-caption class disabled');
    assert.equal(find(listCaption).textContent.trim(), listName1, 'Shall have correct list caption');
    assert.equal(find(listCaption).getAttribute('title'), null, 'When no value is selected the title is empty');
    assert.equal(find(rsaSplitDropdown).getAttribute('title'), null, 'When not disabled the title is empty');
  });

  test('The list-menu-trigger component when disabled renders to the DOM ', async function(assert) {
    const i18n = lookup('service:i18n');
    new ReduxDataHelper(setState).list(items).listName(listName1).build();
    this.set('stateLocation', stateLocation1);
    this.set('isDisabled', true);

    await render(hbs`{{#list-manager/list-manager-container/list-menu-trigger
      stateLocation=stateLocation
      isDisabled=isDisabled
    }}
    {{/list-manager/list-manager-container/list-menu-trigger}}`);

    assert.ok(find(rsaButtonGroup), 'Shall have rsa-button-group class');
    assert.ok(find(rsaSplitDropdown), 'Shall have button with rsa-split-dropdown class');
    assert.ok(find(listCaption), 'Shall have list caption with list-caption class');
    assert.ok(find(rsaSplitDropdownIsDisabled), 'Should have button with rsa-split-dropdown class disabled');
    assert.ok(find(listCaptionIsDisabled), 'Should have list caption with list-caption class disabled');
    assert.equal(find(listCaption).textContent.trim(), listName1, 'Shall have correct list caption');
    assert.equal(find(listCaptionIsDisabled).getAttribute('title'), i18n.t('rsaListManager.listMenuTrigger.disabled.permission') + listName1.toLowerCase(),
      'disabled list menu trigger should have correct title');
    assert.equal(find(rsaSplitDropdownIsDisabled).getAttribute('title'), i18n.t('rsaListManager.listMenuTrigger.disabled.permission') + listName1.toLowerCase(),
      'disabled list menu trigger should have correct title');
  });
});
