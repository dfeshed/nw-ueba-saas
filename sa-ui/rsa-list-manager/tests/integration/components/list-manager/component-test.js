import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import SELECTORS from './selectors';

let setState;

module('Integration | Component | list-manager', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  // selectors
  const {
    listManager,
    listManagerContainerNested,
    listManagerTrigger
  } = SELECTORS;

  const stateLocation1 = 'listManager';
  const listName1 = 'Some Things';

  const items = [
    { id: 3, name: 'eba', subItems: [ 'a', 'b', 'c' ] },
    { id: 1, name: 'foo', subItems: [ 'a', 'b' ] },
    { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] },
    { id: 4, name: 'Baz', subItems: [ 'c' ] }
  ];

  test('list manager is rendered', async function(assert) {
    new ReduxDataHelper(setState).build();
    this.set('stateLocation', stateLocation1);
    this.set('list', items);
    this.set('listName', listName1);

    await render(hbs`{{#list-manager
      stateLocation=stateLocation
      list=list
      listName=listName
    }}
    {{/list-manager}}`);

    assert.ok(find(listManager), 'list manager shall be found');
    assert.ok(find(listManagerContainerNested), 'list manager shall be found');
  });

  test('stateLocation exists', async function(assert) {
    new ReduxDataHelper(setState).build();
    this.set('stateLocation', stateLocation1);
    this.set('list', items);
    this.set('listName', listName1);

    await render(hbs`{{#list-manager
      stateLocation=stateLocation
      listName=listName
      list=list
    }}
    {{/list-manager}}`);

    assert.equal(this.get('stateLocation'), stateLocation1, 'stateLocation exists and has correct value in list-manager component');
  });

  test('list manager when disabled is rendered', async function(assert) {
    new ReduxDataHelper(setState).build();
    this.set('stateLocation', stateLocation1);
    this.set('list', []);
    this.set('listName', listName1);
    this.set('isDisabled', true);

    await render(hbs`{{#list-manager
      stateLocation=stateLocation
      list=list
      listName=listName
      isDisabled=isDisabled
    }}
    {{/list-manager}}`);

    assert.ok(find(listManager), 'list manager should be found');
    assert.notOk(find(listManagerContainerNested), 'list manager container should not be found');
    assert.ok(find(listManagerTrigger), 'list manager trigger should be found');
  });
});
