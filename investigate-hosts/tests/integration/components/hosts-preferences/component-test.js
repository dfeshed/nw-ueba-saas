import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  hostSchema
} from '../../../data/data';

let setState;

moduleForComponent('hosts-preferences', 'Integration | Component | hosts preferences', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    initialize(this);
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
    this.registry.injection('component', 'i18n', 'service:i18n');
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders', function(assert) {
  new ReduxDataHelper(setState)
    .schema(hostSchema.fields)
    .visibleColumns([])
    .userProjectionChanged(false)
    .build();
  this.render(hbs`{{hosts-preferences}}{{preferences-panel}}`);
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return wait().then(() => {
    assert.equal(this.$('.rsa-preferences-panel-trigger').length, 1, 'panel trigger is present');
    assert.equal(this.$('header.preference-panel-js').text().trim(), 'Hosts Preferences', 'Make sure header title is present');
    assert.equal(this.$('.rsa-preferences-panel-trigger i').attr('title'), 'Open/Hide Hosts Preferences', 'Tooltip is correct');
    // assert.equal(this.$('div.rsa-preferences-field-content').length, 2, '2 preference fields are rendered');
  });
});
