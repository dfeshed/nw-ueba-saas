import { createSelector } from 'reselect';

const _springboards = (state) => state.springboard.springboards;
const _pagerPosition = (state) => state.springboard.pagerPosition;
const _defaultActiveLeads = (state) => state.springboard.defaultActiveLeads;

export const activeSpringboardId = (state) => state.springboard.activeSpringboardId;

/**
 * Filtering the springboard based id
 */
export const springboardConfig = createSelector(
  [activeSpringboardId, _springboards],
  (activeSpringboardId, springboards) => {
    if (activeSpringboardId && springboards.length) {
      const [config] = springboards.filter((springboard) => {
        return springboard.id === activeSpringboardId;
      });
      return config;
    }
    return null;
  }
);

export const springboardWidgets = createSelector(
  [springboardConfig],
  (springboardConfig) => {
    if (springboardConfig) {
      return springboardConfig.widgets;
    }
    return null;
  }
);
export const springboardPagerData = createSelector(
  [springboardConfig],
  (springboardConfig) => {
    if (springboardConfig) {
      const widgets = springboardConfig.widgets.asMutable();
      return widgets.map((widgetConfig) => {
        const { widget } = widgetConfig;
        if (widget) {
          const { name, leadCount } = widget;
          return { name, leadCount };
        }
        return {};
      });
    }
    return null;
  }
);
export const isPagerLeftDisabled = createSelector(
  [springboardPagerData, _pagerPosition, _defaultActiveLeads],
  (springboardPagerData, pagerPosition, defaultActiveLeads) => {
    return springboardPagerData.length <= defaultActiveLeads || pagerPosition === 0;
  }
);
export const isPagerRightDisabled = createSelector(
  [springboardPagerData, _pagerPosition, _defaultActiveLeads],
  (springboardPagerData, pagerPosition, defaultActiveLeads) => {
    return springboardPagerData.length <= defaultActiveLeads || pagerPosition === springboardPagerData.length - defaultActiveLeads;
  }
);
