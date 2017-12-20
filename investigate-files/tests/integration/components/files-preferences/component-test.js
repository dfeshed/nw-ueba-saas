import { moduleForComponent, test } from 'ember-qunit';
import wait from 'ember-test-helpers/wait';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  schema
} from '../../../data/data';

let setState;

moduleForComponent('files-preferences', 'Integration | Component | files preferences', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
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
    .schema(schema.fields)
    .visibleColumns([])
    .build();
  this.render(hbs`{{files-preferences}}{{preferences-panel}}`);
  this.$('.rsa-icon-settings-1-filled').trigger('click');
  return wait().then(() => {
    assert.equal(this.$('header.preference-panel-js').text().trim(), 'Files Preferences', 'Make sure header title is present');
    assert.equal(this.$('.rsa-preferences-panel-trigger i').attr('title'), 'Open/Hide Files Preferences', 'Tooltip is correct');
  });
});
