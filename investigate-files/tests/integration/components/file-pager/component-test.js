import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('file-pager', 'Integration | Component | file pager', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders the total number of files and index', function(assert) {
  new DataHelper(this.get('redux')).initializeData();
  this.render(hbs`{{file-pager}}`);
  assert.equal(this.$('.file-info').text().length, 19, 'Expected to display total number of files and page number');
});
