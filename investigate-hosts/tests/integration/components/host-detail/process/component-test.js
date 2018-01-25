import { moduleForComponent, skip, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import processData from '../../../../integration/components/state/process-data';

let setState;

moduleForComponent('host-detail/process', 'Integration | Component | endpoint host detail/process', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders data when isProcessDataEmpty is true', function(assert) {
  setState(Immutable.from({
    endpoint: {
      process: {
        processTree: [],
        processList: []
      }
    }
  }));
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    {{host-detail/process}}    
  `);

  return wait().then(() => {
    assert.deepEqual(this.$('.process-content-box').length, 0, 'process content box is not present');
    assert.deepEqual(this.$('.process-property-box').length, 0, 'process property box is not present');
  });
});

test('it renders data when isProcessDataEmpty is false', function(assert) {
  setState(Immutable.from({
    endpoint: {
      process: processData
    }
  }));
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    {{host-detail/process}}
  `);

  return wait().then(() => {
    assert.deepEqual(this.$('.process-content-box').length, 1, 'process-content-box');
    assert.deepEqual(this.$('.process-property-box').length, 1, 'process-property-box');
  });
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{host-detail/process}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#host-detail/process}}
      template block text
    {{/host-detail/process}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});
