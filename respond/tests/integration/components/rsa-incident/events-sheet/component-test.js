import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import Immutable from 'seamless-immutable';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';

let setState;
const trim = (text) => text.replace(/\s\s+/g, ' ').trim();

moduleForComponent('rsa-incident/events-sheet', 'Integration | Component | Incident Events Sheet', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(Immutable.from(state));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('should display selectedIndicatorName when incident selection is truthy and of type storyPoint', function(assert) {
  setState({
    respond: {
      incident: {
        selection: {
          type: 'storyPoint',
          ids: [ 'alert1' ]
        }
      },
      storyline: {
        storyline: [
          {
            id: 'alert1',
            alert: { numEvents: 10, name: 'foo' },
            items: [
              {
                id: 'abc123'
              }
            ]
          }
        ]
      }
    }
  });

  this.render(hbs`{{rsa-incident/events-sheet}}`);

  const $el = this.$('[test-id=indicatorLabel]');
  assert.equal($el.length, 1, 'Expected to find indicator label element in DOM.');
  assert.equal(trim($el.text()), 'in foo', 'Expected selected indicator alert name.');
});

test('should display selectedIndicatorName and indicator count when incident selection is truthy but available ids > 1', function(assert) {
  setState({
    respond: {
      incident: {
        selection: {
          type: 'storyPoint',
          ids: [ 'alert1', 'alert2' ]
        }
      },
      storyline: {
        storyline: [
          {
            id: 'alert1',
            alert: { numEvents: 10, name: 'foo' },
            items: [
              {
                id: 'abc123'
              }
            ]
          }
        ]
      }
    }
  });

  this.render(hbs`{{rsa-incident/events-sheet}}`);

  const $el = this.$('[test-id=indicatorLabel]');
  assert.equal($el.length, 1, 'Expected to find indicator label element in DOM.');
  assert.equal(trim($el.text()), 'in 2 indicators', 'Expected indicator count.');
});

test('should not display selectedIndicatorName when incident selection is truthy but storyline is empty array', function(assert) {
  setState({
    respond: {
      incident: {
        selection: {
          type: 'storyPoint',
          ids: [ 'alert1' ]
        }
      },
      storyline: {
        storyline: []
      }
    }
  });

  this.render(hbs`{{rsa-incident/events-sheet}}`);

  const $el = this.$('[test-id=indicatorLabel]');
  assert.equal($el.length, 0, 'Expected not to find indicator label element in DOM.');
});

test('should not display selectedIndicatorName when incident selection is truthy but storyline is undefined', function(assert) {
  setState({
    respond: {
      incident: {
        selection: {
          type: 'storyPoint',
          ids: [ 'alert1' ]
        }
      },
      storyline: {
        storyline: undefined
      }
    }
  });

  this.render(hbs`{{rsa-incident/events-sheet}}`);

  const $el = this.$('[test-id=indicatorLabel]');
  assert.equal($el.length, 0, 'Expected not to find indicator label element in DOM.');
});
