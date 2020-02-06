import { createSelector } from 'reselect';

const _springboards = (state) => state.springboard.springboards;

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
