import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';

let setState;
moduleForComponent('host-detail', 'Integration | Component | host detail', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
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

test('renders the host details header and details', function(assert) {

  this.render(hbs`{{host-detail}}`);

  assert.equal(this.$('.host-header').length, 1, 'header rendered');
  assert.equal(this.$('.host-detail-wrapper').length, 1, 'details rendered');

});

test('it shows loading indicator while fetching the data', function(assert) {
  new ReduxDataHelper(setState)
    .hostDetailsLoading(true)
    .isSnapshotsAvailable(true).selectedTabComponent('PROCESS').build();
  this.render(hbs`{{host-detail}}`);
  assert.equal(this.$('.rsa-loader').length, 1, 'loading indicator displayed');
});

test('it renders the selected tab component', function(assert) {
  new ReduxDataHelper(setState)
    .hostDetailsLoading(false)
    .isSnapshotsAvailable(true)
    .selectedTabComponent('PROCESS').build();
  this.render(hbs`{{host-detail}}`);
  assert.equal(this.$('.host-process-wrapper').length, 1, 'process information rendered');
});

test('it renders the no snapshot message', function(assert) {
  new ReduxDataHelper(setState)
    .hostDetailsLoading(false)
    .isSnapshotsAvailable(false)
    .selectedTabComponent('PROCESS')
    .build();
  this.render(hbs`{{host-detail}}`);
  assert.equal(this.$('.rsa-panel-message').length, 1, 'message panel is rendered');
  assert.equal(this.$('.message').text().trim(), 'No scan history were found.', 'empty snapshot message');
});

test('no snapshot message for OVERVIEW and SYSTEM tab', function(assert) {
  new ReduxDataHelper(setState)
    .hostDetailsLoading(false)
    .isSnapshotsAvailable(false)
    .selectedTabComponent('OVERVIEW')
    .build();
  this.render(hbs`{{host-detail}}`);
  assert.equal(this.$('.rsa-panel-message').length, 0, 'no message panel is rendered');
});
