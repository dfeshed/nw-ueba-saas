import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';

let initState;

moduleForComponent('host-scan/scan-command', 'Integration | Component | Host Scan Command', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    initState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders the scan start button', function(assert) {
  this.set('command', 'START_SCAN');
  this.render(hbs`{{host-scan/scan-command command=command}}`);
  assert.equal(this.$('.host-start-scan-button').length, 1, 'scan start button rendered');
});


test('it should render the proper title for start scan', function(assert) {
  this.set('command', 'START_SCAN');
  new ReduxDataHelper(initState)
    .scanCount(3)
    .build();
  this.render(hbs`{{host-scan/scan-command command=command}}`);
  this.$('.host-start-scan-button .rsa-form-button').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .scan-modal:visible').length, 1, 'Expected to render start scan modal');
    assert.equal($('#modalDestination .rsa-application-modal-content h3').text().trim(), 'Start Scan for 3 host(s)');
  });
});

test('it should render the proper title for stop scan', function(assert) {
  this.set('command', 'STOP_SCAN');
  new ReduxDataHelper(initState)
    .scanCount(3)
    .build();
  this.render(hbs`{{host-scan/scan-command command=command}}`);
  this.$('.stop-scan-button .rsa-form-button').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .stop-scan-modal:visible').length, 1, 'Expected to render start scan modal');
    assert.equal($('#modalDestination .rsa-application-modal-content h3').text().trim(), 'Stop Scan for 3 host(s)');
  });
});
