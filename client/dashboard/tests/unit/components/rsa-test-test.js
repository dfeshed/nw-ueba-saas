import { moduleForComponent, test } from 'ember-qunit';

moduleForComponent('rsa-test', 'Unit | Component | rsa-test', {
    // Specify the other units that are required for this test
    // needs: ['component:foo', 'helper:bar'],
    unit: true
});

test('it renders', function (assert) {
    assert.expect(2);

    // Creates the component instance
    var component = this.subject();
    assert.equal(component._state, 'preRender');

    // Renders the component to the page
    this.render();
    assert.equal(component._state, 'inDOM');
});

test('clicking the button increments the count', function (assert) {
    assert.expect(2);

    var component = this.subject();
    this.render();

    assert.equal(component.get('count'), 0);

    this.$().find('button').click();

    assert.equal(component.get('count'), 1);
});
