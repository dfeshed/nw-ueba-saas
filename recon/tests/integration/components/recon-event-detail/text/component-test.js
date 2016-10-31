import { moduleForComponent, test } from 'ember-qunit';
// import hbs from 'htmlbars-inline-precompile';
//
// import DataActions from 'recon/actions/data-creators';
// import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

moduleForComponent('recon-event-detail-text', 'Integration | Component | recon event detail text', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('text view renders packet text', function(assert) {
  assert.expect(0);
  // TODO: fix this test to wait on meta to load
  // const done = assert.async();
  // this.get('redux').dispatch(DataActions.setNewReconView(RECON_VIEW_TYPES_BY_NAME.TEXT));
  //
  // this.render(hbs`{{recon-event-detail/text}}`);
  //
  // setTimeout(() => {
  //   const str = this.$().text().trim().replace(/\s/g, '').substring(0, 200);
  //   assert.equal(str, '');
  //   done();
  // }, 400);
});
