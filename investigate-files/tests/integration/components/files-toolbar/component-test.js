import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;
moduleForComponent('files-toolbar', 'Integration | Component | Files toolbar', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('Investigate files toolbar', function(assert) {
  new ReduxDataHelper(setState).totalItems(3).build();
  this.render(hbs`{{files-toolbar}}`);

  assert.equal(this.$('.title-header').length, 1, 'Files toolbar present');
  assert.equal(this.$('.title-header').text().trim(), 'Files (3)', 'Title with count present');
  assert.equal(this.$('.title-header .count').text().trim(), '(3)', 'File Count present');
  assert.equal(this.$('.export-button').length, 1, 'Export button present');
});


