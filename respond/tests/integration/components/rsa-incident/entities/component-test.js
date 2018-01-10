import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import Immutable from 'seamless-immutable';
import wait from 'ember-test-helpers/wait';
import { scheduleOnce } from '@ember/runloop';
import { timerFlush } from 'd3-timer';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';

let setState;

moduleForComponent('rsa-incident-entities', 'Integration | Component | Incident Entities', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(Immutable.from(state));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('DidChange events will be runloop safe', function(assert) {
  setState({
    respond: {
      incident: {
        selection: {
          type: 'storyPoint',
          ids: [ 'alert1' ]
        }
      },
      storyline: {
        storyline: []
      }
    }
  });

  this.set('data', { nodes: [], links: [] });
  this.render(hbs`{{rsa-incident/entities data=data}}`);

  return wait().then(() => {
    let $el = this.$('.rsa-incident-entities');
    assert.equal($el.length, 1, 'Expected to find rsa-incident-entities element in DOM.');

    // will destory the component but not until _dataDidChange & _filterDidChange has fired
    scheduleOnce('render', this, function() {
      this.clearRender();
    });

    // will run just before the clearRender is invoked
    scheduleOnce('actions', this, function() {
      this.set('data', { nodes: [], links: [] });
      timerFlush();
    });

    return wait().then(() => {
      $el = this.$('.rsa-incident-entities');
      assert.equal($el.length, 0, 'Should not blow up because _dataDidChange was prevented from running while destroyed');
    });
  });
});
