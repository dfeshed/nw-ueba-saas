import Component from '@glimmer/component';
import { inject as service } from '@ember/service'; // with polyfill
import { action } from '@ember/object';
import { tracked } from '@glimmer/tracking';


export default class CustomWidgetComponent extends Component {
  @service inViewport;

  @service deepLink;

  @tracked isLoadingData = true;

  element = null;

  isActive = false;

  data = null;

  totalItems = 0;

  get widgetData() {
    return { ...this.data };
  }

  @action
  didInsertNode(element, [instance]) {
    const viewportTolerance = { right: 220 };
    const { onEnter } = instance.inViewport.watchElement(element, { viewportTolerance });
    onEnter(instance.didEnterViewport.bind(instance));
  }

  @action
  navigateTo() {
    const { deepLink } = this.args.widget;
    if (deepLink) {
      this.deepLink.transition(deepLink);
    }
  }

  /**
   * Load the data only when component is visible
   */
  didEnterViewport() {
    if (!this.isActive && this.args.widget && this.args.fetchData) {
      this.isLoadingData = true;
      this.isActive = true;
      this.args.fetchData(this.args.widget).then((response) => {
        this.data = response.data;
        this.totalItems = response.data?.items?.length;
        this.isLoadingData = false;
      });
    }
  }

  willDestroy() {
    this.inViewport.stopWatching(this.element);
    const { observerAdmin } = this.inViewport;
    if (observerAdmin) {
      observerAdmin.instance.elementRegistry.destroyRegistry();
      observerAdmin.instance.registry.destroyRegistry();
    }
    super.willDestroy();
  }
}
