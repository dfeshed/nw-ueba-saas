import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { render, findAll } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

const callback = () => {};
const wormhole = 'wormhole-context-menu';


module('Integration | Component | host detail header titlebar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, state);
    };

    // Right click setup
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);

    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
  });

  test('Search does not render for Downloads', async function(assert) {
    new ReduxDataHelper(setState)
      .hostName('XYZ')
      .selectedTabComponent('DOWNLOADS')
      .build();
    await render(hbs`{{host-detail/header/titlebar}}`);
    assert.equal(findAll('.titlebar .host-explore').length, 0, 'should not render the Search');
  });

  test('Search does render for non download tabs', async function(assert) {
    new ReduxDataHelper(setState)
      .hostName('XYZ')
      .selectedTabComponent('AUTORUNS')
      .build();
    await render(hbs`{{host-detail/header/titlebar}}`);
    assert.equal(findAll('.titlebar .host-explore').length, 1, 'should render the Search');
  });
});
