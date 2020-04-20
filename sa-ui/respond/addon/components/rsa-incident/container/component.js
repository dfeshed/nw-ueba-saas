/* global addResizeListener, removeResizeListener */

import Component from '@ember/component';
import { run } from '@ember/runloop';
import { connect } from 'ember-redux';

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
    this._resizeElement = this.element.querySelector('.js-entities-rectangle');
    if (!this._resizeElement) {
      return;
    }
    this._resizeCallback = () => {
      this._measureEntitiesRectangle();
    };
    addResizeListener(this._resizeElement, this._resizeCallback);
    this._resizeCallback();
  },

  willDestroyElement() {
    // Teardown resize event listener.
    if (this._resizeElement) {
      removeResizeListener(this._resizeElement, this._resizeCallback);
      this._resizeElement = null;
    }
    this._super(...arguments);
  },

  _measureEntitiesRectangle() {
    run.debounce(() => {
      this.set('entitiesRectangle', {
        top: this._resizeElement.offsetTop,
        left: this._resizeElement.offsetLeft,
        width: this._resizeElement.clientWidth,
        height: this._resizeElement.clientHeight
      });
    }, 100, true);
  }
});

export default connect(stateToComputed)(Incident);
