import Ember from 'ember';
import { connect } from 'ember-redux';

/* global addResizeListener */
/* global removeResizeListener */

const {
  Component,
  run
} = Ember;

const stateToComputed = ({
  respond: {
    incidents: {
      isTransactionUnderway
    },
    storyline: {
      storylineStatus,
      storyline
    },
    incident: {
      id,
      isShowingTasksAndJournal,
      hideViz
    }
  }
}) => ({
  isShowingTasksAndJournal,
  storylineStatus,
  storyline,
  isTransactionUnderway,
  hideViz,
  incidentId: id
});

const Incident = Component.extend({
  tagName: 'article',
  classNames: ['rsa-incident-container'],
  classNameBindings: ['isTransactionUnderway:transaction-in-progress', 'isShowingTasksAndJournal'],
  storylineStatus: null,
  storyline: null,
  isTransactionUnderway: false,
  hideViz: false,

  /**
   * Rectangle object that specifies the current location & size of the "entities rectangle" DOM element.
   *
   * @type {{ top: number, left: number, width: number, height: number }}
   * @readonly
   * @public
   */
  entitiesRectangle: null,

  didInsertElement() {
    this._super(...arguments);
    run.schedule('afterRender', this, this.afterRender);
  },

  afterRender() {
    // Attach resize event listener to entities rectangle.
    this._resizeElement = this.$('.js-entities-rectangle');
    if (!this._resizeElement[0]) {
      return;
    }
    this._resizeCallback = () => {
      this._measureEntitiesRectangle();
    };
    addResizeListener(this._resizeElement[0], this._resizeCallback);
    this._resizeCallback();
  },

  willDestroyElement() {
    // Teardown resize event listener.
    if (this._resizeElement[0]) {
      removeResizeListener(this._resizeElement[0], this._resizeCallback);
      this._resizeElement = null;
    }
    this._super(...arguments);
  },

  _measureEntitiesRectangle() {
    run.debounce(() => {
      this.set('entitiesRectangle', {
        top: this._resizeElement[0].offsetTop,
        left: this._resizeElement[0].offsetLeft,
        width: this._resizeElement[0].clientWidth,
        height: this._resizeElement[0].clientHeight
      });
    }, 100, true);
  }
});

export default connect(stateToComputed)(Incident);