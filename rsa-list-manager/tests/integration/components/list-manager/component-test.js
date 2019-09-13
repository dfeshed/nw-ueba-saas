import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | list-manager', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const listManagerSelector = '.list-manager';
  const listLocation1 = 'listManager';

  const items = [
    { id: 3, name: 'eba', subItems: [ 'a', 'b', 'c' ] },
    { id: 1, name: 'foo', subItems: [ 'a', 'b' ] },
    { id: 2, name: 'bar', subItems: [ 'e', 'b', 'c' ] },
    { id: 4, name: 'Baz', subItems: [ 'c' ] }
  ];

  test('list manager is rendered', async function(assert) {
    new ReduxDataHelper(setState).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);

    await render(hbs`{{#list-manager
      listLocation=listLocation
      list=list
    }}
    {{/list-manager}}`);

    assert.ok(find(listManagerSelector), 'list manager shall be found');
  });

  test('listLocation exists', async function(assert) {
    new ReduxDataHelper(setState).build();
    this.set('listLocation', listLocation1);
    this.set('list', items);

    await render(hbs`{{#list-manager
      listLocation=listLocation
      list=list
    }}
    {{/list-manager}}`);

    assert.equal(this.get('listLocation'), listLocation1, 'listLocation exists and has correct value in list-manager component');
  });
});
