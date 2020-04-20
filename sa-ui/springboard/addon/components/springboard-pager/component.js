import { action, computed, set } from '@ember/object';
import { tracked } from '@glimmer/tracking';
import Component from '@glimmer/component';
import { springboardPagerData, isPagerLeftDisabled, isPagerRightDisabled } from 'springboard/reducers/springboard/selectors';
import { setActiveLeads, setPagePosition } from 'springboard/actions/creators/springboard';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  springboardPagerData: springboardPagerData(state),
  defaultActiveLeads: state.springboard.defaultActiveLeads,
  pagerPosition: state.springboard.pagerPosition,
  isPagerLeftDisabled: isPagerLeftDisabled(state),
  isPagerRightDisabled: isPagerRightDisabled(state)
});

const dispatchToActions = {
  setActiveLeads,
  setPagePosition
};
class SpringboardPager extends Component {

  @tracked position = 0;
  showAddLeadAction = false;
  transition = 0;
  // default lead widget width
  slideWidth = 450;
  // default active widgets visibility
  @computed('defaultActiveLeads')
  get defaultActiveSlides() {
    return this.defaultActiveLeads;
  }

  @computed('pagerPosition')
  get position() {
    return this.pagerPosition;
  }

  @computed('springboardPagerData', 'defaultActiveLeads')
  get numberOfItems() {
    return this.springboardPagerData.map((item, i) => {
      return { ...item, isActive: i < this.defaultActiveLeads };
    });
  }
  set numberOfItems(value) {
    return value;
  }

  @action
  setup() {
    const w = window.innerWidth;
    this.actions.setActiveLeads(Math.floor(w / this.slideWidth));
  }

  @action
  moveRight() {
    this.actions.setPagePosition(++this.position);
    this.transition += this.slideWidth;
    this._doPageTransition();
  }

  @action
  moveLeft() {
    this.actions.setPagePosition(--this.position);
    this.transition -= this.slideWidth;
    this._doPageTransition();
  }
  @action
  pageAction(page) {
    if (page < this.position || page >= this.position + this.defaultActiveSlides) {
      if (page < this.position) {
        this.position = page;
        this.transition = this.position * this.slideWidth;
      } else {
        this.position = page - this.defaultActiveSlides + 1;
        this.transition = this.position * this.slideWidth;
      }
    }
    this.actions.setPagePosition(this.position);
    this._doPageTransition();
  }

  /**
   * Setting transition value to the right to the dom element.
   * @private
   */
  _doPageTransition() {
    const d = this.__resetData(this.springboardPagerData);
    this._setActiveSlide(d);
    set(this, 'numberOfItems', d);
    this.args.pagerAction(this.transition);
  }

  /**
   * Setting isActive value true for the range of elements from the current position.
   * @param data
   * @returns {*}
   * @private
   */
  _setActiveSlide(data) {
    for (let i = this.position; i < this.position + this.defaultActiveSlides; i++) {
      if (data[i]) {
        data[i] = { ...data[i], isActive: true };
      }
    }
    return data;
  }

  /**
   * All isActive flags set to false.
   * @param data
   * @returns {*}
   * @private
   */
  __resetData(data) {
    let d = data;
    d = d.map((item) => {
      return { ...item, isActive: false };
    });
    return d;
  }
}

export default connect(stateToComputed, dispatchToActions)(SpringboardPager);
