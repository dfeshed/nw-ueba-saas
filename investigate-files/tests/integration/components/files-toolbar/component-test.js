import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;
const item = [
  {
    checksumSha256: '365a393f3a34bf13f49306868b',
    id: '365'
  }
];
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
  new ReduxDataHelper(setState).totalItems(3).setSelectedFileList(item).build();
  this.render(hbs`{{files-toolbar}}`);
  assert.equal(this.$('.title-header').length, 1, 'Files toolbar present');
  assert.equal(this.$('.export-button').length, 1, 'Export button present');
});


