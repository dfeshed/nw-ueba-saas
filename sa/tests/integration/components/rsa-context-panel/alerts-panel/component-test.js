  import { moduleForComponent, test } from 'ember-qunit';
  import hbs from 'htmlbars-inline-precompile';

  moduleForComponent('rsa-context-panel/alerts-panel', 'Integration | Component | rsa context panel/alerts panel', {
    integration: true
  });

  test('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

    this.render(hbs`{{rsa-context-panel/alerts-panel}}`);

    assert.equal('', '');
  });
